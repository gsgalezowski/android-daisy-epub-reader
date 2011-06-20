/**
 * Displays information about this program.
 * 
 * The initial code came from 
 * http://ballardhack.wordpress.com/2010/09/28/subversion-revision-in-android-app-version-with-eclipse/
 */
package com.ader.ui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ader.R;

public class AboutView extends Activity {
	private final String TAG = AboutView.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView versionText = (TextView) findViewById(R.id.version);
		if (versionText != null) {
			versionText.setText(String.format(getString(R.string.version), getVersionName(), getVersionCode()));
		}

	}

	private String getVersionName() {
		String version = "??";
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Version name not found in package", e);
		}
		return version;
	}

	private int getVersionCode() {
		int version = -1;
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Version number not found in package", e);
		}
		return version;
	}
}
