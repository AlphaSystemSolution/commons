/**
 * 
 */
package com.alphasystem.util;

import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.alphasystem.util.AppUtil.getResourceAsStream;
import static com.alphasystem.util.JAXBUtil.XMLGregorianCalendarDateFormat.*;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

/**
 * @author Syed Farhan Ali
 * 
 */
public class JAXBUtil {

	public enum XMLGregorianCalendarDateFormat {
		NO_TIME_TRUNCATION, TIME_SETTTO_ZERO, TIME_PART_UNDEFINED
	}

	public static XMLGregorianCalendar calendarFromDate(Date date) {
		return calendarFromDate(date, TIME_SETTTO_ZERO);
	}

	// This method takes a java.util.date and converts to XMLGregorianCalendar.
	// It truncates the time
	// part according to the enum value passed.
	public static XMLGregorianCalendar calendarFromDate(Date date,
			XMLGregorianCalendarDateFormat format) {
		if (date == null)
			return null;
		try {
			final GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			final XMLGregorianCalendar xmlCalendar = DatatypeFactory
					.newInstance().newXMLGregorianCalendar(cal);
			switch (format) {
			case NO_TIME_TRUNCATION: // Time is unchanged. So it will have
				// actual time
				break;
			case TIME_SETTTO_ZERO: // Time part is set to zero
				xmlCalendar.setTime(0, 0, 0, 0);
				break;
			case TIME_PART_UNDEFINED: // return Date will not have time part
				xmlCalendar.setHour(DatatypeConstants.FIELD_UNDEFINED);
				xmlCalendar.setMinute(DatatypeConstants.FIELD_UNDEFINED);
				xmlCalendar.setSecond(DatatypeConstants.FIELD_UNDEFINED);
				break;
			default:
				break;
			}
			return xmlCalendar;
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static XMLGregorianCalendar calendarFromDateNoTimeTrunc(Date date) {
		return calendarFromDate(date, NO_TIME_TRUNCATION);
	}

	public static XMLGregorianCalendar calendarFromDateWithTimeUndefined(
			Date date) {
		return calendarFromDate(date, TIME_PART_UNDEFINED);
	}

	public static Calendar calendarFromXmlCalendar(XMLGregorianCalendar xmlCal) {
		if (xmlCal == null)
			return null;
		return xmlCal.toGregorianCalendar();
	}

	public static Date dateFromCalendar(XMLGregorianCalendar xmlCal) {
		if (xmlCal == null)
			return null;
		return xmlCal.toGregorianCalendar().getTime();
	}

	public static <T> void marshall(File file, String contextPath,
			JAXBElement<T> jaxbElement, Marshaller.Listener listener,
			String... schemaPath) {
		try {
			Path path = Paths.get(file.toURI());
			OutputStream outputStream = Files.newOutputStream(path);
			marshall(outputStream, contextPath, jaxbElement, listener, schemaPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> void marshall(File file, String contextPath,
			JAXBElement<T> jaxbElement, String... schemaPath) {
		marshall(file, contextPath, jaxbElement, null, schemaPath);
	}

	public static <T> void marshall(OutputStream stream, String contextPath,
			JAXBElement<T> jaxbElement, Marshaller.Listener listener,
			String... schemaPath) {
		try(OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-16")) {
			marshall(writer, contextPath, jaxbElement, listener, schemaPath);
		} catch ( IOException | SAXException | JAXBException e) {
			e.printStackTrace();
		}
	}

	public static <T> void marshall(OutputStream stream, String contextPath,
			JAXBElement<T> jaxbElement, String... schemaPath) {
		marshall(stream, contextPath, jaxbElement, null, schemaPath);
	}

	public static <T> String marshall(String contextPath,
			JAXBElement<T> jaxbElement, Marshaller.Listener listener,
			boolean useCdata, String... schemaPath) throws JAXBException,
			SAXException, XMLStreamException {
		String result = null;
		StringWriter writer = new StringWriter();
		try {
			marshall(writer, contextPath, jaxbElement, listener, useCdata,
					schemaPath);
			result = writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
		return result;
	}

	public static <T> String marshall(String contextPath,
			JAXBElement<T> jaxbElement, Marshaller.Listener listener,
			String... schemaPath) {
		String result = null;
		StringWriter writer = new StringWriter();
		try {
			marshall(writer, contextPath, jaxbElement, listener, schemaPath);
			result = writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
		return result;
	}

	public static <T> String marshall(String contextPath,
			JAXBElement<T> jaxbElement, String... schemaPath) {
		return marshall(contextPath, jaxbElement, null, schemaPath);
	}

	public static <T> void marshall(Writer writer, String contextPath,
			JAXBElement<T> jaxbElement, Marshaller.Listener listener,
			boolean useCdata, String... schemaPath) throws JAXBException,
			SAXException, XMLStreamException {
		JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
		Marshaller marshaller = jaxbContext.createMarshaller();
		if (listener != null) {
			marshaller.setListener(listener);
		}
		if (schemaPath != null && schemaPath.length > 0) {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(W3C_XML_SCHEMA_NS_URI);
			Source[] schemas = new StreamSource[schemaPath.length];
			for (int i = 0; i < schemaPath.length; i++) {
				InputStream stream = AppUtil.getResourceAsStream(schemaPath[i]);
				schemas[i] = new StreamSource(stream);
			}
			Schema schema = schemaFactory.newSchema(schemas);
			marshaller.setSchema(schema);
		}
		marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
		if (useCdata) {
			XMLOutputFactory xof = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlStreamWriter = xof.createXMLStreamWriter(writer);
			CDataXMLStreamWriter cDataXMLStreamWriter = new CDataXMLStreamWriter(
					xmlStreamWriter);
			marshaller.marshal(jaxbElement, cDataXMLStreamWriter);
			cDataXMLStreamWriter.flush();
			cDataXMLStreamWriter.close();
		} else {
			marshaller.marshal(jaxbElement, writer);
		}
	}

	public static <T> void marshall(Writer writer, String contextPath,
			JAXBElement<T> jaxbElement, Marshaller.Listener listener,
			String... schemaPath) throws JAXBException, SAXException {
		try {
			marshall(writer, contextPath, jaxbElement, listener, false,
					schemaPath);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	public static <T> T unmarshal(Class<T> klass, File sourceFile,
			String... schemaPath) {
		return unmarshal(klass, sourceFile, null, schemaPath);
	}

	public static <T> T unmarshal(Class<T> klass, File sourceFile,
			Unmarshaller.Listener listener, String... schemaPath) {
		if (!sourceFile.exists()) {
			return null;
		}
		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(sourceFile));
			return unmarshal(klass, stream, listener, schemaPath);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(Class<T> klass, InputStream source,
			Unmarshaller.Listener listener, String... schemaPath) {
		T result = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(klass
					.getPackage().getName());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			if (listener != null) {
				unmarshaller.setListener(listener);
			}
			if (schemaPath != null && schemaPath.length > 0) {
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance(W3C_XML_SCHEMA_NS_URI);
				Source[] schemas = new StreamSource[schemaPath.length];
				for (int i = 0; i < schemaPath.length; i++) {
					InputStream stream = getResourceAsStream(schemaPath[i]);
					schemas[i] = new StreamSource(stream);
				}
				Schema schema = schemaFactory.newSchema(schemas);
				unmarshaller.setSchema(schema);
			}
			Object obj = unmarshaller.unmarshal(source);
			if (obj.getClass().getName().equals(klass.getName())) {
				result = (T) obj;
			} else {
				JAXBElement<T> t = (JAXBElement<T>) obj;
				result = t.getValue();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return result;
	}

	public static <T> T unmarshal(Class<T> klass, URL sourceURL,
			String... schemaPath) {
		return unmarshal(klass, sourceURL, null, schemaPath);
	}

	public static <T> T unmarshal(Class<T> klass, URL sourceURL,
			Unmarshaller.Listener listener, String... schemaPath) {
		InputStream source = null;
		T result = null;
		try {
			URLConnection urlConnection = sourceURL.openConnection();
			source = urlConnection.getInputStream();
			result = unmarshal(klass, source, listener, schemaPath);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (source != null) {
				try {
					source.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}
}
