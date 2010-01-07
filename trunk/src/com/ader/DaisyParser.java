package com.ader;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class DaisyParser extends DefaultHandler {
	private static final String TAG = DaisyParser.class.getSimpleName();
	ArrayList<DaisyElement> daisyElements = new ArrayList<DaisyElement>();
	DaisyElement current;
	private StringBuilder builder = new StringBuilder();

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		// if (localName.equalsIgnoreCase("a"))

		current.setText(builder.toString());

		// 
	}

	public ArrayList<DaisyElement> parse(final String XMLFile) {
		Util.logInfo(TAG, "XMLFILE" + XMLFile);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();

			org.xml.sax.InputSource input = new InputSource(new FileReader(
					XMLFile));
			XMLReader saxParser = factory.newSAXParser().getXMLReader();
        	// The EntityResolver ensures the SAX parser can correctly locate
			// referenced entities e.g. xhtml1-strict.dtd from
        	// the referenced URI when the files are in relative paths. 
			// 
        	// Thanks to: http://forums.sun.com/thread.jspa?threadID=413937 and
			// http://www.jdom.org/pipermail/jdom-interest/2001-August/007129.html
        	// which helped us to resolve the problem.
			
			// TODO: We may need to limit this handler to file:// references and
			// find another way of resolving other references e.g. http://
        	saxParser.setEntityResolver(new EntityResolver() {
        		public InputSource resolveEntity(String publicId, String systemId)
        		throws java.io.IOException
        		{
        			String directory = XMLFile.substring(0, XMLFile.lastIndexOf("/") + 1);
        			Util.logInfo(TAG, "xml directory:" + directory);
    				String resourcePath =
    					directory + systemId.substring(systemId.lastIndexOf("/") + 1);
					return new InputSource(
    						new BufferedReader(new FileReader(resourcePath)));
        		}
        	});
        	saxParser.setContentHandler(this);
        	saxParser.parse(input);
			// parser.parse(input, this);
			return daisyElements;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		builder.setLength(0);
		current = new DaisyElement();
		// Possible bug between Android and Java...
		// On Android the element name is returned in localName, on the
		// workstation it's returned in 'name'
		// Adding a temporary workaround until I understand what's happening!
		if (localName.length() == 0) {
			localName = name;
		}
		current.setName(localName);
		current.setAttributes(attributes);
		daisyElements.add(current);
	}
}
