package com.ader;

import java.io.Serializable;

public class NCCEntry implements Serializable {
	
    private static final long serialVersionUID = 1L;
    private String smil;
	private String smilRef;
	private String text;
	private int level;

	public NCCEntry(DaisyElement element, int level) {
		text  = element.getText();
		this.level = level;
		smil = element.getAttributes().getValue("", "href");
		int hashPosition = smil.indexOf("#");
		smilRef = smil.substring(hashPosition + 1);
		smil = smil.substring(0, hashPosition);
	}

	public int getLevel() {
		return level;
	}

	public String getSmil() {
		return smil;
	}

	public String getSmilRef() {
		return smilRef;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void setSmil(String smil) {
		this.smil = smil;
	}

	public void setSmilRef(String smilRef) {
		this.smilRef = smilRef;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "smil:" + smil + " text: " + text;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (!(obj instanceof NCCEntry)) {
	        return false;
	    }
	    NCCEntry rhs = (NCCEntry) obj;
        return obj != null && smil.equals(rhs.smil) && smilRef.equals(rhs.smilRef)
            && text.equals(rhs.text);
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + level;
        result = prime * result + ((smil == null) ? 0 : smil.hashCode());
        result = prime * result + ((smilRef == null) ? 0 : smilRef.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }
}
