package com.ader;

import junit.framework.TestCase;

import com.ader.smil.TextElement;

public class SmilFileTest extends TestCase {

    public void testParsingTextOnlyFile() throws Exception {
        SmilFile smilFile = new SmilFile();
        smilFile.open("Resources/WIPO-Treaty-D202Fileset/d202_1.smil");
        assertTrue(smilFile.getTextSegments().size() > 0);
        TextElement textElement = smilFile.getTextSegments().get(0);
        assertEquals("WIPOTreatyForVisuallyImpaired.html#id_4", textElement.getSrc());
    }
    
    public void testParsingAudioOnlyFile() throws Exception {
        SmilFile smilFile = new SmilFile();
        smilFile.open("Resources/light-man/icth0001.smil");
        assertTrue(smilFile.getAudioSegments().size() > 0);
        assertEquals(smilFile.getAudioSegments().get(0).getClipBegin(), 0.0);
        assertEquals(smilFile.getAudioSegments().get(1).getClipBegin(), 1.384);
    }
    
    public void testParsingFileWithWindows1252Encoding() throws Exception {
    	SmilFile smilfile = new SmilFile();
    	smilfile.open("Resources/problemcontent/bcbw0001.smil");
    	assertEquals("Expected correct count of audio segments", 
    			9, smilfile.getAudioSegments().size());
    }
    
    public void testParsingCBFW000BWithWindows1252Encoding() throws Exception {
    	SmilFile smilfile = new SmilFile();
    	smilfile.open("Resources/problemcontent/cbfw000B.smil");
    	assertEquals("Expected correct count of audio segments", 
    			11, smilfile.getAudioSegments().size());
    }}
