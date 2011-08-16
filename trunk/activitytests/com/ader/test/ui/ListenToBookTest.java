package com.ader.test.ui;


import java.io.IOException;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.ader.InvalidDaisyStructureException;
import com.ader.OldDaisyBookImplementation;
import com.ader.ui.DaisyPlayer;

public class ListenToBookTest extends ActivityInstrumentationTestCase2<DaisyPlayer> {
	
	private static final Class<DaisyPlayer> FOR_ACTIVITY = DaisyPlayer.class;
	private static final String PACKAGE = FOR_ACTIVITY.getPackage().toString();
	private DaisyPlayer listenToBook;
	private TextView playingView;

	@Override
	protected void setUp() throws Exception {
		Intent i = new Intent();
		i.putExtra(DaisyPlayer.DAISY_BOOK_KEY, theBook());
		setActivityIntent(i);
		listenToBook = getActivity();
		playingView = (TextView) listenToBook.findViewById(com.ader.R.id.statusText);
		}
	
	public ListenToBookTest() {
		super(PACKAGE, FOR_ACTIVITY);
	}
	
	public void testStartsByListeningToTheBook() throws Exception {
		assertEquals("DaisyPlayer", listenToBook.getTitle());
		String playing = listenToBook.getString(com.ader.R.string.playing) + "...";
		assertEquals(playing, playingView.getText());
	}

	private OldDaisyBookImplementation theBook() throws InvalidDaisyStructureException, IOException {
		OldDaisyBookImplementation book = new OldDaisyBookImplementation();
		//TODO 20110816 (amarcano): I'm guessing that we'll need some sort of test-book... but wondering if we can use a test-double here?
		//TODO 20110816 (amarcano): Remove this temporary phone-specific hack, used to see how this all works
		book.openFromFile("/sdcard/books/daisy2.02/niv_nt-202/ncc.html");
		return book;
	}
}
