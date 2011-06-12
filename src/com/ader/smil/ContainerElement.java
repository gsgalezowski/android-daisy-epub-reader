package com.ader.smil;

import java.util.List;

public interface ContainerElement extends SmilElement {
	
    /**
     * Useful when navigating the nested data structures.
     * @return the parent SMIL element
     */
	public ContainerElement getParent();
	
    public List<AudioElement> getAllAudioElementDepthFirst();
    public List<TextElement> getAllTextElementDepthFirst();
}
