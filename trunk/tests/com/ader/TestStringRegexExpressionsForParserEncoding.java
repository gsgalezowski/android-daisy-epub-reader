package com.ader;

import java.util.regex.Pattern;

import junit.framework.TestCase;

public class TestStringRegexExpressionsForParserEncoding extends TestCase {
	
	private static final String validUTF8EncodingString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private static final String validISO8859_1EncodingString="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	
	public void testRegexDetectsEncoding() {
		assertTrue(validUTF8EncodingString.matches(SmilFile.XML_FIRST_LINE_REGEX));
		assertTrue(validISO8859_1EncodingString.matches(SmilFile.XML_FIRST_LINE_REGEX));
	}
	
	public void testCanExtractEncoding() {
		Pattern p = Pattern.compile(SmilFile.EXTRACT_ENCODING_REGEX);
		String matches[] = p.split(validUTF8EncodingString);
		String encoding = matches[1].replace(SmilFile.XML_TRAILER, "");
		assertEquals("utf-8", encoding);
		String isomatches[] = p.split(validISO8859_1EncodingString);
		encoding = isomatches[1].replace(SmilFile.XML_TRAILER, "").toLowerCase();
		assertEquals("iso-8859-1", encoding);
	}
	
	public void testCanUseExtractMethod() {
		String encoding = SmilFile.extractEncoding(validISO8859_1EncodingString);
		assertEquals("iso-8859-1", encoding);
	}
}
