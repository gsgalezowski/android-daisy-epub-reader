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

import junit.framework.TestCase;

public class DaisyParserTest extends TestCase {
	DaisyParser parser;
	String path;
	String filename;
	
	/**
	 * Note: currently this ncc.html doesn't include all of the mandatory
	 * elements e.g. no meta tags yet...
	 */
	String simpleValidNccHtml = 
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
		"\"xhtml1-transitional.dtd\"> " +
		"<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
		"<head> <title>Simple Test Valid ncc.html</title></head>" +
		"<body>" +
		"<h1 class=\"title\" id=\"testcase\">" +
		"<a href=\"dtb_01.smil#rgn_txt_01\">Test Book Title</a></h1>" +
		"</body></html>"
		;
	
	public void setUp() throws Exception {
		parser = new DaisyParser();
		path = new File(".").getCanonicalPath();
		filename = path + "/Resources/light-man/ncc.html";
	}
	
	/**
	 * Ensures we can parse the ncc.html content from a file. Runs in JUnit3 
	 * @throws IOException
	 */
	public void testCanParseFromFile() throws IOException {
		ArrayList<DaisyElement> elements = parser.openAndParseFromFile(filename);
		assertTrue("There should be SOME content", elements.size() > 0);
	}

	public void testCanParseFromTextContent() {
		System.out.println(simpleValidNccHtml);
		ArrayList<DaisyElement> elements = parser.parse(simpleValidNccHtml);
		assertTrue("There should be SOME content", elements.size() > 0);
	}
	
	public void testCanParseFromInputStream() throws Exception {
		ArrayList<DaisyElement> elements = parser.parse(new FileInputStream(filename));
		assertTrue("There should be SOME content", elements.size() > 0);
	}
}

