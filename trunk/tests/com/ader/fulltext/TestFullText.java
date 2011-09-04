package com.ader.fulltext;

import java.io.File;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
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
	private static final String SECTION_PREAMBLE = "d2e289";
	private static final String SEPARATOR = "#";
	private static final String SAMPLE_FULL_TEXT_REFERENCE = WIPO_TREATY_FOR_VISUALLY_IMPAIRED_HTML + SEPARATOR + SECTION_PREAMBLE;
	private static final String CONTENTS_TO_MATCH = "(a) Where a";
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
		Element elementToRead = document.getElementById(SECTION_PREAMBLE);
		assertTrue("We should have an element to work with...", elementToRead.html().contains("Copyright"));
		elementToRead.parent().toString();
		for (Element child : elementToRead.parent().children()) {
			child.html();
		}
	}
	
	@MediumTest
	public void testCanObtainHTMLForASmilReference() throws Exception {
		StringBuilder fileContents = textContents.getContentsOfHTMLFile(fileToReadFrom);
		textContents.processHTML(fileContents.toString());
		String html = textContents.getHtmlFor("id_224");
		assertTrue("The text seems broken, expected " + CONTENTS_TO_MATCH, html.contains(CONTENTS_TO_MATCH));
	}
	
	@SmallTest
	public void testCanExtractFilenameAndLocation() {
		String [] elements = SAMPLE_FULL_TEXT_REFERENCE.split(SEPARATOR);
		assertEquals("Filename incorrect", WIPO_TREATY_FOR_VISUALLY_IMPAIRED_HTML, elements[0]);
		assertEquals("Preamble name incorrect", SECTION_PREAMBLE, elements[1]);
	}
}
