/**
 * Displays information about this program.
 * 
 * The initial code came from 
 * http://ballardhack.wordpress.com/2010/09/28/subversion-revision-in-android-app-version-with-eclipse/
 */
package com.ader.ui;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ader.R;
import com.ader.io.XmlFormatter;
import com.ader.utilities.About;
import com.ader.utilities.Logging;

/**
 * TODO (jharty): I'd like to add information on the book being read and any
 * exceptions that have been reported. We could also add the ability for users
 * to add comments about the problem they've discovered.
 * 
 * Format the contents so it's easy to parse. 
 *  
 * @author Julian Harty
 *
 */
public class AboutView extends Activity implements OnClickListener {
	private static final String ABOUT_EMAIL_VERSION = "0.0.4";
	private static final String TAG = AboutView.class.getName();
	private StringBuilder locales;
	private StringBuilder aboutMsg;
	private StringBuilder xmlFormattedAboutMsg;
	private XmlFormatter xmlFormatter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		Button email = (Button) findViewById(R.id.eMail);
		email.setOnClickListener(this);
		
		Button returnToHomescreen = (Button) findViewById(R.id.return_to_homescreen);
		returnToHomescreen.setOnClickListener(this);
		
		TextView aboutText = (TextView) findViewById(R.id.about);
		if (aboutText != null) {
			About aboutApplication = new About(this);
			
			aboutMsg = new StringBuilder();
			xmlFormattedAboutMsg = new StringBuilder();
			xmlFormattedAboutMsg.append("<daisyreader>");

			xmlFormatter = new XmlFormatter();

			aboutMsg.append(String.format(getString(R.string.version), 
					aboutApplication.getVersionName(), aboutApplication.getVersionCode()));
			aboutMsg.append("\nAndroid Version: " + Build.VERSION.SDK_INT);
			String version = xmlFormatter.formatAsXml(aboutApplication.getVersionName() + "." + 
													  aboutApplication.getVersionCode(), 
													  "version");
			xmlFormattedAboutMsg.append(version);
			
			String androidVersion = xmlFormatter.formatAsXml(Integer.toString(Build.VERSION.SDK_INT), "android_version");
			xmlFormattedAboutMsg.append(androidVersion);
			
			aboutMsg.append("\n" + Build.MANUFACTURER);
			xmlFormattedAboutMsg.append(xmlFormatter.formatAsXml(Build.MANUFACTURER, "manufacturer"));
			
			aboutMsg.append(" " + Build.MODEL);
			xmlFormattedAboutMsg.append(xmlFormatter.formatAsXml(Build.MODEL, "model"));
			
			String sampleString = (String) getText(R.string.open_book);
			aboutMsg.append("\nSample Text: " + sampleString);
			xmlFormattedAboutMsg.append(xmlFormatter.formatAsXml(sampleString, "sampletext"));
			
			// TODO(jharty): Decide what to report on the screen and what to
			// include in the email.
			aboutMsg.append("\n");
			aboutMsg.append("\nCurrent Locale is: " + java.util.Locale.getDefault().getDisplayName());
			aboutMsg.append("\n");
			Locale locale = getResources().getConfiguration().locale;
			aboutMsg.append("\nDisplay language is:  " + locale.getDisplayLanguage());
			aboutMsg.append("\nCountry is: " + locale.getCountry());
			aboutMsg.append("\nDisplay country is: " + locale.getDisplayCountry());
			aboutMsg.append("\nLanguage is: " + locale.getLanguage());
			aboutMsg.append("\nVariant is: " + locale.getVariant());
			aboutMsg.append("\n");
			aboutText.setText(aboutMsg.toString());
			
			xmlFormattedAboutMsg.append("</daisyreader>");
		}
		
		TextView localesText = (TextView) findViewById(R.id.installed_locales);
		if (localesText != null) {
			locales = new StringBuilder();
			locales.append("Locales installed on phone are:\n");
			
			Locale installedLocales [] = Locale.getAvailableLocales();
			for (Locale l : installedLocales) {
				locales.append("\n  " + l.getDisplayName());
			}
			
			localesText.setText(locales.toString());
		}
	}

	/**
	 * OnClick Handler for the About View.
	 * 
	 * Provides users with the ability to email information about their device.
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.eMail:
			Logging.logInfo(TAG, "Send an email.");
			emailInformation();
			break;
			
		case R.id.return_to_homescreen:
			Logging.logInfo(TAG, "Heading back to the homescreen.");
			this.finish();
			break;
		}
	}
	
	/**
	 * Sends information about the application in an e-mail.
	 * 
	 * Currently the code, and the contents are basic. The aim is to enable
	 * users to report problems easily and reliably in a way I/we can process
	 * efficiently and effectively.
	 */
	private void emailInformation() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		
		// TODO (jharty): Can we automatically cc the sender?
		String recipients [] = { "android.daisy.reader@gmail.com, julianharty@gmail.com" };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
		final String formattedSubjectMessage = 
			String.format("DaisyReader:About:Version=%s", ABOUT_EMAIL_VERSION);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, formattedSubjectMessage);
		emailIntent.setType("plain/text");
		
		// Construct the message to send.
		StringBuilder message = new StringBuilder(aboutMsg);
		message.append(xmlFormattedAboutMsg);
		
		// TODO(jharty): add formatting for the contents
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message.toString());
		startActivity(Intent.createChooser(emailIntent, getString(R.string.send_your_email_in)));
	}
}
