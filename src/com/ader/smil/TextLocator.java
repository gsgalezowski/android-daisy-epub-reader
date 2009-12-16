package com.ader.smil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utility for retrieving the text for TextElement
 * Currently it only works with local files.
 */
public class TextLocator extends DefaultHandler {
    
    private File baseDirectory;
    private String targetId;
    private String result;
    private boolean found;
    private Logger log = Logger.getAnonymousLogger();
    
    public TextLocator(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    
    public String getText(String src) throws IOException {
        found = false;
        if (src.contains("#")) {
            targetId = src.substring(src.indexOf("#") + 1);
            File file = new File(baseDirectory, src.substring(0, src.indexOf("#")));
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser parser = factory.newSAXParser();
                org.xml.sax.InputSource input = new InputSource(new FileInputStream(file));
                parser.parse(input, this);
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
            found = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (found) {
            result = new String(ch, start, length);
            log.info("found " + result);
            found = false;
        }
    }
    
}
