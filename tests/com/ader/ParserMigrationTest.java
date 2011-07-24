package com.ader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ader.smil.SmilFile;

import junit.framework.TestCase;

import android.test.suitebuilder.annotation.MediumTest;

public class ParserMigrationTest extends TestCase {

	private static final String PATH_TO_LIGHT_MAN_FILES = "/Resources/light-man/";
	private DaisyParser oldParser;
	private XMLParser newParser;
	private DaisyItem entry;

	protected void setUp() throws Exception {
		oldParser = new DaisyParser();
		super.setUp();
	}
	
	@MediumTest
	public void testSideBySideContent() throws IOException {
		String path = new File(".").getCanonicalPath();
		String filename = path + PATH_TO_LIGHT_MAN_FILES + "ncc.html";
		FileInputStream stream1 = new FileInputStream(filename);
		FileInputStream stream2 = new FileInputStream(filename);
		List <DaisyElement> oldElements = oldParser.parse(stream1);
		DaisyBook tempBook = new OldDaisyBookImplementation();
		List<DaisyItem> items = tempBook.processDaisyElements(oldElements);
		
		newParser = new XMLParser(stream2);
		NavCentre navCentre = newParser.processNCC();
		// NB: This assert is unlikely to be correct as the parsers will
		// return different sets of elements e.g. NavCentre should include
		// additional elements, not provided by the DaisyParser, however we can
		// refine the match once this test runs and as the new parser matures.
		assertEquals(
			"Expected identical results for NavCentre and the ncc Entries for the old DaisyParser",
			navCentre.count(), items.size());
		
		// First go at using an Iterator to compare the elements
		// At this stage I don't know whether the order of elements needs to be
		// maintained. In the new Parser we separate hierarchy elements from
		// page-numbers.
		// TODO(jharty): Clearly there's too much code to have in a unit test
		// I need to clean up the code and make the test(s) simpler.
		ArrayList<String> headings = new ArrayList<String>();
		ArrayList<String> pageNumbers = new ArrayList<String>();
		Iterator<DaisyItem> iterator = items.iterator();
		while(iterator.hasNext()) {
			entry = iterator.next();
			switch(entry.getType()) {
			case PAGENUMBER:
				pageNumbers.add(entry.getText());
				break;
		
			case LEVEL:
				// TODO(jharty): Check the level of each heading
				headings.add(entry.getText());
				break;
			}
		}
		// So we need some way to navigate through the entries...
		// For now, simply iterate by using the content generated by the old
		// parser.
		for (int i = 0; i < headings.size(); i++) {
			assertEquals("Contents of headings should match", 
					headings.get(i), navCentre.getNavPoint(i).getText());
		}
		
		// OK, now compare the page-numbers match
		for (int i = 0; i < pageNumbers.size(); i++) {
			assertEquals(String.format("Page numbers should match, item [%s] didn't.", i),
					pageNumbers.get(i), navCentre.getPageTarget(i).getText());
		}
		
		// The next test is to see if we can open a smil file processed by the
		// new parser. Turns out, we can...
		SmilFile smilFile = new SmilFile();
		path = new File(".").getCanonicalPath();
		filename = path + PATH_TO_LIGHT_MAN_FILES + "/" + navCentre.getNavPoint(0).getSmil();
		smilFile.open(filename);
        assertEquals("The external file should have 3 short audio elements.", 
        		3, smilFile.getAudioSegments().size());
        assertEquals(smilFile.getAudioSegments().get(0).getClipBegin(), 0.0);
        assertEquals(smilFile.getAudioSegments().get(1).getClipBegin(), 1.384);
        assertEquals(smilFile.getAudioSegments().get(2).getClipBegin(), 4.441);
		
	}

}
