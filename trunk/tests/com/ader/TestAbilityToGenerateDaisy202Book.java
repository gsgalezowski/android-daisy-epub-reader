package com.ader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ader.smil.AudioElement;
import com.ader.testutilities.CreateDaisy202Book;

import junit.framework.TestCase;

public class TestAbilityToGenerateDaisy202Book extends TestCase {

	private ByteArrayOutputStream out;
	private CreateDaisy202Book eBook;
	private static final String singleValueSmilFileContents = 
		"";
	
	@Override
	protected void setUp() {
		out = new ByteArrayOutputStream();
		try {
			eBook = new CreateDaisy202Book(out);
		} catch (NotImplementedException e) {
			throw new RuntimeException(e);
		}
		eBook.writeXmlHeader();
		eBook.writeDoctype();
		eBook.writeXmlns();
		eBook.writeBasicMetadata();
	}
	/**
	 * Note: this test confirms we are able to insert incorrect content e.g.
	 * for testing. It confirms the code we're testing is *NOT* intended for
	 * real-world use.
	 */
	// @SmallTest
	public void testAbilityToInjectEmptySmilFile() {
		eBook.addSmilFileEntry(1, "", "");
		eBook.writeEndOfDocument();

		ByteArrayInputStream newBook = new ByteArrayInputStream(out.toByteArray());
		XMLParser anotherParser = new XMLParser(newBook);
		
		NavCentre nc = anotherParser.processNCC();
		assertEquals("Expected a 1:1 match of NavPoints and sections", 0, nc.count());
	}
	
	// @SmallTest
	public void testAbilityToInjectSingleItemSmilFile() throws Exception {
		SmilFile singleEntry = new SmilFile();
		singleEntry.open("Resources/testfiles/singleEntry.smil");
		List <AudioElement> audio = singleEntry.getAudioSegments();
		String id = audio.get(0).getId();
		assertEquals("ID of the audio element incorrect", "audio_0001", id);
		eBook.addSmilFileEntry(1, "singleEntry.smil", id);
		eBook.writeEndOfDocument();

		ByteArrayInputStream newBook = new ByteArrayInputStream(out.toByteArray());
		XMLParser anotherParser = new XMLParser(newBook);
		
		NavCentre nc = anotherParser.processNCC();
		assertEquals("Expected a 1:1 match of NavPoints and sections", 1, nc.count());
	}

	/* Hacky helper method 
	 * TODO(jharty) clean up as I refactor this code. 
	 */
	private static byte[] toByteArray(File file) throws Exception {
	    long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	    	throw new IOException("File too large to process, sorry.");
	    }
	    
	    byte[] array = new byte[(int) length];
	    InputStream in = new FileInputStream(file);
	    long offset = 0;
	    while (offset < length) {
	        int count = in.read(array, (int) offset, (int)(length - offset));
	        offset += length;
	    }
	    in.close();
	    return array;
	}
	
	
}