package com.ader;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ader.smil.AudioElement;
import com.ader.smil.SequenceElement;
import com.ader.smil.SmilParser;
import com.ader.smil.TextElement;

public class SmilFile implements Serializable {
	protected static final String XML_TRAILER = "\"?>";
	protected static final String EXTRACT_ENCODING_REGEX = ".*encoding=\"";
	protected static final String XML_FIRST_LINE_REGEX = "<\\?xml version=\"1\\.0\" encoding=\"(.*)\"?>";
	
	private String fileName;
	private SequenceElement elements;
	
	public String getFilename() {
		return this.fileName;
	}

	public void open(String filename) throws FileNotFoundException, IOException {
		String encoding = "UTF-8";
           try {
			FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			if (bis.markSupported()) {
				String line = null;
				// read the first line after setting the mark, then reset
				// before calling the parser.
				bis.mark(200);
				DataInputStream dis = new DataInputStream(bis);
				line = dis.readLine();
				if (line.matches(SmilFile.XML_FIRST_LINE_REGEX)) {
					encoding = extractEncoding(line);
				}
				bis.reset();
			}
			
			elements = new SmilParser().parse(bis, encoding);
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
	
	/**
	 * Helper method to extract the XML file encoding
	 * @param line the first line of an XML file
	 * @return The value of the encoding in lower-case.
	 */
	protected static String extractEncoding(String line) {
		Pattern p = Pattern.compile(EXTRACT_ENCODING_REGEX);
		String matches[] = p.split(line);
		String encoding = matches[1].replace(XML_TRAILER, "");
		return encoding.toLowerCase();
	}
}
