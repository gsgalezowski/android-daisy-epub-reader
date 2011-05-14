package com.ader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import android.test.suitebuilder.annotation.MediumTest;

public class ParserMigrationTest extends TestCase {

	private DaisyParser oldParser;
	private XMLParser newParser;

	protected void setUp() throws Exception {
		oldParser = new DaisyParser();
		super.setUp();
	}
	
	@MediumTest
	public void testSideBySideContent() throws IOException {
		String path = new File(".").getCanonicalPath();
		String filename = path + "/Resources/light-man/ncc.html";
		FileInputStream stream1 = new FileInputStream(filename);
		FileInputStream stream2 = new FileInputStream(filename);
		ArrayList <DaisyElement> oldElements = oldParser.parse(stream1);
		DaisyBook tempBook = new DaisyBook();
		List<NCCEntry> nccEntries = tempBook.processDaisyElements(oldElements);
		
		newParser = new XMLParser(stream2);
		NavCentre navCentre = newParser.processNCC();
		// NB: This assert is unlikely to be correct as the parsers will
		// return different sets of elements e.g. NavCentre should include
		// additional elements, not provided by the DaisyParser, however we can
		// refine the match once this test runs and as the new parser matures.
		assertEquals(
			"Expected identical results for NavCentre and the ncc Entries for the old DaisyParser",
			navCentre.count(), nccEntries.size());
	}

}
