package com.ader.smil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ader.io.ExtractXMLEncoding;

public class SmilFile implements Serializable {
	
	private SequenceElement elements;
	
	/**
	 * Opens a SMIL file.
	 * 
	 * Notes:
	 *   - Currently a NPE can be thrown e.g. if the file has no content. This
	 *     is ugly. Should we convert/wrap these exceptions into an application
	 *     specific Exception? 
	 *   - Also, how about adding some basic validation for the content? e.g.
	 *     length, structure, etc.
	 *   TODO(jharty): ruminate on the above notes... Address at some point.  
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void open(String filename) throws FileNotFoundException, IOException {
		// TODO(jharty): Add validation here?
		String encoding = ExtractXMLEncoding.obtainEncodingStringFromFile(filename);

		FileInputStream fis = new FileInputStream(filename);
		BufferedInputStream bis = new BufferedInputStream(fis);

		try {
			elements = new SmilParser().parse(bis, encoding);
		} catch (SAXException e) {
			// TODO(jharty): The following code seems to allow us to re-parse
			// an unsupported encoding (windows-1252). However it's messy and
			// ugly. I'll check in this version, then try to clean up the code.
			try {
				encoding = ExtractXMLEncoding.mapUnsupportedEncoding(encoding);
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

	/**
	 * @return all the audio segments extracted from a Smil File.
	 * 
	 * Note: This approach works adequately when we want the audio in
	 * isolation from any other synchronised content. It's not sufficient when
	 * we want to synchronise content.
	 */
	public List<AudioElement> getAudioSegments() {
		return elements.getAllAudioElementDepthFirst();
	}

	/**
	 * @return all the text segments from a Smil File.
	 * 
	 * Note: This approach works adequately when we want the text in
	 * isolation from any other synchronised content. It's not sufficient when
	 * we want to synchronise content.
	 */
	public List<TextElement> getTextSegments() {
		return elements.getAllTextElementDepthFirst();
	}

	/**
	 * Does this Smil file contain at least 1 audio segment?
	 * @return true if it has, else false.
	 */
	public boolean hasAudioSegments() {
		return getAudioSegments().size() > 0;
	}

	/**
	 * Does this Smil file contain at least 1 text segment?
	 * @return true if it has, else false.
	 */
	public boolean hasTextSegments() {
		return getTextSegments().size() > 0;
	}
}
