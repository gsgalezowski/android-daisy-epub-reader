package com.ader.smil;

/**
 * Public interface for interacting with Smil elements
 * 
 *
 */
public interface MediaElement {
    public TextElement getTextElement();
    public AudioElement getAudioElement();
    public MediaElement next();
    public MediaElement previous();
    public MediaElement current();
}
