package com.ader;
/**
 * NavCenter stores and represents the navigable sections in an eBook.
 * 
 * Currently it is limited to sections in the eBook e.g. page-numbers aren't
 * included - this is a short-term limitation we expect to address.
 */
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
	
	/**
	 * Returns the specified NavPoint. 
	 * 
	 * The index matches the order that navigation points were found in the
	 * source document.
	 * @param index between 0 and count() items
	 * @return the specified NavPoint, or null if index is outside the valid
	 * range.
	 */
	// TODO(gary): I've modified the behaviour to return null, what do you think?
	public NavPoint getNavPoint(int index) {
		if (index >= 0 && index < navMap.size()) {
			return navMap.get(index);
		} else {
			return null;
		}
	}
	/**
	 * A NavCenter contains NavPoints, get the count of items.
	 * @return the number of items in the NavCenter.
	 */
	public int count() {
		return navMap.size();
	}
}
