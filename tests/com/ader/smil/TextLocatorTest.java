package com.ader.smil;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class TextLocatorTest extends TestCase {
    private TextLocator textLocator;
    
    public void setUp() {
        textLocator = new TextLocator(new File("./Resources"));
    }
    
    public void testLoadFromFile() throws IOException {
        assertEquals("test1\ntest2\ntest3\n", textLocator.getText("test.txt"));
    }
    
    public void testLoadFromTag() throws IOException {
        assertEquals("Hello World", textLocator.getText("test.xml#001"));
        assertEquals("Test", textLocator.getText("test.xml#002"));
    }
}
