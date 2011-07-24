package com.ader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface DaisyBook {

	int getDisplayPosition();

	int setSelectedLevel(int level);

	int incrementSelectedLevel();

	int decrementSelectedLevel();

	/**
	 * Returns the current depth in the book.
	 */
	int getCurrentDepthInDaisyBook();

	/**
	 * gets the maximum depth of the book
	 * @return the depth of the book in sections.
	 */
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

	/**
	 * Go to the specified item in the book.
	 * @param item
	 */
	void goTo(DaisyItem nccEntry);

	/**
	 * Go to the next section in the book
	 * @param includeLevels - when true, pick the next section at a level
	 * equal or higher than the level selected by the user, else simply go to
	 * the next section.
	 * @return true if the book has a next section and navigated successfully
	 * to that section. If there is no next section, false is returned.
	 */
	boolean nextSection(Boolean includeLevels);

	/**
	 * Go to the previous section in the book.
	 * @return true when the book has a previous section and navigated
	 * successfully to that section. If there is no previous section available,
	 * false is returned.
	 */
	boolean previousSection();

	/**
	 * Processes the Daisy Elements, e.g. from DaisyParser()
	 * @param elements The Daisy Book Elements
	 * @throws NumberFormatException
	 */
	List<DaisyItem> processDaisyElements(List<DaisyElement> elements) throws NumberFormatException;

}