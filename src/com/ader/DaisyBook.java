package com.ader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface DaisyBook {

	int getDisplayPosition();

	int getNCCDepth();

	int setSelectedLevel(int level);

	int incrementSelectedLevel();

	int decrementSelectedLevel();

	int getCurrentDepthInDaisyBook();

	int getMaximumDepthInDaisyBook();

	/**
	 * FIXME: Currently returns the path for the book. This is no longer needed 
	 * by the book, rather, the player needs it to manage bookmarks. However I
	 * need to change the design so DaisyReader (which *knows* the path) can 
	 * pass the path to DaisyPlayer. Rather than make the change in this 
	 * refactoring; I'll do so later.
	 * @return
	 */
	@Deprecated
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
	 * Processes the Daisy Elements, e.g. from DaisyParser()
	 * @param elements The Daisy Book Elements
	 * @throws NumberFormatException
	 */
	List<DaisyItem> processDaisyElements(
			ArrayList<DaisyElement> elements) throws NumberFormatException;

}