package com.ader;

import org.w3c.dom.Node;

public class NavBase {
	private int playOrder;
	private String text;
	private String smil;
	private String id;
	
	public NavBase() {
		
	}
	
	public NavBase(Node anchor) {
		this.text = anchor.getFirstChild().getNodeValue();
		this.smil = anchor.getAttributes().getNamedItem("href").getNodeValue();
		this.id = smil.substring(smil.indexOf("#") + 1);
		smil = smil.substring(0, smil.indexOf("#") );
	}

	public String getId() {
		return id;
	}
	public int getPlayOrder() {
		return playOrder;
	}
	public String getText() {
		return text;
	}
	public String getSmil() {
		return smil;
	}

}
