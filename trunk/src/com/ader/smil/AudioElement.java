package com.ader.smil;

/**
 * Encapsulates the <audio> tag.
 */
public class AudioElement implements MediaElement {
    
    private String src;
    private String clipBegin;
    private String clipEnd;
    private String id;
    private MediaElement parent;

    public AudioElement(MediaElement parent, String src, String clipBegin, String clipEnd, String id) {
        super();
        this.parent = parent;
        this.src = src;
        this.clipBegin = clipBegin;
        this.clipEnd = clipEnd;
        this.id = id;
    }

    public TextElement getTextElement() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((clipBegin == null) ? 0 : clipBegin.hashCode());
        result = prime * result + ((clipEnd == null) ? 0 : clipEnd.hashCode());
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
        AudioElement other = (AudioElement) obj;
        if (clipBegin == null) {
            if (other.clipBegin != null)
                return false;
        } else if (!clipBegin.equals(other.clipBegin))
            return false;
        if (clipEnd == null) {
            if (other.clipEnd != null)
                return false;
        } else if (!clipEnd.equals(other.clipEnd))
            return false;
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

    public String getSrc() {
        // TODO Auto-generated method stub
        return src;
    }

    public AudioElement getAudioElement() {
        // TODO Auto-generated method stub
        return this;
    }

    public String getClipBegin() {
        // TODO Auto-generated method stub
        return clipBegin;
    }

    public String getClipEnd() {
        // TODO Auto-generated method stub
        return clipEnd;
    }

    public String getId() {
        // TODO Auto-generated method stub
        return id;
    }
    
    public boolean hasMore() {
        return false;
    }
    
    public MediaElement next() {
        throw new UnsupportedOperationException();
    }
    
    public MediaElement previous() {
        throw new UnsupportedOperationException();
    }

    public MediaElement current() {
        return this;
    }
}
