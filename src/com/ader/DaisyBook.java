package com.ader;

import java.util.ArrayList;
import java.util.List;

public class DaisyBook extends ArrayList<NCCEntry> {
	private Bookmark bookmark = new Bookmark("", 0, 0);
	private SmilFile smilFile = new SmilFile();
	private DaisyPlayer dp = new DaisyPlayer(this);
	private String path = "";
	private int currentnccIndex = -1;
	private int NCCDepth = 0;
	private int selectedLevel = 1;

	public void close() {
		dp.release();
	}

	public Bookmark getBookmark() {
		return bookmark;
	}

	public int getNCCDepth() {
		return NCCDepth;
	}

	public void setSelectedLevel(int level) {
		this.selectedLevel = level;
	}

	public String getPath() {
		return path;
	}

	public void open(String nccPath) {
		clear();
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
				add(new NCCEntry(elements.get(i), level));
		}

		bookmark.load(path + "auto.bmk");
		currentnccIndex = bookmark.getNccIndex();
	}

	NCCEntry current() {
		return this.get(bookmark.getNccIndex());
	}

	public List<NCCEntry>GetNavigationDisplay() {
		ArrayList<NCCEntry> displayItems = new ArrayList<NCCEntry>();

		for (int i = 0; i < this.size(); i++)
			if (this.get(i).getLevel() <= selectedLevel)
				displayItems.add(this.get(i));

		
		return displayItems;
	}

	public void goTo(NCCEntry nccEntry) {
		bookmark.setNccIndex(this.indexOf(nccEntry));
	}

	public void Next() {
		if (bookmark.getNccIndex() < size() - 1)
			bookmark.setNccIndex(bookmark.getNccIndex() + 1);
	}

	public void togglePlay() {
		
		dp.togglePlay();
	}

	public void Previous() {
		if (bookmark.getNccIndex() > 0)
			bookmark.setNccIndex(bookmark.getNccIndex() - 1);
	}

	void OpenSmil() {
		if (currentnccIndex != bookmark.getNccIndex()) {
			smilFile.open(path + current().GetSmil());
			bookmark.setPosition(0);
			bookmark.setFilename(path + smilFile.get(0).getSrc());
			currentnccIndex = bookmark.getNccIndex();
		}
	}
}