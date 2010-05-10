package testpackage;

import junit.framework.TestCase;

public class BookValidatorTests extends TestCase {
	BookValidator validator = new BookValidator();
	String dummyValidPath = "d:\\grg\\testbooks";
	String dummyFileWhichIsNotAFolder = "d:\\grg\\testbooks\\dummyfile.txt";
	String dummyEmptyFolder = "d:\\grg\\testbooks\\emptyfolder";
	String dummyValidBook = "d:\\grg\\testbooks\\14174";

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
		validator.findBooks("d:\\books");
		assertTrue("there should be at least one book in the book list", validator.getBookList().size() > 0);  
	}
}
