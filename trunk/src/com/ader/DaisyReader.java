package com.ader;

import java.io.IOException;

import com.google.marvin.widget.TouchGestureControlOverlay;
import com.google.marvin.widget.TouchGestureControlOverlay.Gesture;
import com.google.marvin.widget.TouchGestureControlOverlay.GestureListener;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

public class DaisyReader extends ListActivity {
	private DaisyBook book = new DaisyBook();
	private TouchGestureControlOverlay gestureOverlay;
	private FrameLayout frameLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activateGesture();
		try {
			book.open(getIntent().getStringExtra("daisyPath"));
		} catch (IOException e) {
			UiHelper.alert(this, R.string.unable_to_open_file);
			finish();
		}
		displayContents();
		getListView().setSelection(book.getDisplayPosition());
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
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

	private void play() {
		Intent dp = new Intent(this, DaisyPlayer.class);
		dp.putExtra("com.ader.DaisyBook", book);
		startActivity(dp);
	}

	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position,
			long id) {
		super.onListItemClick(l, v, position, id);
		book.goTo((NCCEntry) l.getItemAtPosition(position));
		play();
	}

	void displayContents() {
		setListAdapter(new ArrayAdapter<NCCEntry>(this, android.R.layout.simple_list_item_1, book
				.getNavigationDisplay()));
	}

	private void activateGesture() {
		frameLayout = (FrameLayout) getListView().getParent();
		gestureOverlay = new TouchGestureControlOverlay(this, gestureListener);
		frameLayout.addView(gestureOverlay);
	}

	private GestureListener gestureListener = new GestureListener() {

		public void onGestureStart(Gesture g) {
		}

		public void onGestureChange(Gesture g) {
		}

		public void onGestureFinish(Gesture g) {
			if (g == Gesture.LEFT) {
				book.decrementSelectedLevel();
			} else if (g == Gesture.RIGHT) {
				book.incrementSelectedLevel();
			} else if (g == Gesture.CENTER) {
				// play((NCCEntry) getListAdapter().getItem(0));
				play();
			}
			displayContents();
		}
	};
}