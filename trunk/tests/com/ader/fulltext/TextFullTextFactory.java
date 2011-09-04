package com.ader.fulltext;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.test.suitebuilder.annotation.MediumTest;

/**
 * Tests the factory that creates the object containing the FullText content.
 * 
 * @author jharty
 */
public class TextFullTextFactory extends TestCase {
	private static final String WIPO_TREATY_FOR_VISUALLY_IMPAIRED_HTML = "/sdcard/Books/WIPO-Treaty-D202Fileset/WIPOTreatyForVisuallyImpaired.html";
	private static final String SECTION_PREAMBLE = "d2e289";
	private static final String SEPARATOR = "#";
	private static final String SAMPLE_FULL_TEXT_REFERENCE = WIPO_TREATY_FOR_VISUALLY_IMPAIRED_HTML + SEPARATOR + SECTION_PREAMBLE;
	
	@MediumTest
	public void testCanCreateObjectContainingFullTextContents() throws Exception {
		FullTextDocumentFactory factory = new FullTextDocumentFactory();
		Document fullTextContents = factory.createDocument(SAMPLE_FULL_TEXT_REFERENCE);
		Element html = fullTextContents.getElementById(SECTION_PREAMBLE);
		assertNotNull("We expect some contents for this section of the book.", html.html());
	}


}
