package com.ader.smil;

import java.util.List;

public interface ContainerElement extends SmilElement {
    public List<AudioElement> getAllAudioElementDepthFirst();
    public List<TextElement> getAllTextElementDepthFirst();
}
