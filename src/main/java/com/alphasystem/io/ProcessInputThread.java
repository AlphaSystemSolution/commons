/**
 * 
 */
package com.alphasystem.io;

import static com.alphasystem.util.Utils.NEW_LINE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import javax.swing.SwingWorker;

/**
 * @author sali
 * 
 */
public class ProcessInputThread extends SwingWorker<String, String> {

	private BufferedReader in;

	/** name of the process. */
	private String name;

	/** out put stream to display result. */
	private OutputStream out;

	public ProcessInputThread(InputStream in, OutputStream out) {
		this(null, in, out);
	}

	public ProcessInputThread(String name, InputStream in, OutputStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = out;
		this.name = (name == null) ? "" : ("[" + name.trim() + "] ");
	}

	@Override
	protected String doInBackground() throws Exception {
		String result = null;
		try {
			String line = in.readLine();
			while (line != null) {
				result = name + line + NEW_LINE;
				publish(result);
				line = in.readLine();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}

	@Override
	protected void process(List<String> chunks) {
		for (String line : chunks) {
			try {
				out.write(line.getBytes());
			} catch (IOException e) {
				System.out.println("ERROR");
				e.printStackTrace();
			}
		}
	}

}
