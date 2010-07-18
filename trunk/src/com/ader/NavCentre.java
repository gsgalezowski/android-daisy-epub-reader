package com.ader;

import java.util.ArrayList;
import java.util.List;

public class NavCentre {
	private List<NavPoint> navMap = new ArrayList<NavPoint>();
	
	// TODO(gary): What's your plan for pagelist? currently it's not used....
	// I'm assuming this will be used to store page-numbers?
	private List<PageTarget> pageList = new ArrayList<PageTarget>();

	public void addNavPoint(NavPoint navPoint) {
		this.navMap.add(navPoint);
	}
	
	// TODO(gary): what happens if we pass an index out of range?
	// Also this doesn't seem to be called, what's it for?
	public NavPoint getNavPoint(int index) {
		return navMap.get(index);
	}
}
