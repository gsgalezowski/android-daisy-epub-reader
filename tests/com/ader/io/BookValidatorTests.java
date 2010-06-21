package com.ader.io;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.ader.testutilities.CreateDaisy202Book;

import junit.framework.TestCase;

public class BookValidatorTests extends TestCase {
	private static final String dummyValidPath = "/tmp/sdcard/daisyreadertests/";
	private static final String dummyFileWhichIsNotAFolder = dummyValidPath + "dummyfile.txt";
	private static final String DUMMYVALIDDAISYBOOKFOLDER = dummyValidPath + "validbook/";
	private static final String DUMMYVALIDDAISY202INDEXFILE = DUMMYVALIDDAISYBOOKFOLDER + "ncc.html";
	private static final String dummyValidBook = DUMMYVALIDDAISYBOOKFOLDER;
	private static final String dummyEmptyFolder = dummyValidPath + "emptyfolder/";
	BookValidator validator = new BookValidator();
	CreateDaisy202Book eBook;
	
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		// TODO (jharty): We need to create the folders and files that will be
		// used by these tests. At first we can probably live with creating and
		// purging the folders and files for each test, but we should try to
		// streamline the file IO to reduce the overall execution time and,
		// ideally, keep the side-effects of our tests (e.g. when run on a real
		// device) to a minimum.

		// TODO (jharty): find out how to stop needing so many 'new File(...)' calls
		if (new File(dummyValidPath).exists() || new File(dummyValidPath).mkdirs()) {}
		if (new File(dummyEmptyFolder).exists() || new File(dummyEmptyFolder).mkdirs()) {}
		if (!new File(dummyFileWhichIsNotAFolder).exists()) {
			// TODO (jharty): There MUST be a cleaner way to code this!
			File dummyFile = new File(dummyFileWhichIsNotAFolder);
			FileOutputStream myFile = new FileOutputStream(dummyFile);
			new PrintStream(myFile).println("some junk text which should be ignored.");
			myFile.close();
		}
		// Check whether the folder already exists
		if (new File(DUMMYVALIDDAISYBOOKFOLDER).exists() || new File(DUMMYVALIDDAISYBOOKFOLDER).mkdirs()) {
			// If the ncc.html file doesn't exist, create it
			if(!new File(DUMMYVALIDDAISY202INDEXFILE).exists()) {
				// How about creating a helper method WriteableFile(...)? to make the code readable
				File validDaisy202BookOnDisk = new File(DUMMYVALIDDAISY202INDEXFILE);
				FileOutputStream out = new FileOutputStream(validDaisy202BookOnDisk); 
				eBook = new CreateDaisy202Book(out);
				eBook.writeXmlHeader();
				eBook.writeDoctype();
				eBook.writeXmlns();
				eBook.writeBasicMetadata();
				eBook.addLevelOne();
				eBook.writeEndOfDocument();
				out.close(); // Now, save the changes.
			}
		}

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO (jharty): see note in setUp() about streamlining the file and
		// folder creation, etc.
		super.tearDown();
		File cleanup = new File("/tmp/sdcard/");
		// TODO (jharty): I still need to delete the folders and files
		cleanup.delete();
	}

	public void testShouldFailForInvalidFileSystemRoot() {
		assertFalse("an invalid path should fail", validator
				.validFileSystemRoot("nonexistent file system"));
	}

	public void testShouldPassForValidFileSystemRoot() {
		assertTrue("an valid path should pass", validator
				.validFileSystemRoot(dummyValidPath));
	}

	public void testShouldFailForFileWhichIsNotAFolder() {
		assertFalse("an valid path should pass", validator
				.validFileSystemRoot(dummyFileWhichIsNotAFolder));
	}

	public void testEmptySubfolderListWhenNoSubfolders() {
		validator.validFileSystemRoot(dummyEmptyFolder);
		validator.addFolders(dummyEmptyFolder);

		assertTrue("Folder list should be empty for paths with no subfolders",
				validator.getFolderList().isEmpty());
	}

	public void testNotEmptySubfolderListWhenExistingSubfolders() {

		validator.validFileSystemRoot(dummyValidPath);
		validator.addFolders(dummyValidPath);

		assertTrue(
				"Folder list should not be empty for paths which contain subfolders",
				!validator.getFolderList().isEmpty());

	}

	public void testFolderContainsBook() {
		assertTrue("This folder should contain a valid book", validator
				.containsBook(dummyValidBook));
	}
	
	public void testValidBookFound() {
		validator.validFileSystemRoot(dummyValidPath);
		// TODO (jharty): the following call looks inappropriate since
		// dummyValidPath points elsewhere. I'm guessing this test intends to
		// read an external book from the filesystem (rather than one we
		// create in these tests. We need to decide how much to rely on
		// external content as these tests mature.
		// validator.findBooks("d:\\books"); -- commented out until I check the previous source
		validator.findBooks(dummyValidPath);
		assertTrue("there should be at least one book in the book list", validator.getBookList().size() > 0);  
	}
}
