package com.ader.testutilities;

import java.io.ByteArrayOutputStream;

import com.ader.NotImplementedException;

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
		writeHeaders();
		
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
		writeHeaders();
		
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

	
	public void testParameterizedBookSectionGenerator() {
		writeHeaders();
		eBook.addTheseLevels("12345653211234543211234321123211211");
		eBook.writeEndOfDocument();
		assertTrue("Generated book should have some content", out.size() > 100);
	}
	
	public void testCreateDummyEBook() {
		
		class DummyEBook extends CreateEBook {

			public DummyEBook() throws NotImplementedException {
				super();
			}
			
		}
		
		try {
			CreateEBook dummyBook = new DummyEBook();
			fail("Expected the CreateEBook to fail");
		} catch (NotImplementedException nie) {
			// Good enough, we expect this exception
		}
		
	}

	/**
	 * Utility Helper to write the headers. Could be subsumed into setUp()
	 */
	private void writeHeaders() {
		eBook.writeXmlHeader();
		eBook.writeDoctype();
		eBook.writeXmlns();
		eBook.writeBasicMetadata();
	}
}
