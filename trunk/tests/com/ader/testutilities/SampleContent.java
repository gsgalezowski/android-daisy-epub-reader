package com.ader.testutilities;

/**
 * Here's a central place to hold content used for various tests.
 * 
 * @author Julian Harty
 */
public class SampleContent {
	
	public static String firstTitle = "Test Book Title";
	
 	/**
	 * Note: currently this ncc.html doesn't include all of the mandatory
	 * elements e.g. no meta tags yet...
	 */
	public static String simpleValidNccHtml = 
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
		"\"xhtml1-transitional.dtd\"> " +
		"<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
		"<head> <title>Simple Test Valid ncc.html</title></head>" +
		"<body>" +
		"<h1 class=\"title\" id=\"testcase\">" +
		"<a href=\"dtb_01.smil#rgn_txt_01\">" + firstTitle + "</a></h1>" +
		"</body></html>"
		;
	
	/** 
	 * This is an extracted subset of a Daisy book, with Icelandic characters.
	 * It upset the SAX parser, so it's included here to help test our
	 * attempts to fix the problems.
	 */
	public static String validIcelandicNccHtml = 
		// "<?xml version=\"1.0\" encoding=\"windows-1252\"?>" +
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
		"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\" []>" +
		"<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
		"<head>" +
		"<title>Iceland ð</title>" +
		// "<meta name=\"dc:title\" content=\"Iceland ð\" />" +
		"<meta name=\"dc:creator\" content=\"Harty, Julian\" />" +
		"<meta name=\"dc:date\" content=\"2011-06-27\" scheme=\"yyyy-mm-dd\" />" +
		"<meta name=\"dc:format\" content=\"Daisy 2.02\" />" +
		// "<meta name=\"dc:publisher\" content=\"Í ók\" />" +
		"<meta name=\"ncc:charset\" content=\"windows-1252\" />" +
		"<meta name=\"ncc:depth\" content=\"1\" />" +
		"<meta name=\"ncc:multimediaType\" content=\"audioNcc\" />" +
		// "<meta name=\"ncc:narrator\" content=\"Óún Són\" />" +
		"<meta http-equiv=\"Content-type\" content=\"text/html; charset=windows-1252\" />" +
		"</head>" +
		"<body>" +
		// "<h1 class=\"title\" id=\"testcase\"><a href=\"missing.smil#a_0001\">ð</a></h1>" + 
		"</body>" +
		"</html>"
		;
}
