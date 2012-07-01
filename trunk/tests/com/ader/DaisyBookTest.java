package com.ader;

import static com.ader.OldDaisyBookImplementation.determineMetaLabel;
import static com.ader.OldDaisyBookImplementation.getAttributeValue;
import static com.ader.OldDaisyBookImplementation.getMetaValue;

import java.io.File;
import java.io.IOException;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import org.xml.sax.helpers.AttributesImpl;
import junit.framework.TestCase;

import com.ader.OldDaisyBookImplementation.MetaLabel;
import com.ader.utilities.Logging;

public class DaisyBookTest extends TestCase {
	private static final String CHARSET = "iso-8859-1",
	CHARSET_LABEL = "ncc:charset",
	NAME_KEY = "name",
	META = "meta",
	VALUE_KEY = "content";
	
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
			Logging.logInfo("DaisyBookTest", name);
		}
		
		String path = new File(".").getCanonicalPath();
		Logging.logInfo("Path", path);
		daisyBook.openFromFile(path + "/sdcard/Books/light-man/ncc.html");
		assertEquals("The light-man book should have 1 level of content", 1, daisyBook.getMaximumDepthInDaisyBook());
		daisyBook.setSelectedLevel(1);
		assertEquals("The light-man book should have.. ", 17, daisyBook.getNavigationDisplay().size());
	}
	
	@MediumTest
	public void testLevelsCanBeSetCorrentlyFor1LevelDaisy202Book() throws Exception {
		String path = new File(".").getCanonicalPath();
		Logging.logInfo("Path", path);
		daisyBook.openFromFile(path + "/sdcard/Books/light-man/ncc.html");
		assertEquals("The light-man book should have 1 level of content", 1, daisyBook.getMaximumDepthInDaisyBook());
		daisyBook.setSelectedLevel(1);
		assertEquals("The light-man book should have.. ", 17, daisyBook.getNavigationDisplay().size());
		daisyBook.setSelectedLevel(2);
		assertEquals("The light-man book should still have 17 levels even if the selected " 
				+ "depth > than the number of levels in the book.. ", 17, daisyBook.getNavigationDisplay().size());
		assertEquals("The minimum selected level should be 1, even if we select 0", 1, daisyBook.setSelectedLevel(0));
		assertEquals("The light-man book should have.. ", 17, daisyBook.getNavigationDisplay().size());
	}		
	
	private DaisyElement getCharsetMeta() {
		DaisyElement metaElement = new DaisyElement();
		metaElement.setName(META);
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute("", NAME_KEY, "", "String", CHARSET_LABEL);
		attributes.addAttribute("", VALUE_KEY, "", "String", CHARSET);
		metaElement.setAttributes(attributes);
		return metaElement;
	}
	
	@SmallTest
	public void testGetElementAttributeKey() {
		DaisyElement metaElement = getCharsetMeta();
		assertEquals(CHARSET_LABEL, getAttributeValue(NAME_KEY,
				metaElement.getAttributes()));
	}
	
	@SmallTest
	public void testGetElementAttributeValue() {
		DaisyElement metaElement = getCharsetMeta();
		assertEquals(CHARSET, getAttributeValue(VALUE_KEY,
				metaElement.getAttributes()));
	}
	
	@SmallTest
	public void testDetermineMetaLabel() {
		DaisyElement metaElement = getCharsetMeta();
		assertEquals(MetaLabel.CHARACTER_SET, determineMetaLabel(metaElement));
	}
	
	@SmallTest
	public void testGetMetaValue() {
		DaisyElement metaElement = getCharsetMeta();
		assertEquals(CHARSET, getMetaValue(metaElement));
	}
	
	@SmallTest
	public void determineMetaLabelNullElement() {
		assertEquals(MetaLabel.UNKNOWN, determineMetaLabel(null));
	}
	
	@SmallTest
	public void determineMetaLabelNullName() {
		DaisyElement element = getCharsetMeta();
		element.setName(null);
		assertEquals(MetaLabel.UNKNOWN, determineMetaLabel(element));
	}
	
	@SmallTest
	public void determineMetaLabelUnknownName() {
		DaisyElement element = getCharsetMeta();
		element.setName("randomnessstrikesentropy");
		assertEquals(MetaLabel.UNKNOWN, determineMetaLabel(element));
	}
	
	/**
	 * Consider testing attributes variants as they are problematic. Add 
	 * attributes without value and other similar tests.
	 */
}
