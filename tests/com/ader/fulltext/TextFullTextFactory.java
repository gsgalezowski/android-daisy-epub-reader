package com.ader.fulltext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
		SimpleDocumentFactory factory = new SimpleDocumentFactory();
		Document fullTextContents = factory.createDocumemt(SAMPLE_FULL_TEXT_REFERENCE);
		Element html = fullTextContents.getElementById(SECTION_PREAMBLE);
		assertNotNull("We expect some contents for this section of the book.", html.toString());
	}

	private class SimpleDocumentFactory {
		Document processedContents = null;
		
		public Document createDocumemt(String filename) throws FileNotFoundException, IOException {
			if (processedContents == null) {
				FullText fullText = new FullText();
				String elements[] = filename.split(SEPARATOR);
				File fileToReadFrom = new File(elements[0]);
				StringBuilder fileContents = fullText.getContentsOfHTMLFile(fileToReadFrom);
				processedContents = fullText.processHTML(fileContents.toString());
			}
			return processedContents;
		}
	}
}
