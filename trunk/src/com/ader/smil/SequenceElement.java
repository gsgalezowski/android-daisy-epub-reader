package com.ader.smil;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the <seq> tag.
 */
public class SequenceElement implements MediaElement {

    private double duration;
    private List<MediaElement> elements = new ArrayList<MediaElement>();
    int cursor = 0;
    private MediaElement parent;

    public SequenceElement(MediaElement parent, double duration) {
        this.parent = parent;
        this.duration = duration;
    }

    public SequenceElement(MediaElement parent) {
        this.parent = parent;
    }

    public AudioElement getAudioElement() {
        return elements.get(cursor).getAudioElement();
    }
    
    public TextElement getTextElement() {
        return elements.get(cursor).getTextElement();
    }

    public void add(MediaElement currentElement) {
        elements.add(currentElement);
    }

    public MediaElement get(int i) {
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

    public boolean hasNext() {
        return cursor < elements.size() - 1;
    }

    public MediaElement next() {
        if (hasNext()) {
            cursor++;
            return elements.get(cursor);
        } else {
            if (parent != null) {
                return parent.next();
            } else {
                return null;
            }
        }
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public MediaElement previous() {
        if (cursor > 0) {
            cursor--;
            return elements.get(cursor);
        } else {
            return parent.previous();
        }
    }

    public boolean hasPrevious() {
        return cursor > 0;
    }

    public MediaElement current() {
        return elements.get(cursor);
    }
}
