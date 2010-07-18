package com.ader;
/**
 * Tests for the XML Parser class.
 */
import java.io.ByteArrayInputStream;

import com.ader.testutilities.SampleContent;

import junit.framework.TestCase;

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
}
