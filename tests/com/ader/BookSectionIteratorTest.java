/**
 * 
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
	eBookContents.addTheseLevels("12312321");
	eBookContents.writeEndOfDocument();
	ByteArrayInputStream bookContents = new ByteArrayInputStream(out.toByteArray());
	XMLParser parser = new XMLParser(bookContents);
	nc = parser.processNCC();
	}

	/**
	 * Test method for {@link com.ader.BookSectionIterator#hasNext()}.
	 */
	public void testHasNext() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.ader.BookSectionIterator#next()}.
	 */
	public void testNext() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.ader.BookSectionIterator#remove()}.
	 */
	public void testRemove() {
		fail("Not yet implemented");
	}

}
