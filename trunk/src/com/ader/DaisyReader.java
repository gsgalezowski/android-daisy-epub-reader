package com.ader;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;

public class DaisyReader extends ListActivity {
	DaisyBook book = new DaisyBook();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		book.open(getIntent().getStringExtra("daisyPath"));

		displayContents();
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		book.stop();
		for (int i = 1; i <= book.getNCCDepth(); i++)
			menu.add(Menu.NONE, i, Menu.NONE, "Level " + Integer.toString(i));
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		book.setSelectedLevel(item.getItemId());
		displayContents();
		getListView().setSelection(book.getDisplayPosition());
		return true;
	};

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
				|| (keyCode == KeyEvent.KEYCODE_DPAD_UP))
			book.goTo((NCCEntry) getListView().getSelectedItem());

		return true;

	}

	@Override
	protected void onListItemClick(android.widget.ListView l,
			android.view.View v, int position, long id) {
		book.togglePlay();

	}

	void displayContents() {
		setListAdapter(new ArrayAdapter<NCCEntry>(this,
				android.R.layout.simple_list_item_1, book
						.GetNavigationDisplay()));

		
		
	}
}