package com.ader.smil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ader.DummyDtdResolver;


public class SmilParser extends DefaultHandler {
    
    private enum State {
        INIT,
        SEQ,
        PARA,
    }
    private Logger log = Logger.getLogger(SmilParser.class.getSimpleName());
    private SmilElement currentElement;
    private Attributes attributes;
    private State state;
    private SequenceElement rootSequence;
    
    public SequenceElement parse(String content) throws IOException {
       return this.parse(new ByteArrayInputStream(content.getBytes()));
    }
    
    public SequenceElement parse(InputStream stream) throws IOException {
        state = State.INIT;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            XMLReader parser = factory.newSAXParser().getXMLReader();
            parser.setEntityResolver(new DummyDtdResolver());
            parser.setContentHandler(this);
            org.xml.sax.InputSource input = new InputSource(stream);
            parser.parse(input);
            return rootSequence;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        String name = localName.length() != 0 ? localName : qName;
        this.attributes = attributes;
        switch (state) {
            case INIT: {
                log.info("init " + name);
                if ("seq".equalsIgnoreCase(name)) {
                    state = State.SEQ;
                    rootSequence = createSequenceElment();
                    currentElement = rootSequence;
                }
                break;
            }
            case SEQ: {
                log.info("seq " + name);
                SequenceElement seq = (SequenceElement) currentElement;
                if ("par".equalsIgnoreCase(name)) {
                    state = State.PARA;
                    currentElement = new ParallelElement(currentElement);
                    seq.add(currentElement);
                } else if ("text".equalsIgnoreCase(name)) {
                    seq.add(createTextElement());
                } else if ("audio".equalsIgnoreCase(name)) {
                    seq.add(createAudioElement());
                }
                break;
            }
            case PARA: {
                log.info("para " + name);
                ParallelElement par = (ParallelElement) currentElement;
                if ("text".equalsIgnoreCase(name)) {
                    par.setTextElement(
                            createTextElement());
                } else if ("audio".equalsIgnoreCase(name)) {
                    SequenceElement seq = new SequenceElement(currentElement);
                    par.setAudioSequence(seq);
                    seq.add(createAudioElement());
                } else if ("seq".equalsIgnoreCase(name)) {
                    SequenceElement seq = createSequenceElment();
                    par.setAudioSequence(seq);
                    currentElement = seq;
                    state = State.SEQ;
                }
                break;
            }
        }
    }
    
    private SequenceElement createSequenceElment() {
        double duration = 0;
        if (attributes.getValue("dur") != null) {
            duration = Double.parseDouble(attributes.getValue("dur").replace("s", ""));
        }
        return new SequenceElement(currentElement, duration);
    }

    private AudioElement createAudioElement() {
        return new AudioElement(currentElement,
                attributes.getValue("src"),
                // TODO: support more time formats
                Double.parseDouble(attributes.getValue("clip-begin").replace("npt=", "").replace("s", "")),
                Double.parseDouble(attributes.getValue("clip-end").replace("npt=", "").replace("s", "")),
                attributes.getValue("id"));
    }

    private TextElement createTextElement() {
        //TODO: handle inline text
        return new TextElement(currentElement,
                attributes.getValue("src"),
                attributes.getValue("id"));
    }
}
