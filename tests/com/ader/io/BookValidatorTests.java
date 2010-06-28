package com.ader.io;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.util.Log;

import com.ader.testutilities.CreateDaisy202Book;

import junit.framework.TestCase;

public class BookValidatorTests extends TestCase {
	private final String dummyValidPath = System.getProperty("java.io.tmpdir") + "/daisyreadertests/";
	private final String dummyValidTextFile = dummyValidPath + "dummyfile.txt";
	private final String dummyValidDaisyBookFolder = dummyValidPath + "validbook";
	private final String dummyValidDaisy202IndexFile = dummyValidDaisyBookFolder + "/ncc.html";
	private final String dummySecondDaisyBookFolder = dummyValidPath + "anotherbook";
	private final String dummyValidDaisy202UpperCaseIndexFile = dummySecondDaisyBookFolder + "/NCC.HTML";
	private final String dummyValidBook = dummyValidDaisyBookFolder;
	private final String dummyEmptyFolder = dummyValidPath + "emptyfolder/";
	BookValidator validator = new BookValidator();
	CreateDaisy202Book eBook;
	
	
	@Override
	protected void setUp() throws Exception {
		// TODO (jharty): We need to create the folders and files that will be
		// used by these tests. At first we can probably live with creating and
		// purging the folders and files for each test, but we should try to
		// streamline the file IO to reduce the overall execution time and,
		// ideally, keep the side-effects of our tests (e.g. when run on a real
		// device) to a minimum.

		// TODO (jharty): find out how to stop needing so many 'new File(...)' calls
		if (new File(dummyValidPath).exists() || new File(dummyValidPath).mkdirs()) {}
		if (new File(dummyEmptyFolder).exists() || new File(dummyEmptyFolder).mkdirs()) {}
		if (!new File(dummyValidTextFile).exists()) {
			// TODO (jharty): There MUST be a cleaner way to code this!
			File dummyFile = new File(dummyValidTextFile);
			FileOutputStream myFile = new FileOutputStream(dummyFile);
			new PrintStream(myFile).println("some junk text which should be ignored.");
			myFile.close();
		}
		// Check whether the folder already exists
		if (new File(dummyValidDaisyBookFolder).exists() || new File(dummyValidDaisyBookFolder).mkdirs()) {
			// If the ncc.html file doesn't exist, create it
			if(!new File(dummyValidDaisy202IndexFile).exists()) {
				// How about creating a helper method WriteableFile(...)? to make the code readable
				File validDaisy202BookOnDisk = new File(dummyValidDaisy202IndexFile);
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
		super.tearDown();
		File cleanup = new File(dummyValidPath);
		recursiveDelete(cleanup);
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
				.validFileSystemRoot(dummyValidTextFile));
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

		validator.findBooks(dummyValidPath);
		assertTrue("there should be at least one book in the book list",
				validator.getBookList().size() > 0);  
		assertEquals("The path for the valid book is incorrect",
				dummyValidBook, validator.getBookList().get(0));
	}
	
	public void testValidBookWithUpperCaseIndexFileFound() throws Exception {
		
		if (new File(dummySecondDaisyBookFolder).exists() || new File(dummySecondDaisyBookFolder).mkdirs()) {
			File validUpperCaseDaisy202BookOnDisk = new File(dummyValidDaisy202UpperCaseIndexFile);
			FileOutputStream out = new FileOutputStream(validUpperCaseDaisy202BookOnDisk); 
			eBook = new CreateDaisy202Book(out);
			eBook.writeXmlHeader();
			eBook.writeDoctype();
			eBook.writeXmlns();
			eBook.writeBasicMetadata();
			eBook.addLevelOne();
			eBook.writeEndOfDocument();
			out.close(); // Now, save the changes.
			
		}
		
		assertTrue("The newly created book should exist.",
				new File(dummyValidDaisy202UpperCaseIndexFile).exists());
		
		validator.validFileSystemRoot(dummySecondDaisyBookFolder);
		validator.findBooks(dummySecondDaisyBookFolder);

		assertTrue("there should be one book in the book list",
				validator.getBookList().size() == 1);  
		assertEquals("The path for the valid book is incorrect",
				dummySecondDaisyBookFolder, validator.getBookList().get(0));

	}
	
	/*
	 * Recursively delete file or directory
	 * @param fileOrDir the file or dir to delete
	 * @return true iff all files are successfully deleted
	 * 
	 * This code based on an answer from:
	 * http://stackoverflow.com/questions/617414/create-a-temporary-directory-in-java
	 */
	private static boolean recursiveDelete(File fileOrDir)
	{
	    if(fileOrDir.isDirectory())
	    {
	        // recursively delete contents
	        for(File innerFile: fileOrDir.listFiles())
	        {
	            if(!BookValidatorTests.recursiveDelete(innerFile))
	            {
	                return false;
	            }
	        }
	    }

	    return fileOrDir.delete();
	}

}
