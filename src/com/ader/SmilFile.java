package com.ader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

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

	public void open(String filename) {
		try {
            elements = new SmilParser().parse(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
	
	public List<AudioElement> getAudioSegments() {
		return elements.getAllAudioElementDepthFirst();
	}
	
	public List<TextElement> getTextSegments() {
		return elements.getAllTextElementDepthFirst();
	}
}
