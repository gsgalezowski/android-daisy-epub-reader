package com.ader;

import java.io.Serializable;
import java.util.ArrayList;

public class SmilFile implements Serializable {
	private static final String TAG = "SimlFile";
	private String fileName;
	private ArrayList<SmilEntry> audioSegments = new ArrayList<SmilEntry>(); 
	private ArrayList<SmilEntry> textSegments = new ArrayList<SmilEntry>();
	
	public String getFilename() {
		return this.fileName;
	}

	public void open(String filename) {
		Util.logInfo(TAG, "Open " + filename);
		clear();
		this.fileName = filename;
		DaisyParser parser = new DaisyParser();
		String elementName;
		
		ArrayList<DaisyElement> elements = parser.parse(filename);
		
		for (int i = 0; i < elements.size(); i++) {
			elementName = elements.get(i).getName();
			SmilEntry entry = null;
			if (elementName.equalsIgnoreCase("audio")) {
			    entry = new SmilEntry(elements.get(i));
				audioSegments.add(entry);
			} else if (elementName.equalsIgnoreCase("text")){
				DaisyElement name = elements.get(i);
				entry = new SmilEntry(name);
				textSegments.add(entry);
			}
			Util.logInfo(TAG, "Adding segment " + entry);
		}
	}
	
	public ArrayList<SmilEntry> getAudioSegments() {
		return audioSegments;
	}
	
	public ArrayList<SmilEntry> getTextSegments() {
		return textSegments;
	}
	
	public void clear() {
		audioSegments.clear();
		textSegments.clear();
	}
}
