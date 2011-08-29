package com.ader.fulltext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/** FullText represents the contents of a DAISY full-text book.
 * 
 * @author jharty
 */
public class FullText {

	/**
	 * Simply reads the contents from the HTML File.
	 * 
	 * @param fileToReadFrom the full filename of the file to read.
	 * @return the contents of the file in a StringBuffer.
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
	StringBuilder getContentsOfHTMLFile(File fileToReadFrom)
			throws FileNotFoundException, IOException {
		StringBuilder fileContents = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(fileToReadFrom));
		String line;
		
		while ((line = reader.readLine()) != null) {
			fileContents.append(line);
			fileContents.append('\n');
		}
		
		fileToReadFrom = null;
		reader = null;
		return fileContents;
	}
}
