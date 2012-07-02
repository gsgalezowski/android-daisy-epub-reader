package com.ader;
/**
 * Tests for the DaisyParser class, to help us express the intent of the class
 * and to test the ongoing changes and refactoring.
 * 
 * Currently these tests assume DAISY 202 content e.g. ncc.html
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.ader.testutilities.SampleContent;

public class DaisyParserTest extends TestCase {
	private static final String LIGHT_MAN_NCC_HTML = "/sdcard/Books/light-man/ncc.html";
	DaisyParser parser;
	
	public void setUp() throws Exception {
		parser = new DaisyParser();
	}
	
	
	/**
	 * Ensures we can parse the ncc.html content from a file. Runs in JUnit3 
	 * @throws IOException
	 */
	@MediumTest
	public void testCanParseFromFile() throws IOException {
		List<DaisyElement> elements = parser.openAndParseFromFile(LIGHT_MAN_NCC_HTML);
		assertTrue("There should be SOME content", elements.size() > 0);
	}


	@SmallTest
	public void testCanParseFromTextContent() {
		System.out.println(SampleContent.simpleValidNccHtml);
		List<DaisyElement> elements = parser.parse(SampleContent.simpleValidNccHtml);
		assertEquals("The elements should be: html head, body, title, h1, a.", 6, elements.size());
	}
	
	/**
	 * TODO(jharty): Add a smaller test for the replacement of ' with " in 
	 * ExtractXMLEncoding.extractEncoding() - I haven't checked-in the relevant
	 * test class yet :(
	 * 
	 * @throws IOException
	 */
	@MediumTest
	public void testCanParseIcelandicContent() throws IOException {
		String filename = "/sdcard/testfiles/icelandic/ncc.html";
		List<DaisyElement> elements = 
			parser.openAndParseFromFile(filename);
		assertEquals("html", elements.get(0).getName());
		// Might be worth adding additional validation, however this is the
		// old parser, so I will focus on adding an equivalent test for the
		// new parser first...
	}
	
	@MediumTest
	public void testCanParseFromInputStream() throws Exception {
		List<DaisyElement> elements = parser.parse(new FileInputStream(LIGHT_MAN_NCC_HTML));
		assertTrue("There should be SOME content", elements.size() > 0);
	}
}

