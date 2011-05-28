package com.ader;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

import android.test.suitebuilder.annotation.MediumTest;

public class DaisyBookTest extends TestCase {
	private DaisyBook daisyBook;

	public void setUp() {
		daisyBook = new OldDaisyBookImplementation();
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
	 * @throws IOException - only for the logging code (we may remove it soon).
	 */
	@MediumTest
	public void testDaisy202BookCanBeOpenedWithoutError() throws Exception {
		for(String name : new File(".").list()) {
			Util.logInfo("DaisyBookTest", name);
		}
		
		String path = new File(".").getCanonicalPath();
		Util.logInfo("Path", path);
		daisyBook.openFromFile(path + "/Resources/light-man/ncc.html");
		assertEquals("The light-man book should have 1 level of content", 1, daisyBook.getNCCDepth());
		daisyBook.setSelectedLevel(1);
		assertEquals("The light-man book should have.. ", 17, daisyBook.getNavigationDisplay().size());
	}
	
	@MediumTest
	public void testLevelsCanBeSetCorrentlyFor1LevelDaisy202Book() throws Exception {
		String path = new File(".").getCanonicalPath();
		Util.logInfo("Path", path);
		daisyBook.openFromFile(path + "/Resources/light-man/ncc.html");
		assertEquals("The light-man book should have 1 level of content", 1, daisyBook.getNCCDepth());
		daisyBook.setSelectedLevel(1);
		assertEquals("The light-man book should have.. ", 17, daisyBook.getNavigationDisplay().size());
		daisyBook.setSelectedLevel(2);
		assertEquals("The light-man book should still have 17 levels even if the selected " 
				+ "depth > than the number of levels in the book.. ", 17, daisyBook.getNavigationDisplay().size());
		assertEquals("The minimum selected level should be 1, even if we select 0", 1, daisyBook.setSelectedLevel(0));
		assertEquals("The light-man book should have.. ", 17, daisyBook.getNavigationDisplay().size());
	}
}
