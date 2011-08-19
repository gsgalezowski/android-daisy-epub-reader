/**
 * The main DaisyReader UI that interacts with the user to play a Daisy book.
 * 
 * Currently an overstuffed class with more methods and code than sense. I have
 * lots of work to do to redesign the class and simplify the code.
 */
package com.ader.ui;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ader.DaisyItem;
import com.ader.InvalidDaisyStructureException;
import com.ader.OldDaisyBookImplementation;
import com.ader.R;
import com.ader.utilities.DaisyBookUtils;
import com.ader.utilities.Logging;
import com.google.marvin.widget.GestureOverlay;
import com.google.marvin.widget.GestureOverlay.Gesture;
import com.google.marvin.widget.GestureOverlay.GestureListener;

/**
 * This class currently connects the HomeScreen with the DaisyPlayer.
 * 
 * It doesn't seem to add much value and could be removed with little loss of
 * functionality. However, it may become more useful if it's used to launch the
 * appropriate user interface depending on the mix of the type of book and the
 * preferences of the user. Anyhow, one way or another, expect this class to
 * change.
 * 
 * @author jharty
 */

// TODO 20110819 (jharty): Decide what to do to improve or replace this class.
public class DaisyReader extends ListActivity {
	private OldDaisyBookImplementation book = new OldDaisyBookImplementation();
	private static final String TAG = "DaisyReader";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		String path = "";
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.results_list);

		try {
			activateGesture();

			try {
				path = getIntent().getStringExtra("daisyPath");
				book.openFromFile(path + getIntent().getStringExtra("daisyNccFile"));
			} catch (InvalidDaisyStructureException idse) {
				// TODO(jharty): add a UI to help the user address the problem.
				Logging.logSevereWarning(TAG, "Problem Opening DAISY book, aborting...", idse); 
				DaisyReader.this.finish();
				return;
			}
			
			// Now let's save details of the this book, as the most recent book
			SharedPreferences bookSettings = getSharedPreferences(DaisyBookUtils.PREFS_FILE, 0);
			SharedPreferences.Editor editor = bookSettings.edit();
			editor.putString(DaisyBookUtils.LAST_BOOK, path);
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
			Logging.logInfo(TAG, "Cannot open book :( see:" + e.getLocalizedMessage());
			// UiHelper.alert(this, R.string.unable_to_open_file);
			finish();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		for (int i = 1; i <= book.getMaximumDepthInDaisyBook(); i++) {
			menu.add(Menu.NONE, i, Menu.NONE, "Level " + Integer.toString(i));
		}
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
		dp.putExtra(DaisyPlayer.DAISY_BOOK_KEY, book);
		startActivity(dp);
	}

	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position,
			long id) {
		super.onListItemClick(l, v, position, id);
		book.goTo((DaisyItem) l.getItemAtPosition(position));
		play();
	}

	// TODO 20110819 (jharty): This doesn't seem to display the contents at all - fix me or delete me.
	void displayContents() {
		Logging.logInfo(TAG, "displayContents called - should we bother?");
		setListAdapter(new ArrayAdapter<DaisyItem>(this, R.layout.results_list, R.layout.listrow, book
				.getNavigationDisplay()));
	}

	private void activateGesture() {
		ListView listView = getListView();
		LinearLayout linearLayout = (LinearLayout) listView.getParent();
		GestureOverlay gestureOverlay = new GestureOverlay(this, gestureListener);
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
				Logging.logInfo(TAG, "Decremented Level to: " + levelSetTo);
			} else if (g == Gesture.RIGHT) {
				book.incrementSelectedLevel();
				int levelSetTo = book.decrementSelectedLevel();
				Logging.logInfo(TAG, "Incremented Level to: " + levelSetTo);
			} else if (g == Gesture.CENTER) {
				play();
			}
			displayContents();
		}
	};
}