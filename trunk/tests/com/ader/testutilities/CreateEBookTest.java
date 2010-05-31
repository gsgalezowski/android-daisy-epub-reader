package com.ader.testutilities;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class CreateEBookTest extends TestCase {

	public void testCreateMinimalDaisy202Book() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CreateDaisy202Book eBook = new CreateDaisy202Book(out);
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

}
