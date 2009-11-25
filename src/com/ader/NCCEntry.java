package com.ader;

public class NCCEntry {
	private String smil;
	private String smilRef;
	private String text;
	private int level;

	public NCCEntry() {

	}

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

	public String GetSmil() {
		return smil;
	}

	public String GetSmilRef() {
		return smilRef;
	}

	public String GetText() {
		return text;
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

}
