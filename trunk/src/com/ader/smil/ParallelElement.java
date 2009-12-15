package com.ader.smil;

/**
 * Encapsulates the <par> tag.
 */
public class ParallelElement implements MediaElement {
    private SequenceElement audioSequence;
    private TextElement textElement;
    private MediaElement parent;
    
    public ParallelElement(MediaElement parent) {
        this.parent = parent;
    }

    public AudioElement getAudioElement() {
        if (audioSequence != null) {
            return audioSequence.getAudioElement();
        } else {
            return null;
        }
    }

    public TextElement getTextElement() {
        if (textElement != null) {
            return textElement;
        } else {
            return null;
        }
    }
    
    public void setTextElement(TextElement textElement) {
        this.textElement = textElement;
    }

    public void addAudioElement(AudioElement audioElement) {
        audioSequence.add(audioElement);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((audioSequence == null) ? 0 : audioSequence.hashCode());
        result = prime * result
                + ((textElement == null) ? 0 : textElement.hashCode());
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
        ParallelElement other = (ParallelElement) obj;
        if (audioSequence == null) {
            if (other.audioSequence != null)
                return false;
        } else if (!audioSequence.equals(other.audioSequence))
            return false;
        if (textElement == null) {
            if (other.textElement != null)
                return false;
        } else if (!textElement.equals(other.textElement))
            return false;
        return true;
    }
    
    public MediaElement next() {
        if (audioSequence.hasNext()) {
            return audioSequence.next();
        } else {
            return parent.next();
        }
    }
    
    public MediaElement previous() {
        if (audioSequence.hasPrevious()) {
            return audioSequence.previous();
        }
        return parent.previous();
    }

    public void setAudioSequence(SequenceElement sequence) {
        audioSequence = sequence;
    }

    public MediaElement current() {
        return audioSequence.current();
    }
}
