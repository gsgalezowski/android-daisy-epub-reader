package com.ader.fulltext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.Html;
import android.text.Spanned;
/**
 * Tests for processing the contents of Full Text DAISY books.
 * 
 * TDD development, let's see if I manage to keep up this habit.
 * 
 * These tests must run as Android test cases because they need to exercise
 * Android-specific code.
 * 
 * @author jharty
 */
public class TestFullText extends TestCase {

	// TODO 20110828 (jharty): These tests rely on an external file which must be on the Android device. We need an elegant way to manage these dependencies.
	private static final String WIPO_TREATY_FOR_VISUALLY_IMPAIRED_HTML = "/sdcard/Books/WIPO-Treaty-D202Fileset/WIPOTreatyForVisuallyImpaired.html";

	@MediumTest
	public void testCanProcessContentsOfExternalFullTextHTMLContents() throws Exception {
		File fileToReadFrom = new File(WIPO_TREATY_FOR_VISUALLY_IMPAIRED_HTML);

		StringBuilder fileContents = getContentsOfHTMLFile(fileToReadFrom);
		
		assertTrue("We should have some contents.", fileContents.length() > 0);
		
		Spanned formattedContents = Html.fromHtml(fileContents.toString());
		assertNotNull("No error should have occurred.", formattedContents);
	}

	/**
	 * Simply reads the contents from the HTML File.
	 * 
	 * @param fileToReadFrom the full filename of the file to read.
	 * @return the contents of the file in a StringBuffer.
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
	StringBuilder getContentsOfHTMLFile(File fileToReadFrom)
			throws FileNotFoundException, IOException {
		StringBuilder fileContents = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(fileToReadFrom));
		String line;
		
		while ((line = reader.readLine()) != null) {
			fileContents.append(line);
			fileContents.append('\n');
		}
		
		fileToReadFrom = null;
		reader = null;
		return fileContents;
	}
}
