package com.ader;

public class SmilEntry {
	private String src;
	private String clipBegin;
	private String clipEnd;
	private String id;

	public SmilEntry(DaisyElement audio) {
		src = audio.getAttributes().getValue("", "src");
		clipBegin = audio.getAttributes().getValue("", "clip-begin").substring(
				4);
		clipBegin = clipBegin.substring(0, clipBegin.indexOf("s"));
		clipEnd = audio.getAttributes().getValue("", "clip-end").substring(4);
		clipEnd = clipEnd.substring(0, clipEnd.indexOf("s"));
		id = audio.getAttributes().getValue("", "id");
	}

	public String getSrc() {
		return src;
	}

	public int getClipBegin() {
		// return the start of the clip in miliseconds for use with the Media Player
		return (int)(Float.parseFloat(clipBegin) * 1000);	
	}

	public String getClipEnd() {
		return clipEnd;
	}

	public String getId() {
		return id;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setClipBegin(String clipBegin) {
		this.clipBegin = clipBegin;
	}

	public void setClipEnd(String clipEnd) {
		this.clipEnd = clipEnd;
	}

	public void setId(String id) {
		this.id = id;
	}

}
