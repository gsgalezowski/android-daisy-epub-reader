package com.ader;
/**
 * This parser processes DAISY 2.02 specification, albeit incompletely.
 * 
 * We intend to add support for DAISY 3 and epub formats.
 * 
 * Currently the parsing is limited and does not detect malformed books. We
 * want to add logic to detect malformed books. The reference will be the DAISY
 * 2.02 Specification http://www.daisy.org/z3986/specifications/daisy_202.html
 */
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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

	public ArrayList<DaisyElement> parse(String content) {
		return this.parse(new ByteArrayInputStream(content.getBytes()));
	}

	public ArrayList<DaisyElement> parse(InputStream stream) {
		
		org.xml.sax.InputSource input = new InputSource(stream);
		EntityResolver der = dummyEntityResolver();
		return parseNccContents(input, der);
	}
	
	public ArrayList<DaisyElement> openAndParseFromFile(final String XMLFile) throws FileNotFoundException {
		Util.logInfo(TAG, "XMLFILE " + XMLFile);
		org.xml.sax.InputSource input = new InputSource(new FileReader(XMLFile));
		EntityResolver er = entityResolverForExternalFile(XMLFile);
		return parseNccContents(input, er);
	}

	@Override
	public void endElement(String uri, String localName, String name)
	throws SAXException {
		current.setText(builder.toString());
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
	private ArrayList<DaisyElement> parseNccContents(org.xml.sax.InputSource input, EntityResolver er) throws FactoryConfigurationError,
			RuntimeException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader saxParser = factory.newSAXParser().getXMLReader();

			// The EntityResolver ensures the SAX parser can correctly locate
			// referenced entities e.g. xhtml1-strict.dtd from
        	// the referenced URI when the files are in relative paths. 
			// 
        	// Thanks to: http://forums.sun.com/thread.jspa?threadID=413937 and
			// http://www.jdom.org/pipermail/jdom-interest/2001-August/007129.html
        	// which helped us to resolve the problem.
			
        	saxParser.setEntityResolver(er);
        	saxParser.setContentHandler(this);
        	saxParser.parse(input);

        	return daisyElements;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private EntityResolver entityResolverForExternalFile(final String XMLFile) {
		// TODO: We may need to limit this handler to file:// references and
		// find another way of resolving other references e.g. http://
		
		EntityResolver er = new EntityResolver() {
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
		};
		return er;
	}
	
	private EntityResolver dummyEntityResolver() {
		// Thanks to http://www.junlu.com/msg/202604.html 
		EntityResolver er = new EntityResolver() {
	    public InputSource resolveEntity(String publicId, String systemId)
	    {
	        return new InputSource(new StringReader(" "));
	    }
	};
	return er;
	}

}