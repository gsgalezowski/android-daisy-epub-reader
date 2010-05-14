package com.ader;

import android.app.Activity;
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
    	}
    }
}
