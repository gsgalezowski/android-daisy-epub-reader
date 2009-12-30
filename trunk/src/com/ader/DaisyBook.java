package com.ader;

import java.io.File;
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

	public void incrementSelectedLevel() {
		if (this.selectedLevel < NCCDepth) {
			this.selectedLevel++;
		}
	}

	public void decrementSelectedLevel() {
		if (this.selectedLevel > 1) {
			this.selectedLevel--;
		}
	}

	public String getPath() {
		return path;
	}

	public void open(String nccPath) {
		nccEntries.clear();
		this.path = nccPath;
		DaisyParser parser = new DaisyParser();
		try {
			Util.logInfo(TAG, new File(".").getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<DaisyElement> elements = parser.parse(path + "ncc.html");
		int level = 0;

		for (int i = 0; i < elements.size(); i++) {
			// is it a heading element
			if (elements.get(i).getName().matches("h[123456]")) {
				level = Integer.decode(elements.get(i).getName().substring(1));
				if (level > NCCDepth)
					NCCDepth = level;
			}

			// is it an anchor element
			if (elements.get(i).getName().equalsIgnoreCase("a")) {
				nccEntries.add(new NCCEntry(elements.get(i), level));
			}
				
		}
	}

	public void loadAutoBookmark() throws IOException  {
		bookmark.load(path + "auto.bmk");
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
			if (nccEntries.get(i).getLevel() <= selectedLevel)
				displayItems.add(nccEntries.get(i));
		return displayItems;
	}

	public void goTo(NCCEntry nccEntry) {
		int index = nccEntries.indexOf(nccEntry);
		Util.logInfo(TAG, "goto " + index);
		bookmark.setNccIndex(index);
	}

	public void next(Boolean includeLevels) {
		Util.logInfo(TAG, "next");
		if (! includeLevels) {
			if (currentnccIndex < nccEntries.size())
				bookmark.setNccIndex(currentnccIndex + 1);
		} else
			for (int i = bookmark.getNccIndex() + 1; i < nccEntries.size(); i++)
				if (nccEntries.get(i).getLevel() <= selectedLevel) {
					bookmark.setNccIndex(i);
					break;
				}
	}

	public void previous() {
		Util.logInfo(TAG, "previous");
		for (int i = bookmark.getNccIndex() -1; i > 0; i--)
			if (nccEntries.get(i).getLevel() <= selectedLevel) {
				bookmark.setNccIndex(i);
				break;
			}
	}

	void openSmil() {
		Util.logInfo(TAG, "Test Logger");
	if (currentnccIndex != bookmark.getNccIndex()
		|| smilFile.getFilename() == null) 
		{
			currentnccIndex = bookmark.getNccIndex();
			smilFile.open(path + current().getSmil());
			if (smilFile.getAudioSegments().size() > 0) {
				bookmark.setFilename(path + smilFile.getAudioSegments().get(0).getSrc());
				bookmark.setPosition(smilFile.getAudioSegments().get(0).getClipBegin());
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