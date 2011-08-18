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

import com.ader.smil.SmilFile;
import com.ader.utilities.Logging;

/**
 * Represents bookmarks for a given book.
 * 
 * Currently the code is limited to processing the existing automatically
 * maintained bookmark. I plan to extend the class to use and process the
 * DAISY 3 bookmarks file. To do so, we will need to:
 * - parse the XML file format and be able to write it out, probably 
 *   on serialization and explicitly when called to do so.
 * - enable callers to add, list and retrieve individual bookmarks.
 * 
 * @author jharty
 *
 */
public final class Bookmark implements Serializable {
	protected static final String AUTO_BMK = "auto.bmk";
	private static final String TAG = "Bookmark";
	private static final String BOOKMARKS_FILENAME = "bookmarks.bmk";
	private String pathToBook; 
    private String filename;
	private int nccIndex;
	private int position;

	/**
	 * Load the bookmarks from a given path. Only called in this class.
	 * @param path
	 * @throws IOException
	 */
	private Bookmark(String path) throws IOException {
		this.pathToBook = ensureTrailingSlash(path);
		this.loadBookmarks();
	}
	
	private Bookmark() {
		// Stop callers from calling new Bookmark();
	}
	
	/**
	 * Create and return a Bookmark
	 * @param path
	 * @return a new Bookmark if the underlying bookmark is found and loaded.
	 * @throws IOException if there are IO problems.
	 */
	public static Bookmark getInstance(String path) throws IOException {
		return new Bookmark(path);
		
	}

	/**
	 * @return the Filename of the element stored in the bookmark.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the NCC index stored in the bookmark.
	 */
	public int getNccIndex() {
		return nccIndex;
	}

	/**
	 * @return the position (offset) into the element referenced by the 
	 * bookmark.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Set / Update the bookmark with the NCC index to store.
	 * @param nccIndex
	 */
	public void setNccIndex(int nccIndex) {
		this.nccIndex = nccIndex;
	}

	/**
	 * Set / Update the position (offset) into the current element referenced
	 * by the bookmark. 
	 * @param position
	 */
	public void setPosition(int position) {
		Logging.logInfo(TAG, "Setting position to " + position);
		this.position = position;
	}

	/**
	 * Save the contents of the bookmark to the specified filename. 
	 * @param bookmarkFilename
	 */
	public void save(String bookmarkFilename) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(bookmarkFilename);
			save(fileOutputStream);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Update the automatic bookmark. 
	 * 
	 *  This is typically used to enable the player to restart from the most
	 *  recent smilfile.
	 */
	public void updateAutomaticBookmark(SmilFile smilFile) {
		
		// Compare this.filename with the path to the book.
		if (smilFile.getAudioSegments().size() > 0) {
			// TODO(jharty): This happens to work because audio is typically
			// in a common mp3 file even when there are several segments.
			// However it's gravely flawed and needs cleaning up.
			this.setFilename(pathToBook + smilFile.getAudioSegments().get(0).getSrc());

			// Only set the start if we don't already have an offset into
			// this file from an existing bookmark. 
			// TODO(jharty): Again this code is flawed. Address with above fix.
			if (this.getPosition() <= 0) {
				this.setPosition((int) smilFile.getAudioSegments().get(0).getClipBegin());
				Logging.logInfo(TAG, String.format(
						"After calling setPosition SMILfile[%s] NCC index[%d] offset[%d]",
						this.getFilename(), this.getNccIndex(), this.getPosition()));
			}

		} else if (smilFile.getTextSegments().size() > 0) {
			// TODO(jharty): ditto - fix this logic.
			this.setFilename(pathToBook + smilFile.getTextSegments().get(0).getSrc());
			this.setPosition(0);
		}
	}

	/* non javadoc
	 * Extracted this method to improve the testability of this class.
	 */
	void load(InputStream inputStream) throws IOException {
		DataInputStream in = new DataInputStream(inputStream);
		try {
			filename = in.readUTF();
			nccIndex = in.readInt();
			position = in.readInt();
			Logging.logInfo(TAG, String.format(
					"Reading Bookmark details SMILfile[%s] NCC index[%d] offset[%d]",
					filename, nccIndex, position));
		} catch (IOException ioe) {
			Logging.logSevereWarning(TAG, "There is a problem reading the contents of the bookmark", ioe);
			// We rely on the rest of the logic to cope e.g. when the book
			// starts being read, the contents of the bookmark will be updated.
		} finally {
			in.close();
		}
	}
	
	/* non javadoc
	 * Extracted this method to improve the testability of this class.
	 */
	void save(OutputStream outputStream) throws IOException {
		DataOutputStream out = new DataOutputStream(outputStream);

		Logging.logInfo(TAG, String.format(
				"Saving Bookmark details SMILfile[%s] NCC index[%d] offset[%d]",
				filename, nccIndex, position));
		out.writeUTF(filename);
		out.writeInt(nccIndex);
		out.writeInt(position);
		out.flush();
		out.close();
	}

	/**
	 * Set / Update the Automatic bookmark to store the filename provided.
	 * @param filename
	 */
	void setFilename(String filename) {
		this.filename = filename;
	}

	private String ensureTrailingSlash(String path) {
		if (path.endsWith("/") || path.endsWith("\\")) {
			return path;
		} else {
			return path + File.separator;
		}
	}
	
	private void load(String bookmarkFilename) throws IOException {

		if (new File(bookmarkFilename).exists()) {
			FileInputStream fileInputStream = new FileInputStream(bookmarkFilename);
			load(fileInputStream);
		}
	}
	
	private void loadBookmarks() throws IOException {
		// Hmmm, what to do about dual bookmarks? old and new...
		// Some sort of migration path seems sensible.
		// Let's implement support to read from the current (old) file as I
		// need to write code to create the recommended DAISY 3 bookmark
		// structure (XML based).
		
		// The following file will not exist currently as we don't create it.
		String newBookmarkFile = pathToBook + BOOKMARKS_FILENAME;
		if (new File(newBookmarkFile).exists()) {
			Logging.logInfo(TAG, "Apparently the new bookmarks file exists!");
			// TODO(jharty): Add code to parse the XML contents
		} else {
			// Load the old automatic bookmark file, if it exists
			load(pathToBook + AUTO_BMK);
		}
		
	}
}
