package com.ader.smil;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the <seq> tag.
 */
public class SequenceElement implements ContainerElement {

    private double duration;
    private List<SmilElement> elements = new ArrayList<SmilElement>();
    private SmilElement parent;

    public SequenceElement(SmilElement parent, double duration) {
        this.parent = parent;
        this.duration = duration;
    }

    public SequenceElement(SmilElement parent) {
        this.parent = parent;
    }

    public void add(SmilElement currentElement) {
        elements.add(currentElement);
    }

    public SmilElement get(int i) {
        return elements.get(i);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(duration);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + ((elements == null) ? 0 : elements.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SequenceElement other = (SequenceElement) obj;
        if (Double.doubleToLongBits(duration) != Double
                .doubleToLongBits(other.duration))
            return false;
        if (elements == null) {
            if (other.elements != null)
                return false;
        } else if (!elements.equals(other.elements))
            return false;
        return true;
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public List<AudioElement> getAllAudioElementDepthFirst() {
        List<AudioElement> ret = new ArrayList<AudioElement>();
        for (SmilElement elem : elements) {
            if (elem instanceof ContainerElement) {
                ret.addAll(((ContainerElement) elem).getAllAudioElementDepthFirst());
            } else if (elem instanceof AudioElement) {
                ret.add((AudioElement) elem);
            }
        }
        return ret;
    }

    public List<TextElement> getAllTextElementDepthFirst() {
        List<TextElement> ret = new ArrayList<TextElement>();
        for (SmilElement elem : elements) {
            if (elem instanceof ContainerElement) {
                ret.addAll(((ContainerElement) elem).getAllTextElementDepthFirst());
            } else if (elem instanceof TextElement) {
                ret.add((TextElement) elem);
            }
        }
        return ret;
    }
}
