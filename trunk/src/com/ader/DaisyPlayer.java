package com.ader;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.marvin.widget.TouchGestureControlOverlay;
import com.google.marvin.widget.TouchGestureControlOverlay.Gesture;
import com.google.marvin.widget.TouchGestureControlOverlay.GestureListener;

import java.io.IOException;

public class DaisyPlayer extends Activity implements OnCompletionListener {

	private static final String IS_THE_BOOK_PLAYING = "Playing";
	private static final String TAG = "DaisyPlayer";
	private DaisyBook book;
	private MediaPlayer player;
	private TouchGestureControlOverlay gestureOverlay;
	private FrameLayout frameLayout;

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

	//@Override
	//protected void onStop() {
//		super.onStop();
		//player.release();
// 	}

	public void onCompletion(MediaPlayer mp) {
		stop();
		book.next(false);
		play();
	}

	public void play() {
		Log.i(TAG, "play");
		player.reset();
		book.openSmil();
		read();
	}

	/**
	 * Start reading the current section of the book
	 */
	private void read() {
		Bookmark bookmark = book.getBookmark();

		if (book.hasAudioSegments()) {
			try {
				Log.i(TAG, "Start playing " + bookmark.getFilename() + " " + bookmark.getPosition());
				player.setDataSource(bookmark.getFilename());
				player.prepare();
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalStateException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			player.seekTo(bookmark.getPosition());
			player.start();
		} else if (book.hasTextSegments()) {
			// TODO(jharty): add TTS to speak the text section
			// Note: we need to decide how to handle things like \n
			// For now, perhaps we can simply display the text in a new view.
			Log.i("We need to read the text from: ", bookmark.getFilename());
		}
	}
    
	public void stop() {
		Log.i(TAG, "stop");
		player.pause();
		book.getBookmark().setPosition(player.getCurrentPosition());
		player.reset();
		book.getBookmark().save(book.getPath() + "auto.bmk");
	}

	public void togglePlay() {
		if (player.isPlaying())
			stop();
		else
			play();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean(IS_THE_BOOK_PLAYING, player.isPlaying());
		if (player.isPlaying()) {
			stop();
		}
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Boolean isPlaying = savedInstanceState.getBoolean(IS_THE_BOOK_PLAYING, true);
		if (!isPlaying) {
			stop();
		}
	}
	
	private void activateGesture() {
		frameLayout = new FrameLayout(this);
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
			} else if (g == Gesture.LEFT) {
				book.previous();
				play();
			} else if (g == Gesture.RIGHT) {
				book.next(true);
				play();
			}
		}
	};
}