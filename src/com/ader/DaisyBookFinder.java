package com.ader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DaisyBookFinder {
	private ArrayList<String> folderList = new ArrayList<String>();
	private ArrayList<String> BookList = new ArrayList<String>();
	private File fileSystem;

	public void findBooks(String path) {
		
		
		FilenameFilter dirFilter = new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		};

		if (containsBook(path))
			BookList.add(path);
		else
			for (File folder : new File(path).listFiles(dirFilter))
				findBooks(folder.toString());

		return;
	}

	public Boolean validFileSystemRoot(String path) {
		fileSystem = new File(path);

		return fileSystem.isDirectory();
	}

	public void addFolders(String path) {
		File currentDirectory = new File(path);

		FilenameFilter dirFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		};

		folderList.addAll(new ArrayList<String>(Arrays.asList(currentDirectory
				.list(dirFilter))));

		Collections.sort(folderList, String.CASE_INSENSITIVE_ORDER);
	}

	public Boolean containsBook(String path) {
		return new File(path, "ncc.html").exists();

	}

	public ArrayList<String> getBookList() {
		for (String path : BookList)
			System.out.println(path);
		return BookList;
	}

	public ArrayList<String> getFolderList() {
		return folderList;
	}
}
