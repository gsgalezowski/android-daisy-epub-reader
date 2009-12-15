package com.ader.smil;

/**
 * Encapsulates the <text> tag.
 */
public class TextElement implements MediaElement {
    String src; //TODO: should we keep the location of the text or just keep the text here?
    String id;
    private MediaElement parent;
    
    public TextElement(MediaElement parent, String src, String id) {
        this.parent = parent;
        this.src = src;
        this.id = id;
    }
    
    public AudioElement getAudioElement() {
        return null;
    }
    public TextElement getTextElement() {
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((src == null) ? 0 : src.hashCode());
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
        TextElement other = (TextElement) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (src == null) {
            if (other.src != null)
                return false;
        } else if (!src.equals(other.src))
            return false;
        return true;
    }
    
    public MediaElement next() {
        throw new UnsupportedOperationException();
    }
    
    public MediaElement previous() {
        throw new UnsupportedOperationException();
    }

    public String getSrc() {
        return src;
    }

    public MediaElement current() {
       return this;
    }

}
