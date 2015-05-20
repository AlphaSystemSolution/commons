/**
 * 
 */
package com.alphasystem.util;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * @author sali
 * 
 */
public class JAXBUtil2 {

	private class NoNamespaceContext implements NamespaceContext {

		@Override
		public String getNamespaceURI(String prefix) {
			return "";
		}

		@Override
		public String getPrefix(String namespaceURI) {
			return "";
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Iterator getPrefixes(String namespaceURI) {
			return null;
		}

	}

	private boolean useCdata;

	private boolean omitNamespace;

	private Marshaller.Listener marshallerListener;

	private Unmarshaller.Listener unmarshallerListener;

	private Schema schema;

	private NamespaceContext noNamespaceContext;

	private Map<String, Object> marshallerProperties = new HashMap<String, Object>();

	public JAXBUtil2() {
		this(false, false);
	}

	public JAXBUtil2(boolean useCdata) {
		this(useCdata, false);
	}

	public JAXBUtil2(boolean useCdata, boolean omitNamespace) {
		this(useCdata, omitNamespace, null, null, (String[]) null);
	}

	public JAXBUtil2(boolean useCdata, boolean omitNamespace,
			Marshaller.Listener marshallerListener,
			Unmarshaller.Listener unmarshallerListener) {
		this(useCdata, omitNamespace, marshallerListener, unmarshallerListener,
				(String[]) null);
	}

	public JAXBUtil2(boolean useCdata, boolean omitNamespace,
			Marshaller.Listener marshallerListener,
			Unmarshaller.Listener unmarshallerListener, String... schemaPath) {
		this.useCdata = useCdata;
		this.omitNamespace = omitNamespace;
		this.marshallerListener = marshallerListener;
		this.unmarshallerListener = unmarshallerListener;
		setSchema(schemaPath);
		noNamespaceContext = new NoNamespaceContext();
	}

	public JAXBUtil2(boolean useCdata, boolean omitNamespace,
			String... schemaPath) {
		this(useCdata, omitNamespace, null, null, schemaPath);
	}

	public void clearMarshallerProperties() {
		marshallerProperties.clear();
	}

	public Marshaller.Listener getMarshallerListener() {
		return marshallerListener;
	}

	public Schema getSchema() {
		return schema;
	}

	public Unmarshaller.Listener getUnmarshallerListener() {
		return unmarshallerListener;
	}

	public boolean isOmitNamespace() {
		return omitNamespace;
	}

	public boolean isUseCdata() {
		return useCdata;
	}

	public <T> String marshall(String contextPath, JAXBElement<T> jaxbElement)
			throws JAXBException, XMLStreamException {
		String result = null;
		StringWriter writer = new StringWriter();
		try {
			marshall(writer, contextPath, jaxbElement);
			result = writer.toString();
		} catch (JAXBException e) {
			throw e;
		} catch (XMLStreamException e) {
			throw e;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
		return result;
	}

	public <T> void marshall(Writer writer, String contextPath,
			JAXBElement<T> jaxbElement) throws JAXBException,
			XMLStreamException {
		JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
		Marshaller marshaller = jaxbContext.createMarshaller();
		if (marshallerListener != null) {
			marshaller.setListener(marshallerListener);
		}
		if (schema != null) {
			marshaller.setSchema(schema);
		}
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
		for (Entry<String, Object> entry : marshallerProperties.entrySet()) {
			marshaller.setProperty(entry.getKey(), entry.getValue());
		}
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = xof.createXMLStreamWriter(writer);
		if (useCdata) {
			CDataXMLStreamWriter cDataXMLStreamWriter = new CDataXMLStreamWriter(
					xmlStreamWriter);
			xmlStreamWriter = cDataXMLStreamWriter;
		}
		if (omitNamespace) {
			xmlStreamWriter.setNamespaceContext(noNamespaceContext);
		}
		if (useCdata || omitNamespace) {
			marshaller.marshal(jaxbElement, xmlStreamWriter);
		} else {
			marshaller.marshal(jaxbElement, writer);
		}
		xmlStreamWriter.flush();
		xmlStreamWriter.close();
	}

	public void setMarshallerListener(Marshaller.Listener listener) {
		this.marshallerListener = listener;
	}

	public void setMarshallerProperty(String key, Object value) {
		marshallerProperties.put(key, value);
	}

	public void setOmitNamespace(boolean omitNamespace) {
		this.omitNamespace = omitNamespace;
	}

	public void setSchema(String... schemaPath) {
		if (schemaPath != null && schemaPath.length > 0) {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(W3C_XML_SCHEMA_NS_URI);
			Source[] schemas = new StreamSource[schemaPath.length];
			for (int i = 0; i < schemaPath.length; i++) {
				InputStream stream = AppUtil.getResourceAsStream(schemaPath[i]);
				schemas[i] = new StreamSource(stream);
			}
			try {
				schema = schemaFactory.newSchema(schemas);
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
	}

	public void setUnmarshallerListener(
			Unmarshaller.Listener unmarshallerListener) {
		this.unmarshallerListener = unmarshallerListener;
	}

	public void setUseCdata(boolean useCdata) {
		this.useCdata = useCdata;
	}

	public <T> T unmarshal(Class<T> klass, File sourceFile)
			throws MalformedURLException, IOException, JAXBException {
		return unmarshal(klass, sourceFile.toURI().toURL());
	}

	public <T> T unmarshal(Class<T> klass, InputStream source)
			throws JAXBException {
		T result = null;
		JAXBContext jaxbContext = JAXBContext.newInstance(klass.getPackage()
				.getName());
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		if (unmarshallerListener != null) {
			unmarshaller.setListener(unmarshallerListener);
		}
		if (schema != null) {
			unmarshaller.setSchema(schema);
		}
		@SuppressWarnings("unchecked")
		JAXBElement<T> t = (JAXBElement<T>) unmarshaller.unmarshal(source);
		result = t.getValue();
		return result;
	}

	public <T> T unmarshal(Class<T> klass, URL sourceURL) throws IOException,
			JAXBException {
		InputStream source = null;
		T result = null;
		URLConnection urlConnection;
		try {
			urlConnection = sourceURL.openConnection();
			source = urlConnection.getInputStream();
			result = unmarshal(klass, source);
		} catch (IOException e) {
			throw e;
		} catch (JAXBException e) {
			throw e;
		} finally {
			if (source != null) {
				source.close();
			}
		}
		return result;
	}

}
