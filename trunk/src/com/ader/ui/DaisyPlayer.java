package com.ader.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ader.Bookmark;
import com.ader.OldDaisyBookImplementation;
import com.ader.R;
import com.ader.Util;
import com.ader.smil.SmilFile;
import com.google.marvin.widget.GestureOverlay;
import com.google.marvin.widget.GestureOverlay.Gesture;
import com.google.marvin.widget.GestureOverlay.GestureListener;

public class DaisyPlayer extends Activity implements OnCompletionListener {

	private static final int MSECS_TO_JUMP = 10000;
	private static final String AUDIO_OFFSET = "Offset";
	private static final String IS_THE_BOOK_PLAYING = "playing";
	private static final String TAG = "DaisyPlayer";
	private OldDaisyBookImplementation book;
	private MediaPlayer player;
	private GestureOverlay gestureOverlay;
	private TextView mainText;
	private TextView statusText;
	private TextView depthText;
	private int audioOffset ;
	private SmilFile smilfile = new SmilFile();
	private Bookmark autoBookmark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		book = (OldDaisyBookImplementation) getIntent().getSerializableExtra(
				"com.ader.DaisyBook");
		try {
			loadAutoBookmark();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		activateGesture();
		player = new MediaPlayer();
		player.setOnCompletionListener(this);
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
				Util.logInfo(TAG, "Unhandled keyboard event: " + keyCode);
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
		Util.logInfo(TAG, "onCompletion called.");
		if (book.nextSection(false)) {
			Util.logInfo(TAG, "PLAYING section: " + book.getDisplayPosition() + " " +
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
	 * @throws IOException If there is a problem opening the file representing
	 * the bookmark.
	 */
	public void loadAutoBookmark() throws IOException  {
		autoBookmark = Bookmark.getInstance(book.getPath());
		audioOffset = autoBookmark.getPosition();
		
		/*
		 * OK I need to get rid of telling the book the ncc Index to use!
		 * Instead I need to tell it which smil file to load. Next step is to
		 * see if I can simply remove the call to setCurrentIndex(...) and
		 * modify goTo so it navigates by smil file.
		 */
		// TODO (jharty): Tell the book where it needs to start from
		book.setCurrentIndex(autoBookmark.getNccIndex());
		// FIXME: We need to cleanly tell the book which item to return. The
		// following calls will do for now, but need to be fixed / replaced ASAP
		book.goTo(book.current());
	}

	/**
	 * open the current Smil file. 
	 * 
	 * Sets the auto bookmark to the contents in the current Smil file. 
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	private void openSmil() 
			throws FileNotFoundException, IOException, SAXException, ParserConfigurationException {
		String smilfilename = book.getCurrentSmilFilename();
		Util.logInfo(TAG, "Open SMIL file: " + smilfilename);
		smilfile.open(smilfilename);
		autoBookmark.updateAutomaticBookmark(smilfile);
	}


	private void play() {
		Util.logInfo(TAG, "play");
		player.reset();
		int duration = Toast.LENGTH_LONG;

		try {
			openSmil();
			read();
			return;
		} catch (FileNotFoundException fnfe) {
			reportFileNotFoundException(duration, fnfe);
		} catch (IOException ioe) {
			reportIOException(duration, ioe);
		} catch (SAXException saxe) {
			reportSAXException(duration, saxe);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void reportSAXException(int duration, SAXException saxe) {
		Toast toast;
		CharSequence text = getString(R.string.cannot_open_book) + " " 
			+ saxe.getLocalizedMessage();
		toast = Toast.makeText(this, text, duration);
		toast.show();
		
		AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
		explainProblem
		.setTitle(R.string.serious_problem_found)
		.setMessage(saxe.getLocalizedMessage())
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
	private void read() throws FileNotFoundException {
		Util.logInfo(TAG, String.format(
				"Reading from SMILfile[%s] NCC index[%d] offset[%d]",
				autoBookmark.getFilename(), autoBookmark.getNccIndex(), autoBookmark.getPosition()));

		// TODO(jharty): Find a practical way to format these messages for i18n and l10n
		depthText.setText("Depth " + book.getCurrentDepthInDaisyBook() + " of " + book.getMaximumDepthInDaisyBook());
		mainText.setText(getText(R.string.reading_message) + " " + book.current().getText());

		if (smilfile.hasAudioSegments()) {
			// Note: Allow Java Garbage Collection to close the file.
			File f = new File(autoBookmark.getFilename());
			if (!(f.exists() && f.canRead())) {
				// TODO(jharty): Add a localised message to advise users
				// to upload a valid book. I could also provide a book
				// validation tool at some point.
				Util.logInfo(TAG, "File Not Available: " + autoBookmark.getFilename());
				throw new FileNotFoundException(autoBookmark.getFilename());
			}

			Util.logInfo(TAG, "Start playing " + autoBookmark.getFilename() + " " + audioOffset);
			try {
				player.setDataSource(autoBookmark.getFilename());
				player.prepare();
			} catch (IOException e) {
				throw new RuntimeException(autoBookmark.getFilename() 
						+ "\n" + e.getLocalizedMessage());
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
			Util.logInfo(TAG, "We need to read the text from: " + autoBookmark.getFilename());

			// For now, here is some information for the user. Perhaps I could
			// add a way to automatically send a request e.g. by email?

			// TODO(jharty): Test whether the status is visible at this size.
			statusText.setTextSize(11.0f);
			statusText.setText(R.string.text_content_not_supported_yet);
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
			Util.logInfo(TAG, "No filename, so we didn't save the auto-bookmark");
		}
	}

	/**
	 * Toggles the Media Player between Play and Pause states.
	 */
	public void togglePlay() {
		Util.logInfo(TAG, "togglePlay called.");
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
		Util.logInfo(TAG, "Length in media player is: " + audioOffset);
		autoBookmark.setPosition(audioOffset);

		savedInstanceState.putBoolean(IS_THE_BOOK_PLAYING, player.isPlaying());
		savedInstanceState.putInt(AUDIO_OFFSET, audioOffset);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Boolean isPlaying = savedInstanceState.getBoolean(IS_THE_BOOK_PLAYING, true);
		Util.logInfo(TAG, "Offset at start of onRestoreInstanceState is: " + audioOffset);
		audioOffset = savedInstanceState.getInt(AUDIO_OFFSET, 0);
		Util.logInfo(TAG, "Offset after retrieving saved offset value is: " + audioOffset);
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
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.daisyPlayerLayout);
		gestureOverlay = new GestureOverlay(this, gestureListener);
		frameLayout.addView(gestureOverlay);
		setContentView(frameLayout);
	}

	private GestureListener gestureListener = new GestureListener() {

		private long startTime;
		 
		public void onGestureStart(int g) {
			startTime = System.currentTimeMillis();
			Util.logInfo(TAG, "onGestureStart @" + startTime + " Value: " + g);
		}

		public void onGestureChange(int g) {
			long interimValue = System.currentTimeMillis() - startTime;
			Util.logInfo(TAG, "onGestureChange. Duration is: " + interimValue + " Value: " + g);
		}

		public void onGestureFinish(int g) {
			long timeTaken = System.currentTimeMillis() - startTime;
			Util.logInfo(TAG, "onGestureFinish. Duration is: " + timeTaken + " Value: " + g);
			
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
				Util.logInfo(TAG, "Rewind .");
				// TODO (jharty): This is experimental code and needs refining
				// TODO (jharty): Add ability for user to specify the interval.
				int newValue = player.getCurrentPosition();
				Util.logInfo(TAG, "Current Offset: " + newValue);
				newValue -= MSECS_TO_JUMP; 
				if (newValue < 0) {
					// Consider jumping to previous track
					newValue = 0;
				} else {
					Util.logInfo(TAG, "Seeking to: " + newValue);
					player.seekTo(newValue);
				}
			} else if (g == Gesture.DOWNRIGHT) {
				Util.logInfo(TAG, "Fast forward");
				// TODO (jharty): This is experimental code and needs refining
				// TODO (jharty): Add ability for user to specify the interval.
				int newValue = player.getCurrentPosition();
				Util.logInfo(TAG, "Current Offset: " + newValue);
				newValue += MSECS_TO_JUMP; 
				int duration = player.getDuration();
				if (newValue >= duration) {
					// Consider jumping to next track
					newValue = duration;
				} else {
					Util.logInfo(TAG, "Seeking to: " + newValue);
					player.seekTo(newValue);
				}
			}
		}


	};
	
	private int goDown() {
		// TODO(jharty): Localize all the recently added hardcoded text e.g. here!
		int levelSetTo = book.incrementSelectedLevel();
		Util.logInfo(TAG, "Incremented Level to: " + levelSetTo);
		depthText.setText("Depth " + levelSetTo + " of " + book.getMaximumDepthInDaisyBook());
		return levelSetTo;
	}
	
	private void goUp() {
		// TODO(jharty): Localize all the recently added hardcoded text e.g. here!
		int levelSetTo = book.decrementSelectedLevel();
		Util.logInfo(TAG, "Decremented Level to: " + levelSetTo);
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
		if (book.previousSection()) {
			audioOffset = 0;
			play();
		}
	}
}
