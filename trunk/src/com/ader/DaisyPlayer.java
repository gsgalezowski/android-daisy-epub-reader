package com.ader;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.marvin.widget.TouchGestureControlOverlay;
import com.google.marvin.widget.TouchGestureControlOverlay.Gesture;
import com.google.marvin.widget.TouchGestureControlOverlay.GestureListener;

public class DaisyPlayer extends Activity implements OnCompletionListener {

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
		player.reset();
		book.openSmil();
		try {
			player.setDataSource(book.getBookmark().getFilename());
			player.prepare();
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		player.seekTo(book.getBookmark().getPosition());
		player.start();

	}

	public void stop() {
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