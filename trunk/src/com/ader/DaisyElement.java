package com.ader;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class DaisyElement {
	private AttributesImpl attributes;	  
	private String name = "";
	private String text = "";
	
	public String getName() {
		return name;
		
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setAttributes(Attributes attributes) {
		this.attributes = new AttributesImpl(attributes);
	}
	public void setText(String text) {
		this.text = text;
	}
	public Attributes getAttributes() {
		return attributes;
	}
}
