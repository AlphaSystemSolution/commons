/**
 *
 */
package com.alphasystem.util;

import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

/**
 * @author sali
 */
public final class JAXBTool {

    private boolean useCdata;
    private boolean omitNamespace;
    private Marshaller.Listener marshallerListener;
    private Unmarshaller.Listener unMarshallerListener;
    private Schema schema;
    private NamespaceContext noNamespaceContext;
    private Map<String, Object> marshallerProperties = new HashMap<String, Object>();

    public JAXBTool() {
        noNamespaceContext = new NoNamespaceContext();
    }

    public void clearMarshallerProperties() {
        marshallerProperties.clear();
    }

    public Marshaller.Listener getMarshallerListener() {
        return marshallerListener;
    }

    public void setMarshallerListener(Marshaller.Listener listener) {
        this.marshallerListener = listener;
    }

    public Schema getSchema() {
        return schema;
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

    public Unmarshaller.Listener getUnMarshallerListener() {
        return unMarshallerListener;
    }

    public void setUnMarshallerListener(
            Unmarshaller.Listener unMarshallerListener) {
        this.unMarshallerListener = unMarshallerListener;
    }

    public boolean isOmitNamespace() {
        return omitNamespace;
    }

    public void setOmitNamespace(boolean omitNamespace) {
        this.omitNamespace = omitNamespace;
    }

    public boolean isUseCdata() {
        return useCdata;
    }

    public void setUseCdata(boolean useCdata) {
        this.useCdata = useCdata;
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

    public void setMarshallerProperty(String key, Object value) {
        marshallerProperties.put(key, value);
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
        if (unMarshallerListener != null) {
            unmarshaller.setListener(unMarshallerListener);
        }
        if (schema != null) {
            unmarshaller.setSchema(schema);
        }
        @SuppressWarnings("unchecked")
        JAXBElement<T> t = (JAXBElement<T>) unmarshaller.unmarshal(source);
        result = t.getValue();
        return result;
    }

    public <T> T unmarshal(Class<T> klass, Reader source)
            throws JAXBException {
        T result = null;
        JAXBContext jaxbContext = JAXBContext.newInstance(klass.getPackage()
                .getName());
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        if (unMarshallerListener != null) {
            unmarshaller.setListener(unMarshallerListener);
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

    public JAXBTool withUseCdata(boolean useCdata) {
        setUseCdata(useCdata);
        return this;
    }

    public JAXBTool withOmitNamespace(boolean omitNamespace) {
        setOmitNamespace(omitNamespace);
        return this;
    }

    public JAXBTool withUnMarshallerListener(Unmarshaller.Listener listener) {
        setUnMarshallerListener(listener);
        return this;
    }

    public JAXBTool withMarshallerListener(Marshaller.Listener listener) {
        setMarshallerListener(listener);
        return this;
    }

    public JAXBTool withSchema(String... schemaPath) {
        setSchema(schemaPath);
        return this;
    }

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

}
