package com.ader;

/**
 * DaisyBrowser enables the user to pick the book they want to read.
 * 
 * It is used to navigate through a folder structure on the device, starting
 * with the /sdcard/ For now we assume always exists, we need to make the code
 * both more robust e.g. in case the sdcard is busy or unavailable, and more
 * flexible e.g. the user may want to store the books elsewhere.
 * 
 * This code is getting kinda creaky and is due for revamping. As we're
 * actively integrating BookValidator I can accept the current code for now
 * since some of the current hacks should be able to be removed during the
 * integration work.
 */
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DaisyBrowser extends ListActivity {
    File currentDirectory = new File("/sdcard/");
    private List<String> files;
    private static final String TAG = "DaisyBrowser";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.logInfo(TAG, "onCreate");
        GenerateBrowserData();
    }

    boolean isDaisyDirectory(File aFile) {
        if (!aFile.isDirectory())
            return false;

        // Minor hack to cope with the potential of ALL CAPS filename, as per
        // http://www.daisy.org/z3986/specifications/daisy_202.html#ncc
        if (new File(aFile, "ncc.html").exists() || new File(aFile, "NCC.HTML").exists())
            return true;
        else
            return false;
    }

    @Override
    protected void onListItemClick(android.widget.ListView l,
            android.view.View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String item = files.get(position);

        File daisyPath = new File(currentDirectory, item);
		if (isDaisyDirectory(daisyPath)) {
            Intent i = new Intent(this, DaisyReader.class);

            i.putExtra("daisyPath", daisyPath.getAbsolutePath() + "/");
            i.putExtra("daisyNccFile", getNccFileName(daisyPath));
            startActivity(i);
            return;
        }

        if (item.equals("Up 1 Level")) {
            currentDirectory = new File(currentDirectory.getParent());
            GenerateBrowserData();
            return;
        }

        File temp = daisyPath;
        if (temp.isDirectory()) {
            currentDirectory = temp;
            GenerateBrowserData();
        }
    }

    private String getNccFileName(File currentDirectory) {
    	if (new File(currentDirectory, "ncc.html").exists())
    		return "ncc.html";

    	if (new File(currentDirectory, "NCC.HTML").exists())
    		return "NCC.HTML";

		return null;
	}

	void GenerateBrowserData() {
        FilenameFilter dirFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        };
        files = new ArrayList<String>(Arrays.asList(currentDirectory
                .list(dirFilter)));
        Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
        if (!currentDirectory.getParent().equals("/")) {
            files.add("Up 1 Level");
        }

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, files));
        return;

    }
}
