package com.ader;

import java.util.ArrayList;
import java.util.List;

public class NavCentre {
	private List<NavPoint> navMap = new ArrayList<NavPoint>();
	private List<PageTarget> pageList = new ArrayList<PageTarget>();

	public void addNavPoint(NavPoint navPoint) {
		this.navMap.add(navPoint);
	}
	
	public NavPoint getNavPoint(int index) {
		return navMap.get(index);
	}
}
