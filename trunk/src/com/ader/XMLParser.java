package com.ader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * Non JavaDoc
 * TODO(jharty):
 * 1. Somewhere we need to either add preconditions (to assert the basic
 *  structure is acceptable and as expected) or handle problematic content more
 *  robustly. e.g. at the moment we gayly dereference things like:
 *  document.getElementsByTagName("body").item(0).getChildNodes() without
 *  checking for nulls being returned by the intervening calls.
 */
public class XMLParser {
	private static final String TAG = "XMLParser";
	private Document document;
	private NavCentre navCentre;

	/**
	 * Parses a valid Daisy 2.02 ncc.html document.
	 * 
	 * @param input valid Daisy 2.02 content.
	 */
	public XMLParser(InputStream input) {
		navCentre = new NavCentre();	
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

		try {
			builderFactory
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);
			document = builderFactory.newDocumentBuilder().parse(input);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the NCC contents to generate a NavCentre object.
	 * 
	 * Currently we only process the h1 through h1 elements. And we assume
	 * the content is valid, otherwise various exceptions, including a null
	 * pointer may be thrown.
	 * 
	 * @return the NavCentre object.
	 */
	public NavCentre processNCC() {
		// get a list of all the tags inside the body element
		NodeList body = document.getElementsByTagName("body").item(0).getChildNodes();
				
		for (int i = 0; i < body.getLength(); i++) {
			Node currentNode = body.item(i);
			
			// is it a heading tag
			if (currentNode.getNodeName().matches("h[123456]")) 
				handleNCChTag(currentNode);
			
			// is it a span tag
			 if (currentNode.getNodeName().equalsIgnoreCase("span"))
				handleNCCspanTag(currentNode);
		}
		
		return navCentre;
	}
	
	private void handleNCChTag(Node heading) {
		int level = Integer.decode(heading.getNodeName().substring(1));
		NavPoint navPoint = new NavPoint(heading.getFirstChild(), level);

		navCentre.addNavPoint(navPoint);
	}

	// TODO(gary): How do you suggest we handle page-numbers? 
	private void handleNCCspanTag(Node span) {
		Node value = span.getFirstChild();
		Util.logInfo(TAG, "span found, containing: " + value.toString());
//		NCCEntry entry = new NCCEntry(span.getFirstChild(), NCCEntryType.PAGENUMBER, 0);	
		//System.out.println(entry.getText());	
	}
}










