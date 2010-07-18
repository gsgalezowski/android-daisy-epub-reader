package com.ader;

import org.w3c.dom.Node;

public class NavBase {
	private int playOrder;
	private String text;
	private String smil;
	private String id;
	
	/* non JavaDoc
	 * TODO(jharty): Discuss the following...
	 * This looks dangerous, having a default public constructor, as it means
	 * the object could be created with null values - probably not what we want
	 * How about making it private, so it can't be called inadvertently.
	 * 
	 * Compare with a Factory design pattern where the default constructor is
	 * also prohibited.
	 */
	public NavBase() {
		
	}
	
	/* Non JavaDoc
	 * TODO(jharty): Act on the following note...
	 * Notes: The following code assumes every element is present and correct.
	 * If these assumptions are incorrect, what happens? Let's add some tests
	 * to ascertain the behaviour, then modify the behaviour to what we'd like
	 * it to be :)
	 */
	public NavBase(Node anchor) {
		this.text = anchor.getFirstChild().getNodeValue();
		this.smil = anchor.getAttributes().getNamedItem("href").getNodeValue();
		this.id = smil.substring(smil.indexOf("#") + 1);
		smil = smil.substring(0, smil.indexOf("#") );
	}

	public String getId() {
		return id;
	}
	public int getPlayOrder() {
		return playOrder;
	}
	public String getText() {
		return text;
	}
	public String getSmil() {
		return smil;
	}

}
