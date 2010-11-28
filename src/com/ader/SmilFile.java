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
					// encoding = mapUnsupportedEncoding(encoding);
				}
				bis.reset();
			}
			
		try {
			elements = new SmilParser().parse(bis, encoding);
		} catch (SAXException e) {
			// TODO(jharty): The following code seems to allow us to re-parse
			// an unsupported encoding (windows-1252). However it's messy and
			// ugly. I'll check in this version, then try to clean up the code.
			try {
				encoding = mapUnsupportedEncoding(encoding);
				fis.close();
				bis.close();
				fis = new FileInputStream(filename);
				bis = new BufferedInputStream(fis);
				elements = new SmilParser().parse(bis, encoding);
				return;
			} catch (SAXException e2) {
				// Do nothing, allow the first error to be reported.
			} catch (ParserConfigurationException e2) {
				// This seems an extremely unlikely scenario, that the second
				// attempt would fail with a configuration exception, however
				// catching it explicitly seems the least ugly approach for now
				throw new RuntimeException("Problem with the XML Parser on the Android platform." 
						+ "\n" + e.getLocalizedMessage());
			}
			throw new RuntimeException("Problem with file: " + filename 
					+ "\n" + e.getLocalizedMessage());
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Problem with the XML Parser on the Android platform." 
					+ "\n" + e.getLocalizedMessage());
		}
	}

	protected String mapUnsupportedEncoding(String encoding) {
		if (encoding.equalsIgnoreCase("windows-1252")) {
			encoding = "iso-8859-1";
		}
		return encoding;
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
