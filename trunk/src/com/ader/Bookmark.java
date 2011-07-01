package com.ader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Bookmark implements Serializable {
	private static final String TAG = "Bookmark";
    private String filename;
	private int nccIndex;
	private int position;

	// TODO: redesign this class so the class variables are always initialized.
	// Consider adding a factory and making the constructors private.
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
	    Util.logInfo(TAG, "Setting position to " + position);
		this.position = position;
	}

	public void load(String bookmarkFilename) throws IOException {

		if (new File(bookmarkFilename).exists()) {
			FileInputStream fileInputStream = new FileInputStream(bookmarkFilename);
			load(fileInputStream);
		}
	}

	/* non javadoc
	 * Extracted this method to improve the testability of this class.
	 */
	void load(InputStream inputStream) throws IOException {
		DataInputStream in = new DataInputStream(inputStream);
		filename = in.readUTF();
		nccIndex = in.readInt();
		position = in.readInt();
		Util.logInfo(TAG, String.format(
				"Reading Bookmark details SMILfile[%s] NCC index[%d] offset[%d]",
				filename, nccIndex, position));
		in.close();
	}

	/**
	 * Update the automatic bookmark. 
	 * 
	 *  This is typically used to enable the player to restart from the most
	 *  recent smilfile.
	 */
	public void updateAutomaticBookmark(String pathToBook, SmilFile filename) {
		
		// Compare this.filename with the path to the book.
		if (filename.getAudioSegments().size() > 0) {
			// TODO (jharty): are we assuming we always get the first entry?
			this.setFilename(pathToBook + filename.getAudioSegments().get(0).getSrc());

			// Only set the start if we don't already have an offset into
			// this file from an existing bookmark.
			if (this.getPosition() <= 0) {
				this.setPosition((int) filename.getAudioSegments().get(0).getClipBegin());
				Util.logInfo(TAG, String.format(
						"After calling setPosition SMILfile[%s] NCC index[%d] offset[%d]",
						this.getFilename(), this.getNccIndex(), this.getPosition()));
			}

		} else if (filename.getTextSegments().size() > 0) {
			this.setFilename(pathToBook + filename.getTextSegments().get(0).getSrc());
			this.setPosition(0);
		}
	}
	
	public void save(String bookmarkFilename) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(bookmarkFilename);
			save(fileOutputStream);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/* non javadoc
	 * Extracted this method to improve the testability of this class.
	 */
	void save(OutputStream outputStream) throws IOException {
		DataOutputStream out = new DataOutputStream(outputStream);

		Util.logInfo(TAG, String.format(
				"Saving Bookmark details SMILfile[%s] NCC index[%d] offset[%d]",
				filename, nccIndex, position));
		out.writeUTF(filename);
		out.writeInt(nccIndex);
		out.writeInt(position);
		out.flush();
		out.close();
	}
}
