/**
 * Tests the navigation through a Daisy Book.
 * 
 * Navigation occurs in 2 ways:
 * 1. When the user navigates between sections.
 * 2. By the player, after it completes the current section.
 * 
 * When the user navigates, the next section depends on the current 'level'
 * selected by the user. The next section in the book needs to be at the same,
 * or higher level.
 * 
 * The player, simply requires the next contiguous section, regardless of
 * level. This is to comply with the requirement for the player to play an
 * entire book linearly (in order) unless told to do otherwise by the user.
 * 
 * These tests are intended to guide development of the functionality of
 * the navigation in the DaisyReader code, where I will also replace the
 * existing design (which uses side-effects of methods like nextSection()) with
 * standard java iterators.  
 */
package com.ader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.ader.testutilities.CreateDaisy202Book;

/**
 * TDD tests for navigation through generated 'books'.
 * @author Julian Harty
 *
 */
public class BookNavigationTest extends TestCase {

	private ByteArrayOutputStream out;
	private CreateDaisy202Book eBook;
	private ByteArrayInputStream fourLevelBook;
	XMLParser parsedBook;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		generate4LevelBook();
		fourLevelBook = new ByteArrayInputStream(out.toByteArray());
		parsedBook = new XMLParser(fourLevelBook);
		super.setUp();
	}

	public void testGenerated4LevelBookHasCorrectStructure() {
		NavCentre nc = parsedBook.processNCC();
		assertEquals("Generated Daisy book should have 13 sections.", 13, nc.count());
	}
	
	/**
	 * Generate a book with 4 levels.
	 * 
	 * The book can then be navigated by our tests.
	 * 
	 * At the moment, the contents of the book are not specified by the
	 * caller.
	 * 
	 * We can consider passing a constructor that specifies the order of the
	 * sections, e.g. ("1231123443421") would create a book of the same
	 * structure as that generated currently here.
	 * 
	 * @throws Exception if the book cannot be created.
	 */
	private void generate4LevelBook() throws Exception {
		out = new ByteArrayOutputStream();
		eBook = new CreateDaisy202Book(out);
		eBook.writeXmlHeader();
		eBook.writeDoctype();
		eBook.writeXmlns();
		eBook.writeBasicMetadata();
		eBook.addLevelOne();
		eBook.addLevelTwo();
		eBook.addLevelThree();
		eBook.addLevelOne();
		eBook.addLevelOne();
		eBook.addLevelTwo();
		eBook.addLevelThree();
		eBook.addLevelFour();
		eBook.addLevelFour();
		eBook.addLevelThree();
		eBook.addLevelFour();
		eBook.addLevelTwo();
		eBook.addLevelOne();
		eBook.writeEndOfDocument();
	}
}
