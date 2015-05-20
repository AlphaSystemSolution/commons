package com.alphasystem.util;

import java.io.File;

public class ZipFileEntry {

	private final File file;

	private final String name;

	public ZipFileEntry(File file, String name) {
		this.file = file;
		this.name = name;
	}

	public File getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

}
