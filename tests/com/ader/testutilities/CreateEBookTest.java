package com.ader.testutilities;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

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
