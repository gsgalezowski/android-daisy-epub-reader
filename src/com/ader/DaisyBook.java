package com.ader;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.util.Log;

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

	public void LoadAutoBookmark() throws IOException  {
		bookmark.load(path + "auto.bmk");
		currentnccIndex = bookmark.getNccIndex();
	}
	
	NCCEntry current() {
		Log.i(TAG, String.format("Current entry is index:%d, ncc:%s",
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
		Log.i(TAG, "goto " + index);
		bookmark.setNccIndex(index);
	}

	public void next(Boolean includeLevels) {
		Log.i(TAG, "next");
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
		Log.i(TAG, "previous");
		for (int i = bookmark.getNccIndex() -1; i > 0; i--)
			if (nccEntries.get(i).getLevel() <= selectedLevel) {
				bookmark.setNccIndex(i);
				break;
			}
	}

	void openSmil() {
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
	 * Start reading the current section of the book
	 * @param player
	 */
	public void read(MediaPlayer player) {
		if (smilFile.getAudioSegments().size() > 0) {
			try {
				Log.i(TAG, "Start playing " + bookmark.getFilename() + " " + bookmark.getPosition());
				player.setDataSource(bookmark.getFilename());
				player.prepare();
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalStateException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			player.seekTo(bookmark.getPosition());
			player.start();
		} else if (smilFile.getTextSegments().size() > 0) {
			// TODO(jharty): add TTS to speak the text section
			// Note: we need to decide how to handle things like \n
			// For now, perhaps we can simply display the text in a new view.
			Log.i("We need to read the text from: ", bookmark.getFilename());
		}
	}
}