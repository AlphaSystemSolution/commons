/**
 * 
 */
package com.alphasystem.io;

import java.io.FileFilter;

import com.alphasystem.util.FileFilterAdapter;

/**
 * @author sali
 * 
 */
public class IOFileFilter extends FileFilterAdapter implements FileFilter {

	public IOFileFilter() {

	}

	public IOFileFilter(String... extensions) {
		super(extensions);
	}

}
