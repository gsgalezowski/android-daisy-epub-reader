package com.ader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ader.utilities.Logging;
/**
 * This class doesn't work as desired and isn't part of the project.
 * @author Julian Harty
 *
 */

public class FileIOUtils extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Logging.logInfo("FileIOUtils", "OnCreate Called");
	}

	/** 
	 * This is a work in progress 
	 */
	public void reportProblem(Context context) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		  alertDialog.setTitle(R.string.sdcard_title);
		  alertDialog.setMessage(context.getString(R.string.sdcard_mounted));
		  alertDialog.setButton(context.getString(R.string.close_instructions), 
				  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		      // finish();
		      return;
		  } });
		  alertDialog.show();
	}
}
