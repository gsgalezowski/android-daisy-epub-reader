package com.ader;

public interface SectionNavigation {

	/**
	 * Go to the specified item in the book.
	 * @param item
	 */
	public abstract void goTo(DaisyItem item);

	/**
	 * Go to the next section in the book
	 * @param includeLevels - when true, pick the next section at a level
	 * equal or higher than the level selected by the user, else simply go to
	 * the next section.
	 */
	public abstract boolean nextSection(Boolean includeLevels);

	/**
	 * Go to the previous section in the book.
	 * @return
	 */
	public abstract boolean previousSection();

}