/**
 * Displays information about this program.
 * 
 * The initial code came from 
 * http://ballardhack.wordpress.com/2010/09/28/subversion-revision-in-android-app-version-with-eclipse/
 */
package com.ader.ui;

import java.util.Locale;

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
		TextView aboutText = (TextView) findViewById(R.id.about);
		if (aboutText != null) {
			StringBuilder aboutMsg = new StringBuilder();
			aboutMsg.append(String.format(getString(R.string.version), getVersionName(), getVersionCode()));
			aboutMsg.append("\n");
			aboutMsg.append("\nCurrent Locale is: " + java.util.Locale.getDefault().getDisplayName());
			aboutMsg.append("\n");
			aboutText.setText(aboutMsg.toString());
		}
		
		TextView localesText = (TextView) findViewById(R.id.installed_locales);
		if (localesText != null) {
			StringBuilder locales = new StringBuilder();
			locales.append("Locales installed on phone are:\n");
			
			Locale installedLocales [] = Locale.getAvailableLocales();
			for (Locale l : installedLocales) {
				locales.append("\n  " + l.getDisplayName());
			}
			localesText.setText(locales.toString());
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
