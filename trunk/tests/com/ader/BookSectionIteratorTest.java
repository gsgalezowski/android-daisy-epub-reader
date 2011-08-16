/**
 * Tests for the navigation between the sections of a book.
 * 
 * TODO(jharty): I need to implement the actual tests :)
 */
package com.ader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.ader.testutilities.CreateDaisy202Book;

import junit.framework.TestCase;

/**
 * @author Julian Harty
 *
 */
public class BookSectionIteratorTest extends TestCase {

	private ByteArrayOutputStream out;
	private CreateDaisy202Book eBookContents;
	private NavCentre nc;

	protected void setUp() throws Exception {
	out = new ByteArrayOutputStream();
	eBookContents = new CreateDaisy202Book(out);
	eBookContents.writeXmlHeader();
	eBookContents.writeDoctype();
	eBookContents.writeXmlns();
	eBookContents.writeBasicMetadata();
	eBookContents.addTheseLevels("12312321");
	eBookContents.writeEndOfDocument();
	ByteArrayInputStream bookContents = new ByteArrayInputStream(out.toByteArray());
	XMLParser parser = new XMLParser(bookContents);
	nc = parser.processNCC();
	}

	/**
	 * Tests the sequential forward navigation through the sections of a book.
	 */
	public void ignoredTestSequentialNavigation() {
		fail("Not yet implemented");
	}
	
	public void testTemporarilyDummyTestToPreventNoTestWarning() throws Exception {
		//TODO 20110816 (jharty/amarcano): Remove this once the above test is failing for the right reasons
	}

	

}
