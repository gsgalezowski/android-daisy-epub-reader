package com.ader;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.google.marvin.widget.TouchGestureControlOverlay;
import com.google.marvin.widget.TouchGestureControlOverlay.Gesture;
import com.google.marvin.widget.TouchGestureControlOverlay.GestureListener;

import java.io.IOException;

public class DaisyReader extends ListActivity {
	private DaisyBook book = new DaisyBook();
	private TouchGestureControlOverlay gestureOverlay;
	private FrameLayout frameLayout;
	private String path;
	private static final String TAG = "DaisyReader";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			activateGesture();
			try {
				path = getIntent().getStringExtra("daisyPath");
				book.openFromFile(path + getIntent().getStringExtra("daisyNccFile"));
			} catch (InvalidDaisyStructureException idse) {
				// TODO(jharty): add a UI to help the user address the problem.
				idse.printStackTrace();
			}
			book.loadAutoBookmark();
			// Now let's save details of the this book, as the most recent book
			SharedPreferences bookSettings = getSharedPreferences(DaisyBookUtils.PREFS_FILE, 0);
			SharedPreferences.Editor editor = bookSettings.edit();
			editor.putString(DaisyBookUtils.LAST_BOOK, this.path);
			// Commit the edits!
			editor.commit();

			displayContents();
			getListView().setSelection(book.getDisplayPosition());
			registerForContextMenu(getListView());
			play();
			
		} catch (IOException e) {
			UiHelper.alert(this, R.string.unable_to_open_file);
			finish();
		}
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
		Util.logInfo(TAG, "displayContents called - should we bother?");
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
				int levelSetTo = book.decrementSelectedLevel();
				Util.logInfo(TAG, "Decremented Level to: " + levelSetTo);
			} else if (g == Gesture.RIGHT) {
				book.incrementSelectedLevel();
				int levelSetTo = book.decrementSelectedLevel();
				Util.logInfo(TAG, "Incremented Level to: " + levelSetTo);
			} else if (g == Gesture.CENTER) {
				// play((NCCEntry) getListAdapter().getItem(0));
				play();
			}
			displayContents();
		}
	};
}