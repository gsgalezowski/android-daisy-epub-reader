package com.ader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class DaisyBook {
	
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
		if (current().getLevel() <=selectedLevel )
			return getNavigationDisplay().indexOf(current()); 
		else {
			// find the position of the current item in the whole book
		int i = nccEntries.indexOf(current());
		
		// go backward through the book till we find an item in the navigation display
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

	public void open(String nccPath) throws IOException {
		nccEntries.clear();
		this.path = nccPath;
		DaisyParser parser = new DaisyParser();
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
			if (elements.get(i).getName().equalsIgnoreCase("a"))
				nccEntries.add(new NCCEntry(elements.get(i), level));
		}

		bookmark.load(path + "auto.bmk");
		currentnccIndex = bookmark.getNccIndex();
	}

	NCCEntry current() {
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
	    Log.i(TAG, "goto " + index);
		bookmark.setNccIndex(index);
	}

	public void next() {
		if (bookmark.getNccIndex() < nccEntries.size() - 1)
			bookmark.setNccIndex(bookmark.getNccIndex() + 1);
	}


	public void previous() {
		if (bookmark.getNccIndex() > 0)
			bookmark.setNccIndex(bookmark.getNccIndex() - 1);
	}

	void openSmil() {
		if (currentnccIndex != bookmark.getNccIndex() || bookmark.getFilename() == null) {
			smilFile.open(path + current().getSmil());
			bookmark.setFilename(path + smilFile.get(0).getSrc());
			bookmark.setPosition(smilFile.get(0).getClipBegin());
			currentnccIndex = bookmark.getNccIndex();
		}
	}
}