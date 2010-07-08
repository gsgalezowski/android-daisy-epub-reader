package com.ader;
/**
 * Tests for the bookmark class, to help with refactoring that class.
 * 
 * Some of these tests may be removed once I've experimented with using a
 * BufferedReader to process data written by a DataOutputStream (used by the
 * current code).
 * 
 * Notes:
 * Currently (r69) the save could be called, and fail by raising an IOException,
 * for a new eBook that doesn't yet have an auto-bookmark, as the fields are not
 * initialised in the class during the load() method.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class BookmarkTest extends TestCase {

	private static final int DUMMY_NCCINDEX = 2;
	private static final int DUMMY_POSITION = 73;

	public void testWriteReadOldBookmarkFileStructure() throws IOException {
		DataInputStream dis;
		String bookmarkFilename = "/tmp/auto.bmk";
		
		// First create the file
		DataOutputStream dos  = new DataOutputStream(new FileOutputStream(bookmarkFilename));
		String sdcardFilename = "/sdcard/dummyfilename.txt";
		dos.writeUTF(sdcardFilename);
		dos.writeInt(DUMMY_POSITION);
		dos.writeInt(DUMMY_NCCINDEX);
		dos.flush();
		dos.close();
		
		// now read the contents using the 'old' method
		dis = new DataInputStream(new FileInputStream(bookmarkFilename));
		assertEquals("Filename should match", sdcardFilename, dis.readUTF());
		assertEquals("First integer (representing nccIndex) should match", DUMMY_POSITION, dis.readInt());
		assertEquals("Second integer, representing position) should match", DUMMY_NCCINDEX, dis.readInt());
		dis.close();
		
		// Let's try the same thing but with byte arrays rather than external files.
		// Thanks to http://ostermiller.org/convert_java_outputstream_inputstream.html
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos2 = new DataOutputStream(baos);
		dos2.writeUTF(sdcardFilename);
		dos2.writeInt(DUMMY_POSITION);
		dos2.writeInt(DUMMY_NCCINDEX);
		dos2.flush();
		dos2.close();
		
		ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
		DataInputStream dis2 = new DataInputStream(is);
		assertEquals("Filename should match", sdcardFilename, dis2.readUTF());
		assertEquals("First integer (representing nccIndex) should match", DUMMY_POSITION, dis2.readInt());
		assertEquals("Second integer, representing position) should match", DUMMY_NCCINDEX, dis2.readInt());
		dis2.close();
	}
	
	public void testOpenBookmarkForNonExistantBookmark() throws IOException {
		Bookmark bookmark = new Bookmark();
		
		bookmark.load("I do not exist");
		assertTrue("We should have been able to load a non existant bookmark file without error.",
				true);
	}

	public void testSaveForNewBookmarkUsingAByteArray() throws IOException {
		// Note: we need to call load() with a non-existant filename to initialize things
		Bookmark bookmark = new Bookmark();
		bookmark.load("I still do not exist");
		
		// Now populate the bookmark
		bookmark.setFilename("dummy");
		bookmark.setNccIndex(DUMMY_NCCINDEX);
		bookmark.setPosition(DUMMY_POSITION);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bookmark.save(baos);
	}

}
