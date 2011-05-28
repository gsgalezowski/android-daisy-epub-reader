package com.ader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface DaisyBook {

	Bookmark getBookmark();

	int getDisplayPosition();

	int getNCCDepth();

	int setSelectedLevel(int level);

	int incrementSelectedLevel();

	int decrementSelectedLevel();

	int getCurrentDepthInDaisyBook();

	int getMaximumDepthInDaisyBook();

	String getPath();

	/**
	 * Opens a Daisy Book from a full path and filename.
	 * 
	 * @param nccFullPathAndFilename The ncc file
	 * @throws InvalidDaisyStructureException if there are serious problems in
	 * the book structure.
	 * @throws IOException 
	 */
	void openFromFile(String nccFullPathAndFilename)
			throws InvalidDaisyStructureException, IOException;

	/**
	 * Loads the automatically created bookmark.
	 * 
	 * This bookmark keeps track of where the user is in this book. If it
	 * doesn't exist, e.g. if this is the first time the user has opened this
	 * book, then the bookmark will be created once the user starts reading the
	 * book.
	 * @throws IOException If there is a problem opening the file representing
	 * the bookmark.
	 */
	void loadAutoBookmark() throws IOException;

	List<DaisyItem> getNavigationDisplay();

	/* (non-Javadoc)
	 * @see com.ader.SectionNavigation#goTo(com.ader.DaisyItem)
	 */
	void goTo(DaisyItem nccEntry);

	/* (non-Javadoc)
	 * @see com.ader.SectionNavigation#nextSection(java.lang.Boolean)
	 */
	boolean nextSection(Boolean includeLevels);

	/* (non-Javadoc)
	 * @see com.ader.SectionNavigation#previousSection()
	 */
	boolean previousSection();

	/**
	 * TODO: Refactor once the new code is integrated.
	 * @return true if the book has at least one audio segment.
	 */
	boolean hasAudioSegments();

	/**
	 * TODO: Refactor ASAP :)
	 * @return true if the book has at least one text segment.
	 */
	boolean hasTextSegments();

	/**
	 * Processes the Daisy Elements, e.g. from DaisyParser()
	 * @param elements The Daisy Book Elements
	 * @throws NumberFormatException
	 */
	List<DaisyItem> processDaisyElements(
			ArrayList<DaisyElement> elements) throws NumberFormatException;

}