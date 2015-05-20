/**
 * 
 */
package com.alphasystem.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sali
 * 
 */
public class FileFilterAdapter {

	protected List<String> fileExtensions;

	public FileFilterAdapter(String... extensions) {
		fileExtensions = new ArrayList<String>();
		addFilters(extensions);
	}

	public boolean accept(File pathname) {
		if (pathname.isDirectory()) {
			return true;
		}
		String extension = getExtension(pathname);
		if (extension != null) {
			return fileExtensions.contains(extension);
		}
		return false;
	}

	public void addFilters(String... extensions) {
		if (extensions != null) {
			for (String extension : extensions) {
				if (extension.startsWith(".")) {
					extension = extension.substring(1);
				}
				fileExtensions.add(extension);
			}
		}
	}

	public String getDescription() {
		StringBuilder builder = new StringBuilder();
		if (!fileExtensions.isEmpty()) {
			builder.append("*.").append(fileExtensions.get(0));
			for (int i = 1; i < fileExtensions.size(); i++) {
				String extionsion = fileExtensions.get(i);
				builder.append(", *.").append(extionsion);
			}
		}
		if (builder.length() <= 0) {
			builder.append("No Description");
		}
		return builder.toString();
	}

	protected String getExtension(File pathname) {
		String ext = null;
		String s = pathname.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
