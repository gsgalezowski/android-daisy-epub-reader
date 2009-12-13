package com.ader;

public class SmilEntry {
	private String src;
	private String clipBegin;
	private String clipEnd;
	private String id;
	private String text;

	public SmilEntry(DaisyElement element) {
		String type = element.getName().toLowerCase();
		src = element.getAttributes().getValue("", "src");
		id = element.getAttributes().getValue("", "id");

		if (type.equals("audio")) {
			// TODO(jharty): use defensive programming techniques - don't assume all's ok
			clipBegin = element.getAttributes().getValue("", "clip-begin").substring(4);
			clipBegin = clipBegin.substring(0, clipBegin.indexOf("s"));
			clipEnd = element.getAttributes().getValue("", "clip-end").substring(4);
			clipEnd = clipEnd.substring(0, clipEnd.indexOf("s"));
		} else if (type.equals("text")) {
			text = element.getText();
		}
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

	private void setSrc(String src) {
		this.src = src;
	}

	private void setClipBegin(String clipBegin) {
		this.clipBegin = clipBegin;
	}

	private void setClipEnd(String clipEnd) {
		this.clipEnd = clipEnd;
	}

	private void setId(String id) {
		this.id = id;
	}

}
