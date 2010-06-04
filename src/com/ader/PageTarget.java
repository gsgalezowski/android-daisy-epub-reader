package com.ader;

import org.w3c.dom.Element;

public class PageTarget extends NavBase {
	String type;

	public PageTarget(Element anchor, String type) {
		super(anchor);
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
