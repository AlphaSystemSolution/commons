/**
 * 
 */
package com.alphasystem.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.alphasystem.ApplicationException;
import com.alphasystem.SystemException;

/**
 * @author sali
 * 
 */
public class FileUtil {

	public static void copyOutputStream(OutputStream out, InputStream in)
			throws IOException {
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
	}

	public static void deleteFolder(File root) {
		if (root == null || !root.exists()) {
			return;
		}
		File[] files = root.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					file.delete();
				}
			}
		}
		root.delete();
	}

	public static void download(String url, File dest)
			throws ApplicationException {
		try {
			download(new URL(url), dest);
		} catch (MalformedURLException e) {
			throw new SystemException(e.getMessage(), e);
		}
	}

	public static void download(String url, String dest)
			throws ApplicationException {
		download(url, new File(dest));
	}

	public static void download(URL url, File dest) throws ApplicationException {
		BufferedReader in = null;
		PrintWriter writer = null;
		try {
			URLConnection urlConnection = url.openConnection();
			writer = new PrintWriter(new BufferedWriter(new FileWriter(dest)));
			in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			String line = in.readLine();
			while (line != null) {
				writer.println(line);
				line = in.readLine();
			}
		} catch (IOException e) {
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void save(String src, File file) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
			writer.println(src);
		} catch (FileNotFoundException e) {
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void save(String src, String file) {
		save(src, new File(file));
	}
}
