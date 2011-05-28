package com.ader.testutilities;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

/**
 * Ensures we can create basic ncc.html files.
 * 
 * The ncc.html files can be used as part of automated tests to facilitate
 * testing. The files are not intended to be suitable for other purposes,
 * however if they suit your needs, good luck.
 * 
 * @author Julian Harty
 *
 */
public class CreateEBookTest extends TestCase {
	private ByteArrayOutputStream out;
	private CreateDaisy202Book eBook=null;
	
	@Override
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		eBook = new CreateDaisy202Book(out);
	}
	
	public void testCreateMinimalDaisy202Book() throws Exception {
		eBook.writeXmlHeader();
		eBook.writeDoctype();
		eBook.writeXmlns();
		eBook.writeBasicMetadata();
		eBook.addLevelOne();
		eBook.writeEndOfDocument();
		assertTrue("There should be some content", out.size() > 50);
		System.out.println(out.toString());
		// TODO(jharty): We could validate the XML here too...
	}

	/**
	 * Simply generate an unrealistic book with all the valid levels.
	 */
	public void testCreateARoccocoDaisy202Book() {
		eBook.writeXmlHeader();
		eBook.writeDoctype();
		eBook.writeXmlns();
		eBook.writeBasicMetadata();
		eBook.addLevelOne();
		eBook.addLevelFive();
		eBook.addLevelFour();
		eBook.addLevelSix();
		eBook.addLevelThree();
		eBook.addLevelTwo();
		eBook.addLevelFive();
		eBook.addLevelFive();
		eBook.addLevelOne();
		eBook.writeEndOfDocument();
		assertTrue("There should be some content", out.size() > 50);
		System.out.println(out.toString());
	}

}
