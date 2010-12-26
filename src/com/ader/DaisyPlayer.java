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

import java.io.FileNotFoundException;
import java.io.IOException;

public class DaisyPlayer extends Activity implements OnCompletionListener {

	private static final String AUDIO_OFFSET = "Offset";
	private static final String IS_THE_BOOK_PLAYING = "Playing";
	private static final String TAG = "DaisyPlayer";
	private DaisyBook book;
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
		book = (DaisyBook) getIntent().getSerializableExtra(
				"com.ader.DaisyBook");
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
			CharSequence text = "Cannot open book :( A file is missing. " + fnfe.getLocalizedMessage();
			toast = Toast.makeText(this, text, duration);
			toast.show();
			AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
			explainProblem
			.setCancelable(false)
			.setTitle(R.string.unable_to_open_file)
			.setMessage(fnfe.getLocalizedMessage())
			.setPositiveButton(R.string.close_instructions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					DaisyPlayer.this.finish();
				}
			})
			.show();
		} catch (IOException ioe) {
			CharSequence text = "Cannot open book :( " + ioe.getLocalizedMessage();
			toast = Toast.makeText(this, text, duration);
			toast.show();			
			
			AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
			explainProblem
			.setTitle("Permission Problem opening a file")
			.setMessage(ioe.getLocalizedMessage())
			.setPositiveButton(R.string.close_instructions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					DaisyPlayer.this.finish();
				}
			})
			.show();
		} catch (RuntimeException re) {
			CharSequence text = "Cannot open book :( A Runtime error occured." + re.getLocalizedMessage();
			toast = Toast.makeText(this, text, duration);
			toast.show();
			
			AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
			explainProblem
			.setTitle("Serious Problem found")
			.setMessage(re.getLocalizedMessage())
			.setPositiveButton(R.string.close_instructions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					DaisyPlayer.this.finish();
				}
			})
			.show();
		}
	}

	/**
	 * Start reading the current section of the book
	 */
	private void read() {
		Bookmark bookmark = book.getBookmark();

		depthText.setText("Depth " + book.getCurrentDepthInDaisyBook() + " of " + book.getMaximumDepthInDaisyBook());
		
		if (book.hasAudioSegments()) {
			try {
				mainText.setText("Reading " + book.current().getText() +  " " 
						+ new String(book.current().getText().getBytes("ISO8859_1"), "ISO8859_1"));
				Util.logInfo(TAG, "Start playing " + bookmark.getFilename() + " " + bookmark.getPosition());
				player.setDataSource(bookmark.getFilename());
				player.prepare();
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalStateException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(bookmark.getFilename() 
						+ "\n" + e.getLocalizedMessage());
			}
			// Part of my experiment to stop the player restarting the audio
			// when the device is rotated between landscape and portrait modes.
			// player.seekTo(bookmark.getPosition());
			player.seekTo(audioOffset);
			player.setScreenOnWhilePlaying(true);
			statusText.setText("Playing...");
			player.start();
		} else if (book.hasTextSegments()) {
			// TODO(jharty): add TTS to speak the text section
			// Note: we need to decide how to handle things like \n
			// For now, perhaps we can simply display the text in a new view.
			Util.logInfo("We need to read the text from: ", bookmark.getFilename());
		}
	}
    
	public void stop() {
		Util.logInfo(TAG, "stop");
		player.pause();
		Bookmark bookmark = book.getBookmark();
		bookmark.setPosition(player.getCurrentPosition());
		player.reset();
		if (bookmark.getFilename() != null) {
			// We only save the bookmark if there's a valid file, problems e.g.
			// reading a smil file might mean the bookmark hasn't been assigned.
			book.getBookmark().save(book.getPath() + "auto.bmk");
		}
	}

	public void togglePlay() {
		Util.logInfo(TAG, "togglePlay called.");
		if (player.isPlaying()) {
			statusText.setText("Paused");
			player.pause();
		} else {
			statusText.setText("Playing");
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
			player.pause();
			// stop();
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
			statusText.setText("Paused...");
			player.pause();
			// stop();
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