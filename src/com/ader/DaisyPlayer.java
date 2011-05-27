package com.ader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.marvin.widget.TouchGestureControlOverlay;
import com.google.marvin.widget.TouchGestureControlOverlay.Gesture;
import com.google.marvin.widget.TouchGestureControlOverlay.GestureListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DaisyPlayer extends Activity implements OnCompletionListener {

	private static final String AUDIO_OFFSET = "Offset";
	private static final String IS_THE_BOOK_PLAYING = "playing";
	private static final String TAG = "DaisyPlayer";
	private OldDaisyBookImplementation book;
	private MediaPlayer player;
	private TouchGestureControlOverlay gestureOverlay;
	private FrameLayout frameLayout;
	private TextView mainText;
	private TextView statusText;
	private TextView depthText;
	private int audioOffset ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		book = (OldDaisyBookImplementation) getIntent().getSerializableExtra(
				"com.ader.DaisyBook");
		activateGesture();
		player = new MediaPlayer();
		player.setOnCompletionListener(this);
		play();
	}

	@Override
	protected void onDestroy() {
		// Let's stop playing the book if the user presses back, etc.
		// TODO(jharty): Can we detect how far through the audio segment we
		// are here? If so, perhaps we can save / update the auto-bookmark.
		// Anyhow we need to be able to save bookmarks part way through audio
		// segments. Worth investigating...
		stop();
		player.release();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.playermenu, menu);
		return(super.onCreateOptionsMenu(menu));
	}
	
	
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
		}
		return true;
	}
	
	public void onCompletion(MediaPlayer mp) {
		Util.logInfo(TAG, "onCompletion called.");
		// stop();
		if (book.nextSection(false)) {
			Util.logInfo(TAG, "PLAYING section: " + book.getDisplayPosition() + " " +
					book.current().getText());
			mainText.setText(book.current().getText());
			// reset the audio Offset (used on device rotation)
			audioOffset = 0;
			play();
		}
	}

	public void play() {
		Util.logInfo(TAG, "play");
		player.reset();
		int duration = Toast.LENGTH_LONG;

		Toast toast; 
		try {
			book.openSmil();
			read();
		} catch (FileNotFoundException fnfe) {
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
		} catch (IOException ioe) {
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
		} catch (RuntimeException re) {
			CharSequence text = getString(R.string.cannot_open_book) + " A Runtime error occured." 
				+ re.getLocalizedMessage();
			toast = Toast.makeText(this, text, duration);
			toast.show();
			
			AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
			explainProblem
			.setTitle(R.string.serious_problem_found)
			.setMessage(re.getLocalizedMessage())
			.setPositiveButton(R.string.close_instructions, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					DaisyPlayer.this.finish();
				}
			})
			.show();
		}
	}

	/**
	 * Start reading the current section of the book
	 * @throws FileNotFoundException 
	 */
	private void read() throws FileNotFoundException {
		int duration = Toast.LENGTH_LONG;
		
		Bookmark bookmark = book.getBookmark();
		// TODO(jharty): remove the bookmark log messages added in r226 once 
		// I'm happy the code works properly.
		Util.logInfo(TAG, String.format(
				"Loaded Bookmark details SMILfile[%s] NCC index[%d] offset[%d]",
				bookmark.getFilename(),bookmark.getNccIndex(), bookmark.getPosition()));

		// TODO(jharty): Find a practical way to format these messages for i18n and l10n
		depthText.setText("Depth " + book.getCurrentDepthInDaisyBook() + " of " + book.getMaximumDepthInDaisyBook());
		
		if (book.hasAudioSegments()) {
			try {
				mainText.setText(getText(R.string.reading_message) + " " + book.current().getText());
				
				// Note: Allow Java Garbage Collection to close the file.
				File f = new File(bookmark.getFilename());
				if (!(f.exists() && f.canRead())) {
					// TODO(jharty): Add a localised message to advise users
					// to upload a valid book. I could also provide a book
					// validation tool at some point.
					Util.logInfo(TAG, "File Not Available: " + bookmark.getFilename());
					throw new FileNotFoundException(bookmark.getFilename());
				}
				
				Util.logInfo(TAG, "Start playing " + bookmark.getFilename() + " " + bookmark.getPosition());
				player.setDataSource(bookmark.getFilename());
				player.prepare();
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalStateException e) {
				throw new RuntimeException(e);
			} catch (FileNotFoundException fnfe) {
				throw fnfe;
			} catch (IOException e) {
				throw new RuntimeException(bookmark.getFilename() 
						+ "\n" + e.getLocalizedMessage());
			}
			// Part of my experiment to stop the player restarting the audio
			// when the device is rotated between landscape and portrait modes.
			// player.seekTo(bookmark.getPosition());
			player.setScreenOnWhilePlaying(true);
			statusText.setText(getText(R.string.playing_message) + "...");
			try {
				player.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Trying the following to see if I can start playing part way
			// into the audio.
			Util.logInfo(TAG, String.format("Setting offset to %d", bookmark.getPosition()));
			player.seekTo(bookmark.getPosition());
			player.start();
		} else if (book.hasTextSegments()) {
			// TODO(jharty): add TTS to speak the text section
			// Note: we need to decide how to handle things like \n
			// For now, perhaps we can simply display the text in a new view.
			Util.logInfo("We need to read the text from: ", bookmark.getFilename());
			
			// For now, here is some information for the user. Perhaps I could
			// add a way to automatically send a request e.g. by email?
			mainText.setText(bookmark.getFilename());
			// TODO(jharty): Test whether the status is visible at this size.
			statusText.setTextSize(10.0f);
			statusText.setText(R.string.text_content_not_supported_yet);
			depthText.setText("");  // Blank out the depth message.
		}
	}
    
	public void stop() {
		player.pause();
		Bookmark bookmark = book.getBookmark();
		
		// The following change is to see whether the player updates the
		// bookmark with the current offset into the audio file when the book
		// is closed e.g. by pressing the back button on the Android device.
		int currentPosition = player.getCurrentPosition();
		Util.logInfo(TAG, "stop called at: " +currentPosition);
		bookmark.setPosition(currentPosition);
		player.reset();
		if (bookmark.getFilename() != null) {
			// We only save the bookmark if there's a valid file, problems e.g.
			// reading a smil file might mean the bookmark hasn't been assigned.
			book.getBookmark().save(book.getPath() + "auto.bmk");
		} else {
			Util.logInfo(TAG, "No filename, so we didn't save the auto-bookmark");
		}
	}

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
		
		savedInstanceState.putBoolean(IS_THE_BOOK_PLAYING, player.isPlaying());
		savedInstanceState.putInt(AUDIO_OFFSET, audioOffset);
		if (player.isPlaying()) {
			// Try seeing if I can pause the player on rotation rather than stopping it
			// player.pause();
		}
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
			// Try seeing if I can pause the player on rotation rather than stopping it
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
		frameLayout = (FrameLayout) findViewById(R.id.daisyPlayerLayout);
		gestureOverlay = new TouchGestureControlOverlay(this, gestureListener);
		frameLayout.addView(gestureOverlay);
		setContentView(frameLayout);
	}

	private GestureListener gestureListener = new GestureListener() {

		public void onGestureStart(Gesture g) {

		}

		public void onGestureChange(Gesture g) {
		}

		public void onGestureFinish(Gesture g) {
			if (g == Gesture.CENTER) {
				togglePlay();
			} else if (g == Gesture.UP) {
				if (book.previousSection()) {
					audioOffset = 0;
					play();
				}
			} else if (g == Gesture.DOWN) {
				if (book.nextSection(true)) {
					audioOffset = 0;
					play();
				}
			} else if (g == Gesture.LEFT) {
				int levelSetTo = book.decrementSelectedLevel();
				Util.logInfo(TAG, "Decremented Level to: " + levelSetTo);
				depthText.setText("Depth " + levelSetTo + " of " + book.getMaximumDepthInDaisyBook());
			} else if (g == Gesture.RIGHT) {
				int levelSetTo = book.incrementSelectedLevel();
				Util.logInfo(TAG, "Incremented Level to: " + levelSetTo);
				// TODO(jharty): Localize all the recently added hardcoded text e.g. here!
				depthText.setText("Depth " + levelSetTo + " of " + book.getMaximumDepthInDaisyBook());
			}
		}
	};
}