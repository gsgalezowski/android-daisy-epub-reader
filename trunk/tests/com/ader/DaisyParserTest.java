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
import java.util.ArrayList;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.ader.testutilities.SampleContent;

import junit.framework.TestCase;

public class DaisyParserTest extends TestCase {
	DaisyParser parser;
	
	public void setUp() throws Exception {
		parser = new DaisyParser();
	}
	
	/* A private helper method to open a test file, used by some tests. */
	private String openTestFile() throws IOException {
		String path = new File(".").getCanonicalPath();
		String filename = path + "/Resources/light-man/ncc.html";
		return filename;
	}
	
	/**
	 * Ensures we can parse the ncc.html content from a file. Runs in JUnit3 
	 * @throws IOException
	 */
	@MediumTest
	public void testCanParseFromFile() throws IOException {
		String filename = openTestFile();
		ArrayList<DaisyElement> elements = parser.openAndParseFromFile(filename);
		assertTrue("There should be SOME content", elements.size() > 0);
	}


	@SmallTest
	public void testCanParseFromTextContent() {
		System.out.println(SampleContent.simpleValidNccHtml);
		ArrayList<DaisyElement> elements = parser.parse(SampleContent.simpleValidNccHtml);
		assertEquals("The elements should be: html head, body, title, h1, a.", 6, elements.size());
	}
	
	@MediumTest
	public void testCanParseFromInputStream() throws Exception {
		String filename = openTestFile();
		ArrayList<DaisyElement> elements = parser.parse(new FileInputStream(filename));
		assertTrue("There should be SOME content", elements.size() > 0);
	}
}

