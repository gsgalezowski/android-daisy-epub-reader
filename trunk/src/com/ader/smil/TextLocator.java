package com.ader.smil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utility for retrieving the text for TextElement
 * Currently it only works with local files.
 */
public class TextLocator extends DefaultHandler {
    
    private File baseDirectory;
    private String targetId;
    private String result;
    private int depth = 0;
    private Logger log = Logger.getAnonymousLogger();
    
    public TextLocator(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    
    public String getText(String src) throws IOException {
        if (src.contains("#")) {
            targetId = src.substring(src.indexOf("#") + 1);
            File file = new File(baseDirectory, src.substring(0, src.indexOf("#")));
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
            	org.xml.sax.InputSource input = new InputSource(new FileInputStream(file));
            	XMLReader saxParser = factory.newSAXParser().getXMLReader();
            	
            	// The EntityResolver prevents the SAX parser from trying to
            	// download external entities e.g. xhtml1-strict.dtd from
            	// the referenced URI. Having our own entity resolver makes
            	// the tests both faster, as they don't need to visit the web;
            	// and more reliable, as the w3c site returns a HTTP 503 to
            	// requests for this file from the SAX parser (it loads OK in
            	// a web browser).
            	// Thanks to: http://forums.sun.com/thread.jspa?threadID=413937
            	// for the following code and fix.
            	saxParser.setEntityResolver(new EntityResolver() {
            		public InputSource resolveEntity(String publicId, String systemId)
            		throws SAXException, java.io.IOException
            		{
            			if (systemId.endsWith(".dtd"))
            			{
            				return new InputSource(new ByteArrayInputStream(
            						"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
            			}
            			else return null;
            		}
            	});
            	saxParser.setContentHandler(this);
            	saxParser.parse(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            File file = new File(baseDirectory, src); 
            long len = file.length();
            FileInputStream fis = new FileInputStream(file);
            byte buf[] = new byte[(int) len];
            fis.read(buf);
            fis.close();
            result = new String(buf);
        }
        return result;
    }
    
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        String id = attributes.getValue("id");
        log.info(name + ": " + id);
        if (id != null && id.equals(targetId)) {
            depth++;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (depth > 0) {
            result = new String(ch, start, length);
            log.info("found " + result);
        }
    }
    
    @Override
    public void endElement(String uri, String localName, String name) {
    	if (depth > 0) {
    		depth--;
    	}
    }
    
}
