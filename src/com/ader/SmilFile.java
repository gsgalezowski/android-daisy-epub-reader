package com.ader;

import java.util.ArrayList;

public class SmilFile extends ArrayList<SmilEntry> {
	private String fileName;
	private ArrayList<SmilEntry> audioSegments = new ArrayList<SmilEntry>(); 
	private ArrayList<SmilEntry> textSegments = new ArrayList<SmilEntry>();
	
	public String getFilename() {
		return this.fileName;
	}

	public void open(String filename) {
		clear();
		this.fileName = filename;
		DaisyParser parser = new DaisyParser();
		String elementName;
		
		ArrayList<DaisyElement> elements = parser.parse(filename);
		
		for (int i = 0; i < elements.size(); i++) {
			elementName = elements.get(i).getName();
			if (elementName.equalsIgnoreCase("audio")) {
				audioSegments.add(new SmilEntry(elements.get(i)));
				add(new SmilEntry(elements.get(i)));
			} else if (elementName.equalsIgnoreCase("text")){
				DaisyElement name = elements.get(i);
				SmilEntry entry = new SmilEntry(name);
				textSegments.add(entry);
			}
		}
	}
	
	public ArrayList<SmilEntry> getAudioSegments() {
		return audioSegments;
	}
	
	public ArrayList<SmilEntry> getTextSegments() {
		return textSegments;
	}
}
