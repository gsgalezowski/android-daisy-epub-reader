package com.ader;
/**
 * Tests for the XML Parser class.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.ader.testutilities.CreateDaisy202Book;
import com.ader.testutilities.SampleContent;

public class XMLParserTest extends TestCase {
	private NavCentre navCenter = null;
	
	@Override
	public void setUp() {
		ByteArrayInputStream input =
			new ByteArrayInputStream((SampleContent.simpleValidNccHtml).getBytes());
		XMLParser parser = new XMLParser(input);
		navCenter = parser.processNCC();
	}
	
	public void testXMLParserLoadsValidDaisy202Content() {
		String firstTitleFound = navCenter.getNavPoint(0).getText();
		System.out.println("The first value of the navcenter is: " + firstTitleFound);
		assertEquals("The first title should match", SampleContent.firstTitle, firstTitleFound);
	}
	
	public void testWhatHappensIfWeAskForContentBeyondTheArray() {
		int outOfRange = navCenter.count() + 1;
		NavPoint n = navCenter.getNavPoint(outOfRange);
		assertNull("Asking for an item beyond the end of the book, should return null", n);
	}
	
	public void testWhatHappensIfWeAskForANegativeIndex() {
		NavPoint n = navCenter.getNavPoint(-1);
		assertNull("Asking for a negative index should return null", n);
	}
	
	public void testALargerGeneratedBookContainsTheExpectedSections() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CreateDaisy202Book eBook = new CreateDaisy202Book(out);
		eBook.writeXmlHeader();
		eBook.writeDoctype();
		eBook.writeXmlns();
		eBook.writeBasicMetadata();
		int numLevelOneSections = 10;
		for (int i = 1; i <= numLevelOneSections; i++) {
			eBook.addLevelOne();
		}
		eBook.writeEndOfDocument();
		ByteArrayInputStream newBook = new ByteArrayInputStream(out.toByteArray());
		XMLParser anotherParser = new XMLParser(newBook);
		NavCentre nc = anotherParser.processNCC();
		assertEquals("Expected a 1:1 match of NavPoints and sections", 
				numLevelOneSections, nc.count());
	}
	
	@MediumTest
	public void testCanParseIcelandicContent() throws IOException {
		String filename = "/sdcard/testfiles/icelandic/ncc.html";
		
		XMLParser parser = new XMLParser(new FileInputStream(filename));
		NavCentre nc = parser.processNCC();
		assertEquals("The file should have 2 sections", 2, nc.count());
	}
}
