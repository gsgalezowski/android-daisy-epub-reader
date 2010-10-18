package com.ader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ader.smil.AudioElement;
import com.ader.smil.SequenceElement;
import com.ader.smil.SmilParser;
import com.ader.smil.TextElement;

public class SmilFile implements Serializable {
	private String fileName;
	private SequenceElement elements;
	
	public String getFilename() {
		return this.fileName;
	}

	public void open(String filename) throws FileNotFoundException, IOException {
           try {
			elements = new SmilParser().parse(new FileInputStream(filename));
		} catch (SAXException e) {
			throw new RuntimeException("Problem with file: " + filename 
					+ "\n" + e.getLocalizedMessage());
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Problem with the XML Parser on the Android platform." 
					+ "\n" + e.getLocalizedMessage());
		}
	}
	
	public List<AudioElement> getAudioSegments() {
		return elements.getAllAudioElementDepthFirst();
	}
	
	public List<TextElement> getTextSegments() {
		return elements.getAllTextElementDepthFirst();
	}
}
