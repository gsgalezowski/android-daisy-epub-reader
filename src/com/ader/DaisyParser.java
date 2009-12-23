package com.ader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class DaisyParser extends DefaultHandler {
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

	public ArrayList<DaisyElement> parse(String XMLFile) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();

			org.xml.sax.InputSource input = new InputSource(new FileReader(
					XMLFile));

			parser.parse(input, this);
			return daisyElements;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		builder.setLength(0);
		current = new DaisyElement();
		current.setName(localName);
		current.setAttributes(attributes);
		daisyElements.add(current);
	}
}
