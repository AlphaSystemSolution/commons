/**
 * 
 */
package com.alphasystem.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author sali
 * 
 */
public class CDataXMLStreamWriter extends DelegatingXMLStreamWriter {

	public CDataXMLStreamWriter(XMLStreamWriter writer) {
		super(writer);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		super.writeCData(text);
	}

}
