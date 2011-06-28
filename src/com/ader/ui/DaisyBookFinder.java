package com.ader.ui;

/**
 * DaisyBookFinder automatically searches for suitable books on the sdcard.
 * 
 * This is a first cut of the implementation as it's slow, uncommunicative,
 * and calls BookValidator that currently assumes the books are in /sdcard/ on
 * the device.
 */
import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ader.DaisyBookUtils;
import com.ader.R;
import com.ader.Util;
import com.ader.io.BookValidator;

public class DaisyBookFinder extends ListActivity {
	private ArrayList<String> books;
	BookValidator validator;
	private static final String TAG = "DaisyBookFinder";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Util.logInfo(TAG, "onCreate");
        setContentView(R.layout.results_list);
                
        validator = new BookValidator();
        String rootfolder = Preferences.getRootfolder(getBaseContext());
		Util.logInfo(TAG, "The root folder to search is: " + rootfolder);
        validator.findBooks(rootfolder);
        books = validator.getBookList();
        PopulateList();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = books.get(position);
		
		Intent i = new Intent(this, DaisyReader.class);
		i.putExtra("daisyPath", item + "/");
		i.putExtra("daisyNccFile", DaisyBookUtils.getNccFileName(new File(item)));
		startActivity(i);
		return;
	}
	
	void PopulateList() {
		// TODO(jharty): Check if currentDirectory maps to ExternalStorageDirectory
		Util.logInfo(TAG, "External Storage is: " + Environment.getExternalStorageDirectory());
		// TODO(jharty): remove this hack once I've debugged the interaction
		// It probably needs to move to a more general FileIO class that'd be
		// used by the rest of the application. That way we can reduce
		// duplication of code e.g. with DaisyBrowser and make the 
		// application more robust against events such as the sdcard becoming
		// unavailable while the application is in use.
		String storagestate = Environment.getExternalStorageState();

		if (!storagestate.equals(Environment.MEDIA_MOUNTED) ) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(R.string.sdcard_title);
			alertDialog.setMessage(getString(R.string.sdcard_mounted));
			alertDialog.setButton(getString(R.string.close_instructions), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
					return;
				} });
			alertDialog.show();  
		}

		// TODO (jharty): format the list of books more attractively.
		setListAdapter(new ArrayAdapter<String>(this, R.layout.listrow, R.id.textview, books));
		getListView().setTextFilterEnabled(true);
	}
}
