package com.ader;

import java.util.ArrayList;

public class DaisyBook extends ArrayList<NCCEntry> {
	private Bookmark bookmark = new Bookmark("", 0, 0);
	private SmilFile smilFile = new SmilFile();
	private DaisyPlayer dp = new DaisyPlayer(this);
	private String path = "";
	private int currentnccIndex = -1;

	public void close() {
		dp.release();
	}

	public Bookmark getBookmark() {
		return bookmark;
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
			if (elements.get(i).getName().matches("h[123456]"))
				level = Integer.decode(elements.get(i).getName().substring(1));
			
			

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

	public String[] GetNavigationDisplay() {
		String[] result = new String[this.size()];
		for (int i = 0; i < this.size(); i++)
			result[i] = this.get(i).GetText();
		return result;
	}

	public void goTo(int index) {
		bookmark.setNccIndex(index);
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