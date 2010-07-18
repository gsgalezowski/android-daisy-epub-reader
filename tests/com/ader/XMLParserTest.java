package com.ader;
/**
 * Tests for the XML Parser class.
 */
import java.io.ByteArrayInputStream;

import com.ader.testutilities.SampleContent;

import junit.framework.TestCase;

public class XMLParserTest extends TestCase {

	public void testXMLParserLoadsValidDaisy202Content() {
		ByteArrayInputStream input =
			new ByteArrayInputStream((SampleContent.simpleValidNccHtml).getBytes());
		XMLParser parser = new XMLParser(input);
		NavCentre navCenter = parser.processNCC();
		String firstTitleFound = navCenter.getNavPoint(0).getText();
		System.out.println("The first value of the navcenter is: " + firstTitleFound);
		assertEquals("The first title should match", SampleContent.firstTitle, firstTitleFound);
	}
}
