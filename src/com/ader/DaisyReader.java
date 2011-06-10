package com.ader;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.marvin.widget.GestureOverlay;
import com.google.marvin.widget.GestureOverlay.Gesture;
import com.google.marvin.widget.GestureOverlay.GestureListener;

public class DaisyReader extends ListActivity {
	private OldDaisyBookImplementation book = new OldDaisyBookImplementation();
	private GestureOverlay gestureOverlay;
	private LinearLayout linearLayout;
	private String path;
	private static final String TAG = "DaisyReader";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.results_list);

		try {
			activateGesture();

			try {
				path = getIntent().getStringExtra("daisyPath");
				book.openFromFile(path + getIntent().getStringExtra("daisyNccFile"));
			} catch (InvalidDaisyStructureException idse) {
				// TODO(jharty): add a UI to help the user address the problem.
				idse.printStackTrace();
			}
			
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
			// This stops the list of sections from appearing after back is pressed.
			// But it doesn't stop the audio...
			this.finish();
			
		} catch (IOException e) {
			// TODO(jharty): Remove the Toast error message once the AlertDialog works
    		CharSequence text = "Cannot open book :( " + e.getLocalizedMessage();
    		int duration = Toast.LENGTH_SHORT;

    		Toast toast = Toast.makeText(this, text, duration);
    		toast.show();
			
    		// TODO(jharty): Find out why the AlertDialog does not get displayed :(
/*			AlertDialog.Builder explainProblem = new AlertDialog.Builder(this);
			
			explainProblem
				.setTitle("Problem opening the book")
				.setMessage(R.string.unable_to_open_file)
				.setPositiveButton(R.string.close_instructions, null)
				.show();
			explainProblem.show();*/
			Util.logInfo(TAG, "Cannot open book :( see:" + e.getLocalizedMessage());
			// UiHelper.alert(this, R.string.unable_to_open_file);
			finish();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		for (int i = 1; i <= book.getMaximumDepthInDaisyBook(); i++)
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
		book.goTo((DaisyItem) l.getItemAtPosition(position));
		play();
	}

	void displayContents() {
		Util.logInfo(TAG, "displayContents called - should we bother?");
		setListAdapter(new ArrayAdapter<DaisyItem>(this, R.layout.results_list, R.layout.listrow, book
				.getNavigationDisplay()));
	}

	private void activateGesture() {
		ListView listView = getListView();
		linearLayout = (LinearLayout) listView.getParent();
		gestureOverlay = new GestureOverlay(this, gestureListener);
		linearLayout.addView(gestureOverlay);
	}

	private GestureListener gestureListener = new GestureListener() {

		public void onGestureStart(int g) {
		}

		public void onGestureChange(int g) {
		}

		public void onGestureFinish(int g) {
			if (g == Gesture.LEFT) {
				int levelSetTo = book.decrementSelectedLevel();
				Util.logInfo(TAG, "Decremented Level to: " + levelSetTo);
			} else if (g == Gesture.RIGHT) {
				book.incrementSelectedLevel();
				int levelSetTo = book.decrementSelectedLevel();
				Util.logInfo(TAG, "Incremented Level to: " + levelSetTo);
			} else if (g == Gesture.CENTER) {
				play();
			}
			displayContents();
		}
	};
}