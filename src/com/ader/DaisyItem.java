package com.ader;

/**
 * Represents an abstract DaisyItem in Daisy books.
 * @author Julian Harty
 *
 */
public interface DaisyItem {

	DaisyItemType getType();

	/**
	 * The level of the current item.
	 * 
	 * This is based on a Daisy 2.02 concept and needs revisiting with v3.
	 * 
	 * @return
	 */
	int getLevel();

	String getSmil();

	/**
	 * Returns the text for this NCC entry.
	 * 
	 * Typically a brief description of this section in the book.
	 */
	String getText();

	String toString();

	boolean equals(Object obj);

	int hashCode();

}