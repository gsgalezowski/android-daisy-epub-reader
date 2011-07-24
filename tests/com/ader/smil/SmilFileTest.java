package com.ader.smil;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.ader.smil.SmilFile;
import com.ader.smil.TextElement;

public class SmilFileTest extends TestCase {

	@MediumTest
    public void testParsingTextOnlyFile() throws Exception {
        SmilFile smilFile = new SmilFile();
        smilFile.open("Resources/WIPO-Treaty-D202Fileset/d202_1.smil");
        assertEquals("For Resources/WIPO-Treaty-D202Fileset/d202_1.smil expected: ", 3, smilFile.getTextSegments().size());
        for (int i = 0; i < smilFile.getTextSegments().size(); i++) {
        	TextElement textElement = smilFile.getTextSegments().get(i);
        	switch (i) {
        	case 0:
        		assertEquals("WIPOTreatyForVisuallyImpaired.html#h1classtitle", textElement.getSrc());
        		break;
        	
        	case 1:
        		assertEquals("WIPOTreatyForVisuallyImpaired.html#id_2", textElement.getSrc());
        		break;
        		
        	case 2:
        		assertEquals("WIPOTreatyForVisuallyImpaired.html#id_4", textElement.getSrc());
        		break; 
        	}
        }
    }

	@MediumTest
	public void testExtractingTextFromTextElement() throws Exception {
	SmilFile smilFile = new SmilFile();
	smilFile.open("Resources/WIPO-Treaty-D202Fileset/d202_4.smil");	
	assertEquals("Expected 2 text segments in d202_4.smil", 2, smilFile.getTextSegments().size());
	TextElement textElement = smilFile.getTextSegments().get(1);
	assertEquals("WIPOTreatyForVisuallyImpaired.html#id_92", textElement.getSrc());
	}
	
	@MediumTest
    public void testParsingAudioOnlyFile() throws Exception {
        SmilFile smilFile = new SmilFile();
        smilFile.open("Resources/light-man/icth0001.smil");
        assertTrue(smilFile.getAudioSegments().size() > 0);
        assertEquals(smilFile.getAudioSegments().get(0).getClipBegin(), 0.0);
        assertEquals(smilFile.getAudioSegments().get(1).getClipBegin(), 1.384);
        assertEquals(smilFile.getAudioSegments().get(2).getClipBegin(), 4.441);
    }
    
	@MediumTest
    public void testParsingFileWithWindows1252Encoding() throws Exception {
    	SmilFile smilfile = new SmilFile();
    	smilfile.open("Resources/problemcontent/bcbw0001.smil");
    	assertEquals("Expected correct count of audio segments", 
    			9, smilfile.getAudioSegments().size());
    }
    
	@MediumTest
    public void testParsingCBFW000BWithWindows1252Encoding() throws Exception {
    	SmilFile smilfile = new SmilFile();
    	smilfile.open("Resources/problemcontent/cbfw000B.smil");
    	assertEquals("Expected correct count of audio segments", 
    			11, smilfile.getAudioSegments().size());
    	
    	SmilFile smilfile2 = new SmilFile();
    	smilfile2.open("/sdcard/problemcontent/cbfw0029.smil");
    	
    }
}
