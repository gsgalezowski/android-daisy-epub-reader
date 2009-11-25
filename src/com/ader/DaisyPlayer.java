package com.ader;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class DaisyPlayer extends MediaPlayer implements OnCompletionListener {
	private DaisyBook book;
	boolean playing = false;

	public DaisyPlayer(DaisyBook book) {
		super();
		this.book = book;
		setOnCompletionListener(this);
	}

	public void onCompletion(MediaPlayer mp) {
		if (playing) {
			stop();
			book.Next();
			play();
		}
	}

	public void play() {
		playing = true;
		book.OpenSmil();
		try {
			setDataSource(book.getBookmark().getFilename());
			prepare();
			seekTo(book.getBookmark().getPosition());
			start();
		} catch (Exception e) {

		}

	}

	@Override
	public void stop() {
		playing = false;
		pause();
		book.getBookmark().setPosition(getCurrentPosition());
		reset();
		book.getBookmark().Save(book.getPath() + "auto.bmk");
	}

	public void togglePlay() {
		if (playing)
			stop();
		else
			play();
	}
}