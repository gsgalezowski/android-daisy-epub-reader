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
}
