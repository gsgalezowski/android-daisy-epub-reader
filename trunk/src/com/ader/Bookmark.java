package com.ader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Bookmark {
	private String filename;
	private int nccIndex;
	private int position;

	public Bookmark() {
	}
	
	public Bookmark(String filename, int nccIndex, int position) {
		this.filename = filename;
		this.nccIndex = nccIndex;
		this.position = position;
	}

	public String getFilename() {
		return filename;
	}

	public int getNccIndex() {
		return nccIndex;
	}

	public int getPosition() {
		return position;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setNccIndex(int nccIndex) {
		this.nccIndex = nccIndex;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	void load(String bookmarkFilename) throws IOException {
		
			if (new File(bookmarkFilename).exists()) {
				DataInputStream in;
                in = new DataInputStream(new FileInputStream(bookmarkFilename));
                filename = in.readUTF();
                nccIndex = in.readInt();
                position = in.readInt();
                in.close();
			}
	}

	public void save(String bookmarkFilename) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(bookmarkFilename));

			out.writeUTF(filename);
			out.writeInt(nccIndex);
			out.writeInt(position);
			out.flush();
			out.close();
		} catch (IOException ex) {
		    throw new RuntimeException(ex);
		}
	}
}
