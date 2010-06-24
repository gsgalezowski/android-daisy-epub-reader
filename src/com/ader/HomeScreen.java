package com.ader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
    }
    
    public void onClick(View v)
    {
    	switch (v.getId()){
    	case R.id.open_button:
    		Intent i = new Intent(this, DaisyBrowser.class);
    		startActivity(i);
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
    		
    	case R.id.help_button:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			
			builder
				.setTitle(R.string.player_instructions_description)
				.setMessage(R.string.player_instructions)
				.setPositiveButton(R.string.close_instructions, null)
				.show();
    		break;
    	}
    }
}
