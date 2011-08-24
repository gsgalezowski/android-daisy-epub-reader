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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.TestCase;

public class BookmarkTest extends TestCase {

	private static final String DUMMY_FILENAME = "dummy";
	private static final int DUMMY_NCCINDEX = 2;
	private static final int DUMMY_POSITION = 73;
	private static final String TMP = "/tmp/";
	private static final String bookmarkFilename = TMP + Bookmark.AUTO_BMK;
	private Bookmark bookmark;

	@MediumTest
	public void testWriteReadOldBookmarkFileStructure() throws IOException {
		DataInputStream dis;
		
		// First create the file
		DataOutputStream dos  = new DataOutputStream(new FileOutputStream(bookmarkFilename));
		String sdcardFilename = "/sdcard/dummyfilename.txt";
		dos.writeUTF(sdcardFilename);
		dos.writeInt(DUMMY_NCCINDEX);
		dos.writeInt(DUMMY_POSITION);
		dos.flush();
		dos.close();
		
		// now read the contents using the 'old' method
		dis = new DataInputStream(new FileInputStream(bookmarkFilename));
		assertEquals("Filename should match", sdcardFilename, dis.readUTF());
		assertEquals("First integer (representing nccIndex) should match", DUMMY_NCCINDEX, dis.readInt());
		assertEquals("Second integer, representing position) should match", DUMMY_POSITION, dis.readInt());
		dis.close();
		
		// now call the bookmark class to see if it reads the values correctly
		bookmark = Bookmark.getInstance(TMP);
		assertEquals("The filename should match.", sdcardFilename, bookmark.getFilename());
		assertEquals("The position/offset should match", DUMMY_POSITION, bookmark.getPosition());
		assertEquals("The NCC index should match.", DUMMY_NCCINDEX, bookmark.getNccIndex());
		
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
	
	@MediumTest
	public void testSaveForNewBookmarkUsingAByteArray() throws IOException {
		// TODO(jharty): There is code duplication between tests. Consider how
		// to encapsulate the duplication of the following lines.
		deleteAutoBookmarkFile();
		Bookmark bookmark = Bookmark.getInstance(TMP);
		
		// Now populate the bookmark
		updateAutomaticBookmarkToKnownValues(bookmark);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bookmark.save(baos);
	}

	/**
	 * Updates the Bookmark to known values.
	 * @param bookmark
	 */
	private void updateAutomaticBookmarkToKnownValues(Bookmark bookmark) {
		bookmark.setFilename(DUMMY_FILENAME);
		bookmark.setNccIndex(DUMMY_NCCINDEX);
		bookmark.setPosition(DUMMY_POSITION);
	}
	
	@MediumTest
	public void testOpenBookmarkForNonExistantBookmark() throws IOException {
		deleteAutoBookmarkFile();
		Bookmark bookmark = Bookmark.getInstance(TMP);
		
		assertTrue("We should have been able to load a non existant bookmark file without error.",
				null != bookmark);
	}
	
	/** 
	 * TODO(jharty): Fix bookmarks
	 * This failing test reminds me I need to redesign how bookmarks are stored.
	 * When we move to the new bookmark format we will simply create a file
	 * with no entry for the automatic bookmark. Or I may even find a more
	 * elegant solution... should save be allowed when there's no bookmark?
	 * 
	 * @throws IOException
	 */
	@MediumTest
	public void ignoredTestSaveForNonExistantBookmark() throws IOException {
		Bookmark bookmark = Bookmark.getInstance(TMP);
		
		ByteArrayOutputStream empty = new ByteArrayOutputStream();
		bookmark.save(empty);
	}
	
	@MediumTest
	public void testDeletingTheAutomaticBookmarkIsHandledCorrectly() throws IOException {
		File bookmarkFile = new File(bookmarkFilename);
		deleteAutoBookmarkFile();
		bookmarkFile.createNewFile();
		bookmark = Bookmark.getInstance(TMP);
		
		assertEmptyBookmark(bookmark);
		
		updateAutomaticBookmarkToKnownValues(bookmark);
		assertEquals("The filename in the bookmark should match", DUMMY_FILENAME, bookmark.getFilename());
		assertEquals("The offset should match", DUMMY_POSITION, bookmark.getPosition());
		assertEquals("The NCC Index should match", DUMMY_NCCINDEX, bookmark.getNccIndex());
		
		bookmark.deleteAutomaticBookmark();
		assertEmptyBookmark(bookmark);
	}

	/**
	 * 
	 */
	private void assertEmptyBookmark(Bookmark bookmark) {
		assertNull("An empty bookmark should not have a filename", bookmark.getFilename());
		assertEquals("The position should be at 0 for a new bookmark", 0, bookmark.getPosition());
		assertEquals("The NCC Index should be 0 for a new bookmark", 0, bookmark.getNccIndex());
	}
	/**
	 * This helper method is needed to test that when no bookmark file exists
	 * the code copes and doesn't break. We can consider removing this code
	 * once the bookmark code has been redesigned.
	 */
	private void deleteAutoBookmarkFile() {
		File bookmarkFile = new File(bookmarkFilename);
		if (bookmarkFile.exists()) {
			bookmarkFile.delete();
		}
	}
}
