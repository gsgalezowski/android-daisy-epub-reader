package com.ader;

import java.util.ArrayList;



public class SmilFile extends ArrayList<SmilEntry> {
	private String fileName;

	
	public String getFilename() {
		return this.fileName;
	}

	public void open(String filename) {
		clear();
		this.fileName = filename;
		DaisyParser parser = new DaisyParser();
		 
		ArrayList<DaisyElement> elements = parser.parse(filename);
		
		for (int i = 0; i < elements.size(); i++) 
			if (elements.get(i).getName().equalsIgnoreCase("audio"))
				add(new SmilEntry(elements.get(i)));			
	}
}
