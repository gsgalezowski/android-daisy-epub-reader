package com.ader.io;

import java.util.regex.Pattern;

import com.ader.SmilFile;

import junit.framework.TestCase;

public class TestStringRegexExpressionsForParserEncoding extends TestCase {
	
	private static final String validUTF8EncodingString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private static final String validISO8859_1EncodingString="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	// The following string seems to be rejected on the device, but works in Eclipse's JVM
	private static final String quirkyWindows1252EncodingString="<?xml version=\"1.0\" encoding=\"windows-1252\"?>";
	
	private Pattern p;
	
	@Override
	protected void setUp() {
		p = Pattern.compile(ExtractXMLEncoding.EXTRACT_ENCODING_REGEX);
		
	}
	public void testRegexDetectsEncoding() {
		assertTrue(validUTF8EncodingString.matches(ExtractXMLEncoding.XML_FIRST_LINE_REGEX));
		assertTrue(validISO8859_1EncodingString.matches(ExtractXMLEncoding.XML_FIRST_LINE_REGEX));
		assertTrue(quirkyWindows1252EncodingString.matches(ExtractXMLEncoding.XML_FIRST_LINE_REGEX));
	}
	
	public void testCanExtractUtf8Encoding() {
		String matches[] = p.split(validUTF8EncodingString);
		String encoding = matches[1].replace(ExtractXMLEncoding.XML_TRAILER, "");
		assertEquals("utf-8", encoding);
	}
	public void testCanExtractIso9959_1Encoding() {
		String isomatches[] = p.split(validISO8859_1EncodingString);
		String encoding = isomatches[1].replace(ExtractXMLEncoding.XML_TRAILER, "").toLowerCase();
		assertEquals("iso-8859-1", encoding);
	}
	
	public void testCanUseExtractMethod() {
		String encoding = ExtractXMLEncoding.extractEncoding(validISO8859_1EncodingString);
		assertEquals("iso-8859-1", encoding);
	}
}
