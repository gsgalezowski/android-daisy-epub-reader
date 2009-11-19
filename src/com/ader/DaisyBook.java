package com.ader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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

	public void Open(String nccPath) {
		clear();
		this.path = nccPath;

		SAXReader reader = new SAXReader();

		try {
			Document document = reader.read(new File(path, "ncc.html"));
			List<Element> list = (List<Element>) document.getRootElement().elements("body");
			list = (List<Element>) list.get(0).elements();

			for (ListIterator<Element> i = list.listIterator(); i.hasNext();) {
				Element element = i.next();
				if (element.getName() == "h1") {

					add(new NCCEntry(element));
				}
			}

		} catch (DocumentException e) {
		}
	}

	NCCEntry current() {
		return this.get(bookmark.getNccIndex());
	}

	public String[] GetNavigationDisplay() {
		String[] result = new String[this.size() - 1];
		for (int i = 0; i < this.size() - 1; i++)
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
			smilFile.Open(new File(path, current().GetSmil()));
			bookmark.setPosition(0);
			bookmark.setFilename(path + smilFile.get(0).getSrc());
			currentnccIndex = bookmark.getNccIndex();
		}
	}
}