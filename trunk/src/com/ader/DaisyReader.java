package com.ader;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;

public class  DaisyReader extends ListActivity {
	DaisyBook book = new DaisyBook();
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);

		book.open(getIntent().getStringExtra("daisyPath"));

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, book
						.GetNavigationDisplay()));

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
				|| (keyCode == KeyEvent.KEYCODE_DPAD_UP))
			book.goTo((int) getSelectedItemId());
		return true;
	}

	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id) {
		book.togglePlay();
	}

}