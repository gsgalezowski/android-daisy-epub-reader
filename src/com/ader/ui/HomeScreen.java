package com.ader.ui;

import java.io.File;

import com.ader.DaisyBookUtils;
import com.ader.R;
import com.ader.Util;
import com.ader.R.id;
import com.ader.R.layout;
import com.ader.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

public class HomeScreen extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View openButton = findViewById(R.id.open_button);
        openButton.setOnClickListener(this);
        View searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        View helpButton = findViewById(R.id.help_button);
        helpButton.setOnClickListener(this);
        
        // TODO (jharty): how about only enabling this button if we have saved
        // a book previously?
        View lastBookButton = findViewById(R.id.open_last_button);
        lastBookButton.setOnClickListener(this);
        
        View preferencesButton = findViewById(R.id.settings_button);
        preferencesButton.setOnClickListener(this);
        
        View aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        
    }
    
    public void onClick(View v)
    {
    	switch (v.getId()){
    	case R.id.open_button:
    		Intent daisyBrowserIntent = new Intent(this, DaisyBrowser.class);
    		startActivity(daisyBrowserIntent);
    		break;
    	
    	case R.id.open_last_button:
    		
			SharedPreferences settings = getSharedPreferences(DaisyBookUtils.PREFS_FILE, 0);
			String pathToLastBookOpen = settings.getString(DaisyBookUtils.LAST_BOOK, "");
			Util.logInfo("HomeScreen", "Path to last book = " + pathToLastBookOpen);
    		
            File daisyPath = new File(pathToLastBookOpen);
    		if (pathToLastBookOpen.length() > 1 && DaisyBookUtils.folderContainsDaisy2_02Book(daisyPath)) {
                Intent daisyBookIntent = new Intent(this, DaisyReader.class);

                daisyBookIntent.putExtra("daisyPath", daisyPath.getAbsolutePath() + "/");
                daisyBookIntent.putExtra("daisyNccFile", DaisyBookUtils.getNccFileName(daisyPath));
                startActivity(daisyBookIntent);
                return;
    		}
    		
    		String title;
    		String message;
    		
    		String storagestate = Environment.getExternalStorageState();
    		if (!storagestate.equals(Environment.MEDIA_MOUNTED) ) {
    			title = getString(R.string.sdcard_title);
    			message = getString(R.string.sdcard_mounted);
    		} else {
    			// For now we assume the problem is that no previous title
    			// was saved. We could easily fall foul of displaying the
    			// incorrect message. 
    			// TODO(jharty): clean up this code when making the overall
    			// application more robust.
    			title = getString(R.string.no_previous_book_saved_title);
    			message = getString(R.string.no_previous_book_saved_msg);
    		}
    		
    		// TODO(jharty): this is a rough first-cut, not intended as the
    		// finished code. We'll clean up once the UI has been tested.
    		AlertDialog.Builder whatWentWrong = new AlertDialog.Builder(this);
    		whatWentWrong
    			// TODO(jharty): move text to the resources so they can be translated.
    			.setTitle(title)
    			.setMessage(message)
    			// TODO(jharty): use a specific string, rather than repurposing this one!
    			.setPositiveButton(R.string.close_instructions, null)
    			.show();
			
    		break;
    		
    	case R.id.search_button:
    		// TODO (jharty): there are disconnects between the name of this
    		// button and the Intent we're calling (which doesn't allow the
    		// user to search directly. I think this code will end up being
    		// called by the open button, replacing the current, crude open
    		// implementation. However I'll work here until the new code has
    		// been integrated.
    		Intent iSearch = new Intent(this, DaisyBookFinder.class);
    		startActivity(iSearch);
    		break;
    
    	case R.id.settings_button:
    		startActivity(new Intent(this, Preferences.class));
    		break;
    		
    	case R.id.help_button:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			
			builder
				.setTitle(R.string.player_instructions_description)
				.setMessage(R.string.player_instructions)
				.setPositiveButton(R.string.close_instructions, null)
				.show();
    		break;
    		
    	case R.id.about_button:
    		startActivity(new Intent(this, AboutView.class));
    		break;
    	}
    }
}
