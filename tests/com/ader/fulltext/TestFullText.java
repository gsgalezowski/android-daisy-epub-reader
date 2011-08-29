package com.ader.fulltext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
	private File fileToReadFrom;
	private FullText textContents;

	@Override
	protected void setUp() {
		fileToReadFrom = new File(WIPO_TREATY_FOR_VISUALLY_IMPAIRED_HTML);
		textContents = new FullText();
	}
	
	@MediumTest
	public void testCanProcessContentsOfExternalFullTextHTMLContents() throws Exception {
		
		StringBuilder fileContents = textContents.getContentsOfHTMLFile(fileToReadFrom);
		assertTrue("We should have some contents.", fileContents.length() > 0);
	
		Document document = textContents.processHTML(fileContents.toString());
		assertNotNull("No error should have occurred.", document);
		Element elementToRead = document.getElementById("d2e289");
		assertTrue("We should have an element to work with...", elementToRead.html().contains("Copyright"));
		elementToRead.parent().toString();
		for (Element child : elementToRead.parent().children()) {
			child.html();
		}
		
	}
}
