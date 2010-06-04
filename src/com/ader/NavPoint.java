package com.ader;

import org.w3c.dom.Node;

public class NavPoint extends NavBase {
	private int level;

	public NavPoint(Node anchor, int level) {
		super(anchor);
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
