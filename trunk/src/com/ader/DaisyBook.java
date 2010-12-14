package com.ader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DaisyBook implements Serializable {
	// public static final long serialVersionUID = 1;

	private static final String TAG = DaisyBook.class.getSimpleName();
	private Bookmark bookmark = new Bookmark();
	private SmilFile smilFile = new SmilFile();
	private String filename = "";
	private int currentnccIndex = -1;
	private int NCCDepth = 0;
	private int selectedLevel = 1;
	private List<NCCEntry> nccEntries = new ArrayList<NCCEntry>();
	private String path;
	

	public Bookmark getBookmark() {
		return bookmark;
	}

	public int getDisplayPosition() {
		if (current().getLevel() <= selectedLevel)
			return getNavigationDisplay().indexOf(current());
		else {
			// find the position of the current item in the whole book
			int i = nccEntries.indexOf(current());

			// go backward through the book till we find an item in the
			// navigation display
			while (nccEntries.get(i).getLevel() > selectedLevel)
				i--;

			// return the position of the found item in the nav display
			return getNavigationDisplay().indexOf(nccEntries.get(i));
		}
	}

	public int getNCCDepth() {
		return NCCDepth;
	}

	public int setSelectedLevel(int level) {
		if (level >= 1 && level <= NCCDepth) {
			this.selectedLevel = level;
		}
		return this.selectedLevel;
	}

	public int incrementSelectedLevel() {
		if (this.selectedLevel < NCCDepth) {
			this.selectedLevel++;
		}
		return this.selectedLevel;
	}

	public int decrementSelectedLevel() {
		if (this.selectedLevel > 1) {
			this.selectedLevel--;
		}
		return this.selectedLevel;
	}

	public int getCurrentDepthInDaisyBook() {
		return selectedLevel;
	}
	
	public int getMaximumDepthInDaisyBook() {
		return NCCDepth;
	}
	
	public String getPath() {
		return path;
	}

	/**
	 * Opens a Daisy Book from a full path and filename.
	 * 
	 * @param nccFullPathAndFilename The ncc file
	 * @throws FileNotFoundException if the file cannot be found or opened.
	 * @throws InvalidDaisyStructureException if there are serious problems in
	 * the book structure.
	 */
	public void openFromFile(String nccFullPathAndFilename) throws FileNotFoundException, InvalidDaisyStructureException {
		nccEntries.clear();
		this.filename = nccFullPathAndFilename;
		this.path = new File(nccFullPathAndFilename).getParent() + "/";
		DaisyParser parser = new DaisyParser();
		try {
			Util.logInfo(TAG, new File(".").getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<DaisyElement> elements = parser.openAndParseFromFile(filename);
		processDaisyElements(elements);
		validateDaisyContents();
	}
	
	/**
	 * Open a Daisy Book using a text stream. 
	 * 
	 * This is intended to facilitate automated tests.
	 * @param contents The text representing the contents of a DAISY 2.02
	 * ncc.html file. 
	 */
	protected void open(String contents) throws InvalidDaisyStructureException {
		DaisyParser parser = new DaisyParser();
		ArrayList<DaisyElement> elements = parser.parse(contents);
		processDaisyElements(elements);
		validateDaisyContents();
	}


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
	public void loadAutoBookmark() throws IOException  {
		String bookmarkFilename = path + "auto.bmk";
		bookmark.load(bookmarkFilename);
		currentnccIndex = bookmark.getNccIndex();
	}
	
	NCCEntry current() {
		Util.logInfo(TAG, String.format("Current entry is index:%d, ncc:%s",
				bookmark.getNccIndex(),
				nccEntries.get(bookmark.getNccIndex())));
		return nccEntries.get(bookmark.getNccIndex());
	}

	public List<NCCEntry> getNavigationDisplay() {
		ArrayList<NCCEntry> displayItems = new ArrayList<NCCEntry>();

		for (int i = 0; i < nccEntries.size(); i++)
			if (nccEntries.get(i).getLevel() <= selectedLevel 
				&& nccEntries.get(i).getType() == NCCEntryType.LEVEL)
				displayItems.add(nccEntries.get(i));
		return displayItems;
	}

	public void goTo(NCCEntry nccEntry) {
		int index = nccEntries.indexOf(nccEntry);
		Util.logInfo(TAG, "goto " + index);
		bookmark.setNccIndex(index);
	}

	/**
	 * Go to the next section in the eBook
	 * @param includeLevels - when true, pick the next section at a level
	 * equal or higher than the level selected by the user, else simply go to
	 * the next section.
	 */
	public boolean nextSection(Boolean includeLevels) {
		Util.logInfo(TAG, String.format(
				"next called; includelevels: %b selectedLevel: %d, currentnccIndex: %d bookmark.getNccIndex: %d", 
				includeLevels, selectedLevel, currentnccIndex, bookmark.getNccIndex()));
		for (int i = bookmark.getNccIndex() + 1; i < nccEntries.size(); i++) {
			if (nccEntries.get(i).getType() != NCCEntryType.LEVEL) {
				continue;
			}
			
			if (nccEntries.get(i).getLevel() > selectedLevel && includeLevels) {
				continue;
			}
			
			bookmark.setNccIndex(i);
			return true;
		}
		// TODO (jharty): this seems dodgy, e.g. we could fall off the end of the structure without updating the bookmark.
		return false;
	}

	public boolean previousSection() {
		Util.logInfo(TAG, "previous");
		for (int i = bookmark.getNccIndex() -1; i > 0; i--)
			if (nccEntries.get(i).getLevel() <= selectedLevel
				&& nccEntries.get(i).getType() == NCCEntryType.LEVEL) {
				bookmark.setNccIndex(i);
				return true;
			}
		return false;
	}

	void openSmil() throws FileNotFoundException, IOException {
		Util.logInfo(TAG, "Open SMIL file");
	if (currentnccIndex != bookmark.getNccIndex()
		|| smilFile.getFilename() == null) 
		{
			currentnccIndex = bookmark.getNccIndex();
			smilFile.open(path + current().getSmil());
			if (smilFile.getAudioSegments().size() > 0) {
				// TODO (jharty): are we assuming we always get the first entry?
				bookmark.setFilename(path + smilFile.getAudioSegments().get(0).getSrc());
				bookmark.setPosition((int) smilFile.getAudioSegments().get(0).getClipBegin());
			} else if (smilFile.getTextSegments().size() > 0) {
				bookmark.setFilename(path + smilFile.getTextSegments().get(0).getSrc());
				bookmark.setPosition(0);
			}
		}
	}

	/**
	 * TODO: Refactor once the new code is integrated.
	 * @return true if the book has at least one audio segment.
	 */
	public boolean hasAudioSegments() {
		return smilFile.getAudioSegments().size() > 0;
	}

	/**
	 * TODO: Refactor ASAP :)
	 * @return true if the book has at least one text segment.
	 */
	public boolean hasTextSegments() {
		return smilFile.getTextSegments().size() > 0;
	}
	
	protected void validateDaisyContents() throws InvalidDaisyStructureException {
		// Check there is at least one H1 element
		for (int i = 0; i < nccEntries.size(); i++) {
			NCCEntry entry = nccEntries.get(i);
			if (entry.getType() == NCCEntryType.LEVEL && entry.getLevel() == 1) {
				return;
			}
		}
		throw new InvalidDaisyStructureException("No H1 level in the book");
	}
	
	/**
	 * Processes the Daisy Elements, e.g. from DaisyParser()
	 * @param elements The Daisy Book Elements
	 * @throws NumberFormatException
	 */
	private void processDaisyElements(ArrayList<DaisyElement> elements)
	throws NumberFormatException {
		int level = 0;
		NCCEntryType type = NCCEntryType.UNKNOWN;
		
		for (int i = 0; i < elements.size(); i++) {
			String elementName = elements.get(i).getName();
			
			// is it a heading element
			if (elementName.matches("h[123456]")) {
				level = Integer.decode(elementName.substring(1));
				type = NCCEntryType.LEVEL;
				if (level > NCCDepth)
					NCCDepth = level;
				continue;
			}
			
			// Really just to speed the debugging...
			if (elementName.matches("meta")) continue;
			
			// Note: The following is a hack, we should check the 'class'
			// attribute for a value containing "page-"
			if (elementName.contains("span")
					&& elements.get(i).getAttributes().getValue(0).contains("page-")) {
				
				type = NCCEntryType.PAGENUMBER;
			}
			
			// is it an anchor element
			if (elementName.equalsIgnoreCase("a")) {
				// TODO (jharty): level should only be set for content, not
				// page-numbers, etc. However let's see where this takes us
				nccEntries.add(new NCCEntry(elements.get(i), type, level));
			}
		}
	}
}