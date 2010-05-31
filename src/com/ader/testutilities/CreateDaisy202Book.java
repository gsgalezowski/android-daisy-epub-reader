package com.ader.testutilities;

import java.io.OutputStream;
import java.io.PrintStream;

import com.ader.NotImplementedException;

/**
 * Creates an eBook in DAISY v2.02 format.
 * 
 * Note: Currently the functionality is rudimentary, we need to allow the
 * caller to add custom content e.g. to add corrupt headings so the error
 * handling of the parser(s) can be tested.
 * 
 * TODO(jharty): Continue enhancing this class.
 */
public class CreateDaisy202Book extends CreateEBook {

	public CreateDaisy202Book() throws NotImplementedException {
		super();
	}

	public CreateDaisy202Book(OutputStream out) throws NotImplementedException {
		super(out);
	}

	@Override
	public void writeXmlHeader() {
		new PrintStream(out).println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	}
	
	@Override
	public void writeXmlns() {
		new PrintStream(out).println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
	}

	public void writeDoctype() {
		new PrintStream(out).println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"xhtml1-transitional.dtd\">");	
	}
	
	@Override
	public void writeBasicMetadata() {
		new PrintStream(out).println("  <head>");
		new PrintStream(out).println("    <meta name=\"dc:title\" content=\"basic title\"/>");
		new PrintStream(out).println("    <meta name=\"dc:format\" content=\"Daisy 2.02\"/>");
		new PrintStream(out).println("  </head>");
	}

	@Override
	public void writeEndOfDocument() {
		new PrintStream(out).println("</html>");
	}
	
	public void addLevelOne() {
		new PrintStream(out).print("<h1 id=\"test_0001\"><a href=\"test_001.smil#text_0001\">");
		new PrintStream(out).println("This is a dummy level one entry that doesn't match a file</a></h1>");
	}
}
