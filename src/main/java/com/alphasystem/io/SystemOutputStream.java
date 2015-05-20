/*
 * SystemOutputStream.java
 *
 * Created on July 31, 2004, 10:28 PM
 */
package com.alphasystem.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * This class writes OutStream to text area. This class is useful to redirect
 * System.out and System.err to some text area.
 * 
 * @author Syed Frahan Ali
 */
public class SystemOutputStream extends FilterOutputStream {

	// ~ Instance/static variables .............................................

	/**
	 * Text area to output stream.
	 */
	private JTextArea outputArea;

	// ~ Constructors ..........................................................

	/**
	 * Creates new SystemOutputStream with the underlying stream and text area.
	 * 
	 * @param aStream
	 *            underlying OutputStream
	 * @param outputArea
	 *            text area for output text
	 * @throws NullPointerException
	 *             if text area is null
	 */
	public SystemOutputStream(OutputStream aStream, JTextArea outputArea) {
		super(aStream);
		if (outputArea == null) {
			throw new NullPointerException("Output Area could not be null.");
		}
		this.outputArea = outputArea;
	}

	// ~ Methods ...............................................................

	/**
	 * Writes array of bytes to text area.
	 * 
	 * @param b
	 *            array of bytes to write
	 * @throws IOException
	 *             if something happens during IO
	 */
	public void write(byte[] b) throws IOException {
		outputArea.append(new String(b));
		outputArea.setCaretPosition(outputArea.getText().length());
	}

	/**
	 * Writes array of bytes to text area.
	 * 
	 * @param b
	 *            array of bytes to write
	 * @param off
	 *            starting offset
	 * @param len
	 *            number of bytes to read
	 * @throws IOException
	 *             if something happens during IO
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		outputArea.append(new String(b, off, len));
		outputArea.setCaretPosition(outputArea.getText().length());
	}
}
