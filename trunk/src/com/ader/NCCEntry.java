package com.ader;

import java.io.Serializable;

public class NCCEntry implements Serializable {
	
    private static final long serialVersionUID = 2L;
    private String smil;
	private String smilRef;
	private String text;
	private int level;
	private NCCEntryType type;
	

	/**
	 * Retained for now to enable easier migration. Will be deprecated once the
	 * new XMLParser works.
	 * @param element the DaisyElement to process
	 * @param type the type of the entry
	 * @param level the level of the element on the DAISY book hierarchy
	 */
	public NCCEntry(DaisyElement element, NCCEntryType type, int level) {
		this(element.getText(), element.getAttributes().getValue("", "href"), type, level);
	}

	/** 
	 * Creates an NCC Entry.
	 * 
	 * This signature helps with the migration from the old DaisyParser to the
	 * new XMLParser (that uses DAISY3 constructs).
	 * @param text the text content
	 * @param smil the smil reference to the content
	 * @param type the type of NCC Entry e.g. HEADING
	 * @param level the level of the element in the DAISY book hierarchy
	 */
	public NCCEntry(String text, String smil, NCCEntryType type, int level) {
		// I discovered the parser (or something) added newline and tab characters
		// to the last page-number for light-man. This is a crude workaround until
		// I get to the bottom of the issue. Since I want to retire this, old
		// parser, I'm fairly relaxed about the problem, but am curious to learn
		// the cause so I make sure the new parser doesn't suffer from similar
		// problems.
		this.text = text.trim();  // Strips off extraneous new line characters, etc.
		this.type = type;
		this.level = level;
		int hashPosition = smil.indexOf("#");
		smilRef = smil.substring(hashPosition + 1);
		String tempStr = smil.substring(0, hashPosition);
		this.smil = tempStr;
		
	}

	public NCCEntryType getType() {
		return type;
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
	
	public void setType(NCCEntryType type) {
		this.type = type;
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

	/**
	 * Returns the text for this NCC entry.
	 * 
	 * Typically a brief description of this section in the book.
	 */
	public String getText() {
		return text;
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
