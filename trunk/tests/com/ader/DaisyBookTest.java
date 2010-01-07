package com.ader;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class DaisyBookTest extends TestCase {
	private DaisyBook daisyBook;

	public void setUp() {
		daisyBook = new DaisyBook();
	}
	
	/**
	 * Regression Test to ensure a DAISY book can be opened without a SAX error.
	 * 
	 * Opening a DAISY 202 book invokes the XML parser, which then opens files 
	 * referenced in the ncc.html file e.g. xhtml-strict-1.dtd, and it continues
	 * recursively to open the referenced files until the parsing completes.
	 * 
	 * We discovered the SAX parser in standard Java seems to ignore part of
	 * the path (the part relative the the current project folder) which meant
	 * the parser raised FileNotFound exceptions. We added code to DaisyParser
	 * to 'fix' the paths. This test makes sure that that code enables the
	 * SAX parser to find the referenced files.
	 * 
	 *  We may make the test more potent in future revisions as currently it
	 *  lacks asserts.
	 *  
	 * @throws IOException - only for the logging code (we may remove it soon).
	 */
	public void testDaisy202BookCanBeOpenedWithoutError() throws IOException {
		for(String name : new File(".").list()) {
			Util.logInfo("DaisyBookTest", name);
		}
		
		String path = new File(".").getCanonicalPath();
		Util.logInfo("Path", path);
		daisyBook.open(path + "/Resources/light-man/");
		daisyBook.loadAutoBookmark();
		daisyBook.openSmil();
	}
}
