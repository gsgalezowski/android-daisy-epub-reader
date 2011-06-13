/**
 * Allows a caller to iterate through DaisyItems.
 * 
 * Code based on example from Chapter 9 of Head First Design Patterns book.
 */
package com.ader;

import java.util.Iterator;

/**
 * Iterates through Daisy Items.
 * 
 * @author Julian Harty
 */
public class BookSectionIterator implements Iterator<DaisyItem> {
	private DaisyItem[] items;
	private int position = 0;

	public BookSectionIterator(DaisyItem[] items) {
		this.items = items;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (position >= items.length || items[position] == null) {
			return false;
		} else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public DaisyItem next() {
		DaisyItem daisyItem = items[position];
		position = position + 1;
		return daisyItem;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Removing items from a Daisy Book is not supported.");

	}
}
