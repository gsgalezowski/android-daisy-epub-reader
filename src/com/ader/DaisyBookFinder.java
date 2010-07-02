package com.ader;

/**
 * DaisyBookFinder automatically searches for suitable books on the sdcard.
 * 
 * This is a first cut of the implementation as it's slow, uncommunicative,
 * and calls BookValidator that currently assumes the books are in /sdcard/ on
 * the device.
 */
import java.io.File;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ader.io.BookValidator;

public class DaisyBookFinder extends ListActivity {
	private ArrayList<String> books;
	BookValidator validator;
	private static final String TAG = "DaisyBookFinder";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Util.logInfo(TAG, "onCreate");
                
        validator = new BookValidator();
        validator.findBooks("/sdcard/");
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
		// TODO (jharty): format the list of books more attractively.
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, books));
		getListView().setTextFilterEnabled(true);
	}
	
}
