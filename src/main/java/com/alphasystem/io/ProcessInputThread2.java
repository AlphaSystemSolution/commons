/**
 * 
 */
package com.alphasystem.io;

import static com.alphasystem.util.Utils.NEW_LINE;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author sali
 * 
 */
public class ProcessInputThread2 extends Thread {

	private BufferedReader in;

	/** name of the process. */
	private String name;

	/** out put stream to display result. */
	private OutputStream out;

	public ProcessInputThread2(InputStream in, OutputStream out) {
		this(null, in, out);
	}

	public ProcessInputThread2(String name, InputStream in, OutputStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = out;
		this.name = (name == null) ? "" : ("[" + name.trim() + "] ");
	}

	@Override
	public void run() {
		try {
			String line = in.readLine();
			while (line != null) {
				line = name + line + NEW_LINE;
				out.write(line.getBytes());
				line = in.readLine();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
