package com.ader;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
	private Document document;
	private NavCentre navCentre;
	

	public XMLParser(String filename) {
		navCentre = new NavCentre();	
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();

		try {
			builderFactory
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);
			document = builderFactory.newDocumentBuilder().parse(
					new FileInputStream(filename));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public NavCentre processNCC() {
		// get a list of all the tags inside the body element
		NodeList body = document.getElementsByTagName("body").item(0).getChildNodes();
				
		for (int i = 0; i < body.getLength(); i++) {
			Node currentNode = body.item(i);
			
			// is it a heading tag
			if (currentNode.getNodeName().matches("h[123456]")) 
				handleNCChTag(currentNode);
			
			// is it a span tag
			// if (currentNode.getNodeName().equalsIgnoreCase("span"))
	//			handleNCCspanTag(currentNode);
		}
		
		return navCentre;
	}
	
	private void handleNCChTag(Node heading) {
		int level = Integer.decode(heading.getNodeName().substring(1));
		NavPoint navPoint = new NavPoint(heading.getFirstChild(), level);

		
		
		navCentre.addNavPoint(navPoint);
	}

	private void handleNCCspanTag(Node span) {
//		NCCEntry entry = new NCCEntry(span.getFirstChild(), NCCEntryType.PAGENUMBER, 0);	
		//System.out.println(entry.getText());	
	}
}










