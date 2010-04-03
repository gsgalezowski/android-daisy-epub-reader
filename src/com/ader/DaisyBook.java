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
	private String path = "";
	private int currentnccIndex = -1;
	private int NCCDepth = 0;
	private int selectedLevel = 1;
	private List<NCCEntry> nccEntries = new ArrayList<NCCEntry>();
	

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

	public void setSelectedLevel(int level) {
		this.selectedLevel = level;
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

	public String getPath() {
		return path;
	}

	public void open(String nccPath) throws FileNotFoundException {
		nccEntries.clear();
		this.path = nccPath;
		DaisyParser parser = new DaisyParser();
		try {
			Util.logInfo(TAG, new File(".").getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<DaisyElement> elements = parser.openAndParseFromFile(path + "ncc.html");
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
	public void next(Boolean includeLevels) {
		Util.logInfo(TAG, String.format(
				"next called; includelevels: %b selectedLevel: %d, currentnccIndex: %d bookmark.getNccIndex: %d", 
				includeLevels, selectedLevel, currentnccIndex, bookmark.getNccIndex()));
		if (! includeLevels) {
			if (currentnccIndex < nccEntries.size()) {
				// Note: this may need to go to the next entry with a type of 'LEVEL'
				bookmark.setNccIndex(currentnccIndex + 1);
			}
		} else
			for (int i = bookmark.getNccIndex() + 1; i < nccEntries.size(); i++)
				if (nccEntries.get(i).getLevel() <= selectedLevel 
					&& nccEntries.get(i).getType() == NCCEntryType.LEVEL) {
					bookmark.setNccIndex(i);
					break;
				}
	}

	public void previous() {
		Util.logInfo(TAG, "previous");
		for (int i = bookmark.getNccIndex() -1; i > 0; i--)
			if (nccEntries.get(i).getLevel() <= selectedLevel
				&& nccEntries.get(i).getType() == NCCEntryType.LEVEL) {
				bookmark.setNccIndex(i);
				break;
			}
	}

	void openSmil() {
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
	
}