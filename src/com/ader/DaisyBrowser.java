package com.ader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class DaisyBrowser extends ListActivity {
    File currentDirectory = new File("/sdcard/");
    private List<String> files;
    private static final String TAG = "DaisyBrowser";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        GenerateBrowserData();
    }

    boolean isDaisyDirectory(File aFile) {
        if (!aFile.isDirectory())
            return false;

        if (new File(aFile, "ncc.html").exists())
            return true;
        else
            return false;
    }

    @Override
    protected void onListItemClick(android.widget.ListView l,
            android.view.View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String item = files.get(position);

        if (isDaisyDirectory(new File(currentDirectory, item))) {
            Intent i = new Intent(this, DaisyReader.class);

            i.putExtra("daisyPath", new File(currentDirectory, item)
                    .getAbsolutePath()
                    + "/");
            startActivity(i);
            return;
        }

        if (item.equals("Up 1 Level")) {
            currentDirectory = new File(currentDirectory.getParent());
            GenerateBrowserData();
            return;
        }

        File temp = new File(currentDirectory, item);
        if (temp.isDirectory()) {
            currentDirectory = temp;
            GenerateBrowserData();
        }
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
