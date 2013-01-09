package com.ader.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ader.Bookmark;
import com.ader.DaisyBook;
import com.ader.R;
import com.ader.fulltext.FullTextDocumentFactory;
import com.ader.smil.SmilFile;
import com.ader.smil.TextElement;
import com.ader.utilities.Logging;
import com.google.marvin.widget.GestureOverlay;
import com.google.marvin.widget.GestureOverlay.Gesture;
import com.google.marvin.widget.GestureOverlay.GestureListener;

public class DaisyPlayer extends Activity implements OnCompletionListener {

	public static final String DAISY_BOOK_KEY = "com.ader.DaisyBook";
	
	private static final int MSECS_TO_JUMP = 10000;
	private static final String AUDIO_OFFSET = "Offset";
	private static final String IS_THE_BOOK_PLAYING = "playing";
	private static final String TAG = "DaisyPlayer";
	private DaisyBook book;
	private MediaPlayer player;
	private GestureOverlay gestureOverlay;
	private TextView mainText;
	private TextView statusText;
	private TextView depthText;
	private int audioOffset ;
	private SmilFile smilfile = new SmilFile();
	private Bookmark autoBookmark;

	private TextView contentsToRead;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		retrieveTheBookToPlay();
		loadAutoBookmark();
		activateGesture();
		prepareTheAudioPlayer();
		play();
	}

	@Override
	protected void onDestroy() {
		// Let's stop playing the book if the user presses back, etc.
		stop();
		player.release();
		super.onDestroy();
	}

	/**
	 * Called by Android when the user selects the Menu (button).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.playermenu, menu);
		return(super.onCreateOptionsMenu(menu));
	}
	
	/**
	 * Called by Android when the user selects a Menu item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.player_instructions:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			
			builder
				.setTitle(R.string.player_instructions_description)
				.setMessage(R.string.player_instructions)
				.setPositiveButton(R.string.close_instructions, null)
				.show();
			break;
		
		case R.id.return_to_homescreen:
			DaisyPlayer.this.finish();
		}
		
		return true;
	}
	
	/**
	 * This allows us to support bluetooth a2dp devices such as the BT3030.
	 * 
	 * I was surprised how easy the support was to add, once I'd spent an hour
	 * trying to address a related problem, of supporting a Tekla Shield
	 * controller. (I've yet to fathom out how to support this device).
	 * 
	 * The code that helped me is AudioPreview.java which seems to be part of
	 * the default Android music player. Lots of clues and redirection got me
	 * to this file...
	 * 
	 * Note: I noticed the playback and performance when using a bluetooth a2dp
	 * device can collapse, causing the audio to be broken into tiny segments
	 * interspersed with longer pauses. LogCat contains lots of messages like:
	 * AudioFlinger   write blocked for ... msecs. This seems to be a known
	 * issue, I doubt I can fix in my code. The workaround seems to be to
	 * disable and then re-enable bluetooth on the Android device.
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
		case KeyEvent.KEYCODE_DPAD_LEFT:
			goUp();
			return true;
			
		case KeyEvent.KEYCODE_6:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			goDown();
			return true;
			
		case KeyEvent.KEYCODE_B:
		case KeyEvent.KEYCODE_DPAD_UP:
			gotoPreviousSection();
			return true;
			
		case KeyEvent.KEYCODE_F:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			gotoNextSection();
			return true;
			
		case KeyEvent.KEYCODE_P:
			togglePlay();
			return true;
			
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			togglePlay();
			return true;
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			gotoNextSection();
			return true;
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			gotoPreviousSection();
			return true;
		default:
				Logging.logInfo(TAG, "Unhandled keyboard event: " + keyCode);
				break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Called by the Media Player when the current audio has finished playing.
	 * 
	 * Note: This needs to be public so the Media Player callback can find it.
	 */
	public void onCompletion(MediaPlayer mp) {
		Logging.logInfo(TAG, "onCompletion called.");
		if (book.nextSection(false)) {
			Logging.logInfo(TAG, "PLAYING section: " + book.getDisplayPosition() + " " +
					book.current().getText());
			mainText.setText(book.current().getText());
			// reset the audio Offset (used on device rotation)
			audioOffset = 0;
			play();
		} else {
			// Let's tell the users the book is finished.
			statusText.setText(R.string.finished_reading_book);
			stop();
		}
	}

	/**
	 * Loads the automatically created bookmark.
	 * 
	 * Extracted from DaisyBook.
	 * 
	 * This bookmark keeps track of where the user is in this book. If it
	 * doesn't exist, e.g. if this is the first time the user has opened this
	 * book, then the bookmark will be created once the user starts reading the
	 * book.
	 */
	public void loadAutoBookmark() {
		autoBookmark = Bookmark.getInstance(book.getPath());
		audioOffset = autoBookmark.getPosition();
		// Tell the book where it needs to start from
		// TODO 20110818 (jharty): Cleanup once the bookmark code doesn't use the NCC index.
		try {
			book.goTo(autoBookmark.getNccIndex());
		} catch (IndexOutOfBoundsException ioobe) {
			Logging.logSevereWarning(TAG, "Bookmark seems to point to an invalid value.", ioobe);
			// For now we'll delete the automatic bookmark
			// TODO 20110828 (jharty): Provide a UI for handling invalid bookmarks
			autoBookmark.deleteAutomaticBookmark();
			book.goTo(0);
		}
	}

	/**
	 * open the current Smil file to read to the user. 
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	private String openSmilToRead() 
			throws FileNotFoundException, IOException, SAXException, ParserConfigurationException {
		String nextFileToRead = book.getCurrentSmilFilename();
		Logging.logInfo(TAG, "Open SMIL file: " + nextFileToRead);
		smilfile.load(nextFileToRead);
		
		// Only update the automatic bookmark if we open the file correctly.
		autoBookmark.updateAutomaticBookmark(smilfile);
		
		// TODO 20110824 (jharty): Remove this ugly dependency once I modify the SMIL code to return the filename
		return autoBookmark.getFilename();
	}

	private void play() {
		Logging.logInfo(TAG, "play");
		player.reset();
		int duration = Toast.LENGTH_LONG;

		try {
			String fileToRead = openSmilToRead();
			read(fileToRead);
			return;
		} catch (FileNotFoundException fnfe) {
			reportFileNotFoundException(duration, fnfe);
		} catch (IOException ioe) {
			reportIOException(duration, ioe);
		} catch (SAXException saxe) {
			reportSAXException(duration, saxe);
		} catch (ParserConfigurationException pce) {
			reportParserConfigurationException(duration, pce);
		}
	}

	private void reportParserConfigurationException(int duration,
			ParserConfigurationException pce) {
		CharSequence localizedMessage = pce.getLocalizedMessage();
		
		Toast toast = Toast.makeText(this, localizedMessage, duration);
		toast.show();
		
		int titleIDToDisplay = R.string.serious_problem_found;
		displayAlertThenFinishThisActivity(localizedMessage, titleIDToDisplay);
	}

	private void reportSAXException(int duration, SAXException saxe) {
		String localizedMessage = saxe.getLocalizedMessage();
		CharSequence text = getString(R.string.cannot_open_book) + " " 
			+ localizedMessage;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
		
		int titleIDToDisplay = R.string.serious_problem_found;
		displayAlertThenFinishThisActivity(text, titleIDToDisplay);
	}

	/**
	 * Helper method to display an Alert before finishing (closing) this activity.
	 * 
	 * We don't expect to be able to recover from this error when reading the
	 * current book.
	 * @param messageToDisplay the Message to display
	 * @param titleIDToDisplay the ID of the Title to display
	 */
	private void displayAlertThenFinishThisActivity(CharSequence messageToDisplay,
			int titleIDToDisplay) {
		AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
		explainProblem
		.setTitle(titleIDToDisplay)
		.setMessage(messageToDisplay)
		.setPositiveButton(R.string.close_instructions, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				DaisyPlayer.this.finish();
			}
		})
		.show();
	}

	private void reportIOException(int duration, IOException ioe) {
		Toast toast;
		CharSequence text = getString(R.string.cannot_open_book) + ioe.getLocalizedMessage();
		toast = Toast.makeText(this, text, duration);
		toast.show();			
		
		AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
		explainProblem
		.setTitle(R.string.permission_problem_opening_a_file)
		.setMessage(ioe.getLocalizedMessage())
		.setPositiveButton(R.string.close_instructions, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				DaisyPlayer.this.finish();
			}
		})
		.show();
	}

	private void reportFileNotFoundException(int duration,
			FileNotFoundException fnfe) {
		Toast toast;
		
		CharSequence text = getString(R.string.cannot_open_book_a_file_is_missing) + fnfe.getLocalizedMessage();

		// Now record the problem in the log, then tell the user...
		Log.w(TAG, text.toString());
		toast = Toast.makeText(this, text, duration);
		toast.show();
		
		AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
		explainProblem
		.setCancelable(false)
		.setTitle(R.string.unable_to_open_file)
		.setMessage(text)
		.setPositiveButton(R.string.close_instructions, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		})
		.show();
	}

	/**
	 * Start reading the current section of the book
	 * @throws FileNotFoundException 
	 */
	private void read(String fileToRead) throws FileNotFoundException {

		// TODO(jharty): Find a practical way to format these messages for i18n and l10n
		depthText.setText("Depth " + book.getCurrentDepthInDaisyBook() + " of " + book.getMaximumDepthInDaisyBook());
		mainText.setText(getText(R.string.reading_message) + " " + book.current().getText());		
		
		if (smilfile.hasAudioSegments()) {
			// Note: Allow Java Garbage Collection to close the file.
			File f = new File(fileToRead);
			if (!(f.exists() && f.canRead())) {
				// TODO(jharty): Add a localised message to advise users
				// to upload a valid book. I could also provide a book
				// validation tool at some point.
				Logging.logInfo(TAG, "File Not Available: " + fileToRead);
				throw new FileNotFoundException(fileToRead);
			}

			Logging.logInfo(TAG, "Start playing " + fileToRead + " " + audioOffset);
			try {
				player.setDataSource(fileToRead);
				player.prepare();
			} catch (IOException e) {
				throw new RuntimeException(fileToRead + "\n" + e.getLocalizedMessage());
			}
			// TODO(jharty): I'm not sure if the following helps; keep for now.
			player.setScreenOnWhilePlaying(true);
			
			statusText.setText(getText(R.string.playing_message) + "...");
			player.seekTo(audioOffset);
			player.start();
		} else if (smilfile.hasTextSegments()) {
			// TODO(jharty): add TTS to speak the text section
			// Note: we need to decide how to handle things like \n
			// For now, perhaps we can simply display the text in a new view.
			Logging.logInfo(TAG, "We need to read the text from: " + fileToRead);
			statusText.setTextSize(11.0f);
			// TODO 20110828 (jharty): This is temporary while I'm trying to implement basic support
			statusText.setText("Currently you cannot scroll if the text exceeds the screen.");

			// TODO 20110904 (jharty): This is the simplest thing that probably wouldn't work. Fix me...
			FullTextDocumentFactory fullTextFactory = new FullTextDocumentFactory();
			try {
				Document doc = fullTextFactory.createDocument(fileToRead);
				StringBuilder html = new StringBuilder(); 
				
				Logging.logInfo(TAG, "Found [" + smilfile.getTextSegments().size() + "] text segments.");
				for (TextElement e: smilfile.getTextSegments()) {
					String id = e.getSrc().split("#")[1];
					Element element = doc.getElementById(id);
					Logging.logInfo(TAG, String.format("...text segment [%s].", id));
					
					// TODO 20110904 (jharty): This doesn't seem sufficient for 1.6 devices which don't display all the contents, find a workable algorithm
					// for the 1.6 devices, clean HTML seems to be displayed correctly e.g. 10 lines of computer generated stuff.
					// However the text read from the DAISY book is often incomplete / missing. I suspect the <strong> tag is a factor.
					// I will create some separate tests to try and prove or disprove my ideas.
					Elements anchors = doc.select("a");
					for (Element cleanme: anchors) {
						TextNode text = new TextNode(cleanme.text(), "");
						cleanme.replaceWith(text);
					}
					
					String htlmExtract = element.html();
					html.append(htlmExtract + "<br/>");
				}
				
				Spanned content = Html.fromHtml(html.toString());
				contentsToRead.setText(content);
			} catch (IOException ioe) {
				// TODO 20110904 (jharty): Decide how to alert the user
			}
		}
	}
    
	/**
	 * Called when external actions occur e.g. when the audio has finished.
	 */
	public void stop() {
		player.pause();
		int currentPosition = player.getCurrentPosition();
		autoBookmark.setPosition(currentPosition);
		autoBookmark.setNccIndex(book.getCurrentIndex());
		player.reset();
		if (autoBookmark.getFilename() != null) {
			// We only save the bookmark if there's a valid file, problems e.g.
			// reading a smil file might mean the bookmark hasn't been assigned.
			autoBookmark.save(book.getPath() + "auto.bmk");
		} else {
			Logging.logInfo(TAG, "No filename, so we didn't save the auto-bookmark");
		}
	}

	/**
	 * Toggles the Media Player between Play and Pause states.
	 */
	public void togglePlay() {
		Logging.logInfo(TAG, "togglePlay called.");
		if (player.isPlaying()) {
			statusText.setText(getText(R.string.paused_message));
			player.pause();
		} else {
			statusText.setText(getText(R.string.playing_message));
			player.start();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		audioOffset = player.getCurrentPosition();
		Logging.logInfo(TAG, "Length in media player is: " + audioOffset);
		autoBookmark.setPosition(audioOffset);

		savedInstanceState.putBoolean(IS_THE_BOOK_PLAYING, player.isPlaying());
		savedInstanceState.putInt(AUDIO_OFFSET, audioOffset);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Boolean isPlaying = savedInstanceState.getBoolean(IS_THE_BOOK_PLAYING, true);
		Logging.logInfo(TAG, "Offset at start of onRestoreInstanceState is: " + audioOffset);
		audioOffset = savedInstanceState.getInt(AUDIO_OFFSET, 0);
		Logging.logInfo(TAG, "Offset after retrieving saved offset value is: " + audioOffset);
		player.seekTo(audioOffset);
		if (!isPlaying) {
			statusText.setText(getText(R.string.paused_message) + "...");
			player.pause();
		} else {
			player.start();
		}
		

	}
	
	private void activateGesture() {
		setContentView(R.layout.daisyplayerframe);
		depthText = (TextView) findViewById(R.id.depthText);
		mainText = (TextView) findViewById(R.id.mainText);
        statusText = (TextView) findViewById(R.id.statusText);
        contentsToRead = (TextView) findViewById(R.id.contentsToRead);
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.daisyPlayerLayout);
		gestureOverlay = new GestureOverlay(this, gestureListener);
		frameLayout.addView(gestureOverlay);
		setContentView(frameLayout);
	}

	private GestureListener gestureListener = new GestureListener() {

		private long startTime;
		 
		public void onGestureStart(int g) {
			startTime = System.currentTimeMillis();
			Logging.logInfo(TAG, "onGestureStart @" + startTime + " Value: " + g);
		}

		public void onGestureChange(int g) {
			long interimValue = System.currentTimeMillis() - startTime;
			Logging.logInfo(TAG, "onGestureChange. Duration is: " + interimValue + " Value: " + g);
		}

		public void onGestureFinish(int g) {
			long timeTaken = System.currentTimeMillis() - startTime;
			Logging.logInfo(TAG, "onGestureFinish. Duration is: " + timeTaken + " Value: " + g);
			
			if (g == Gesture.CENTER) {
				togglePlay();
			} else if (g == Gesture.UP) {
				gotoPreviousSection();
			} else if (g == Gesture.DOWN) {
				gotoNextSection();
			} else if (g == Gesture.LEFT) {
				goUp();
			} else if (g == Gesture.RIGHT) {
				goDown();
			} else if (g == Gesture.DOWNLEFT) {
				Logging.logInfo(TAG, "Rewind .");
				// TODO (jharty): This is experimental code and needs refining
				// TODO (jharty): Add ability for user to specify the interval.
				int newValue = player.getCurrentPosition();
				Logging.logInfo(TAG, "Current Offset: " + newValue);
				newValue -= MSECS_TO_JUMP; 
				if (newValue < 0) {
					// Consider jumping to previous track
					newValue = 0;
				}
				
				Logging.logInfo(TAG, "Seeking to: " + newValue);
				player.seekTo(newValue);
			} else if (g == Gesture.DOWNRIGHT) {
				Logging.logInfo(TAG, "Fast forward");
				// TODO (jharty): This is experimental code and needs refining
				// TODO (jharty): Add ability for user to specify the interval.
				int newValue = player.getCurrentPosition();
				Logging.logInfo(TAG, "Current Offset: " + newValue);
				newValue += MSECS_TO_JUMP; 
				int duration = player.getDuration();
				if (newValue >= duration) {
					// Consider jumping to next track
					newValue = duration;
				}

				Logging.logInfo(TAG, "Seeking to: " + newValue);
				player.seekTo(newValue);
			}
		}


	};
	
	private int goDown() {
		// TODO(jharty): Localize all the recently added hardcoded text e.g. here!
		int levelSetTo = book.incrementSelectedLevel();
		Logging.logInfo(TAG, "Incremented Level to: " + levelSetTo);
		depthText.setText("Depth " + levelSetTo + " of " + book.getMaximumDepthInDaisyBook());
		return levelSetTo;
	}
	
	private void goUp() {
		// TODO(jharty): Localize all the recently added hardcoded text e.g. here!
		int levelSetTo = book.decrementSelectedLevel();
		Logging.logInfo(TAG, "Decremented Level to: " + levelSetTo);
		depthText.setText("Depth " + levelSetTo + " of " + book.getMaximumDepthInDaisyBook());
	}
	
	/**
	 * Goto the next section in the audio book.
	 * 
	 * This command has no effect if we are at the end of the book.
	 * This is one of 2 helper methods, extracted so we can support a2dp
	 * and other key-based controllers.
	 */
	private void gotoNextSection() {
		if (book.nextSection(true)) {
			audioOffset = 0;
			play();
		}
	}
	
	/**
	 * Go to the previous section in the audio book.
	 * 
	 * This command has no effect if we are at the start of the book.
	 */
	private void gotoPreviousSection() {
		//TODO 20110828 (jharty): Consider going to the start of the CURRENT section if we've finished reading the current book.
		// Currently the player goes to the beginning of the previous section. I don't want to
		// implement this code as I'd probably use the state of the media player to determine
		// we're at the end of the book. I'd prefer to wait until we redesign this code.
		if (book.previousSection()) {
			audioOffset = 0;
			play();
		}
	}
	
	/**
	 * Prepares the Media Player to be able to play the audio files.
	 * 
	 * Utility method to provide the same level of abstraction in the onCreate() method.
	 */
	private void prepareTheAudioPlayer() {
		player = new MediaPlayer();
		player.setOnCompletionListener(this);
	}

	/**
	 * Utility Method that Retrieves the book to play.
	 * 
	 * The Calling Program passes the book object, programmatically.
	 */
	private void retrieveTheBookToPlay() {
		book = (DaisyBook) getIntent().getSerializableExtra(DAISY_BOOK_KEY);
	}


	/**
	 * @return the Localized Status Text being displayed on screen.
	 */
	public String whatIsThePlayerStatus() {
		//TODO 20110816 (jharty/amarcano): Temporary method while we explore some refactorings.
		return statusText.getText().toString();
	}

	/**
	 * Allows our tests to detect whether the Audio book is being played.
	 * @return true if the audio is being played, else false.
	 */
	public boolean isPlayingAudio() {
		// TODO 20110818 (jharty): Second temporary method while we explore some refactorings.
		return player.isPlaying();
	}
}
