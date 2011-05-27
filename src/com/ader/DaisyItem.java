package com.ader;

/**
 * Represents an abstract DaisyItem in Daisy books.
 * 
 * This work is subject to significant change while we experiment with adding
 * the ability to process DAISY 3 books.
 * 
 * @author Julian Harty
 *
 */
public interface DaisyItem {

	/**
	 * Return the type (DaisyItemType) of this DaisyItem.
	 * 
	 * This is based on our implementation for DAISY 2.02 material and may
	 * not be useful for DAISY 3. We will see. For DAISY 3 I expect the type
	 * would match one of the types described in the ncx e.g. navLabel, 
	 * pageTarget, navPoint, etc.
	 * @return the DaisyItemType.
	 */
	DaisyItemType getType();

	/**
	 * The level of the current item.
	 * 
	 * This is based on a Daisy 2.02 concept and needs revisiting with v3.
	 * DAISY 3 appears to use nesting of navPoint elements to encode the
	 * hierarchy of sections. I expect we can calculate the Level for DAISY 3
	 * I'm not sure how useful it will be, we will see.
	 * @return
	 */
	int getLevel();

	/**
	 * The filename of the smil file.
	 * 
	 * This is relative to the current folder of the book's contents.
	 * @return filename of smil file.
	 */
	String getSmil();

	/**
	 * Returns the text for this DaisyItem.
	 * 
	 * Typically a brief description of this section in the book.
	 */
	String getText();

	/**
	 * The name of the smil file and the text for this entry.
	 * @return a string representation. May be useful for debugging.
	 */
	String toString();

	/**
	 * Allows DaisyItems to be compared based on their contents.
	 * 
	 * They have to be of the same type e.g. the implementation is not expected
	 * to compare a DaisyItem representing a DAISY 2.02 item with one
	 * representing a DAISY 3 item.
	 * @param obj
	 * @return true if the contents of the DaisyItem is deemed to match this
	 * one.
	 */
	boolean equals(Object obj);

	/**
	 * Required but not really used...
	 * 
	 * TODO(jharty): Why is this required? Decide whether we can do something
	 * more elegant.
	 * @return a hashocde based on the contents of this DaisyItem.
	 */
	int hashCode();

}