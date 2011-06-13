package com.ader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.ader.smil.AudioElement;
import com.ader.testutilities.CreateDaisy202Book;

public class TestAbilityToGenerateDaisy202Book extends TestCase {

	private ByteArrayOutputStream out;
	private CreateDaisy202Book eBook;
	
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
	@SmallTest
	public void testAbilityToInjectEmptySmilFile() {
		eBook.addSmilFileEntry(1, "", "");
		eBook.writeEndOfDocument();

		ByteArrayInputStream newBook = new ByteArrayInputStream(out.toByteArray());
		XMLParser anotherParser = new XMLParser(newBook);
		
		NavCentre nc = anotherParser.processNCC();
		assertEquals("Expected a 1:1 match of NavPoints and sections", 0, nc.count());
	}
	
	@MediumTest
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
}
