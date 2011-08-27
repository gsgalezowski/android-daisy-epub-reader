package com.ader.smil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.ader.smil.SmilFile;
import com.ader.smil.TextElement;
import com.ader.testutilities.SampleContent;

public class SmilFileTest extends TestCase {

	@SmallTest
	public void testWeCanGetTheElementsForASingleItem() throws Exception {
		SmilFile smilFile = new SmilFile();
		InputStream contents = new ByteArrayInputStream(SampleContent.SMIL_FILE_WITH_SINGLE_ITEM.getBytes());
		smilFile.parse(contents);
		assertEquals("There should be only one segment.", 1, smilFile.getSegments().size());
	}
	
	@MediumTest
    public void testParsingTextOnlyFile() throws Exception {
        SmilFile smilFile = new SmilFile();
        smilFile.load("Resources/WIPO-Treaty-D202Fileset/d202_1.smil");
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
	smilFile.load("Resources/WIPO-Treaty-D202Fileset/d202_4.smil");	
	assertEquals("Expected 2 text segments in d202_4.smil", 2, smilFile.getTextSegments().size());
	TextElement textElement = smilFile.getTextSegments().get(1);
	assertEquals("WIPOTreatyForVisuallyImpaired.html#id_92", textElement.getSrc());
	}
	
	@MediumTest
    public void testParsingAudioOnlyFile() throws Exception {
        SmilFile smilFile = new SmilFile();
        smilFile.load("Resources/light-man/icth0001.smil");
        assertTrue(smilFile.getAudioSegments().size() > 0);
        assertEquals(smilFile.getAudioSegments().get(0).getClipBegin(), 0.0);
        assertEquals(smilFile.getAudioSegments().get(1).getClipBegin(), 1.384);
        assertEquals(smilFile.getAudioSegments().get(2).getClipBegin(), 4.441);
    }
    
	@MediumTest
    public void testParsingFileWithWindows1252Encoding() throws Exception {
    	SmilFile smilfile = new SmilFile();
    	smilfile.load("Resources/problemcontent/bcbw0001.smil");
    	assertEquals("Expected correct count of audio segments", 
    			9, smilfile.getAudioSegments().size());
    }
    
	@MediumTest
    public void testParsingCBFW000BWithWindows1252Encoding() throws Exception {
    	SmilFile smilfile = new SmilFile();
    	smilfile.load("Resources/problemcontent/cbfw000B.smil");
    	assertEquals("Expected correct count of audio segments", 
    			11, smilfile.getAudioSegments().size());
    	
    	SmilFile smilfile2 = new SmilFile();
    	smilfile2.load("/sdcard/problemcontent/cbfw0029.smil");
    	
    }
}
