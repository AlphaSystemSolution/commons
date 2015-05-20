package com.alphasystem.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import com.alphasystem.BusinessException;

public class PropertiesReader {

	private Properties props;
	private URLConnection urlConn;

	public PropertiesReader(URL fileUrl, PropertiesReader defaults)
			throws BusinessException {
		if (defaults != null) {
			props = new Properties(defaults.props);
		} else {
			props = new Properties();
		}
		try {
			urlConn = fileUrl.openConnection();
			InputStream ins = urlConn.getInputStream();
			props.load(ins);
			ins.close();
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage(), ex);
		}
	}

	public PropertiesReader(URL fileUrl) throws BusinessException {
		this(fileUrl, null);
	}

	public PropertiesReader(String fileName) throws BusinessException {
		this(AppUtil.getUrl(fileName));
	}

	public PropertiesReader(String fileName, String defaultPropsFile)
			throws BusinessException {
		this(AppUtil.getUrl(fileName), new PropertiesReader(defaultPropsFile));
	}

	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}

	public void addToProperty(String key, String value) {
		String values = props.getProperty(key);
		if (values == null) {
			return;
		}
		values += "," + value;
		setProperty(key, values);
	}

	public String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	public int getPropertyAsInteger(String key) {
		return getPropertyAsInteger(key, 0);
	}

	public int getPropertyAsInteger(String key, int defaultValue) {
		String value = getProperty(key, String.valueOf(defaultValue));
		int decimalIndex = value.indexOf(".");
		if (decimalIndex >= 0) {
			value = value.substring(0, decimalIndex);
		}
		return Integer.parseInt(value);
	}

	public String[] getPropertyAsArray(String key) {
		String value = props.getProperty(key);
		if (value == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(value, ",");
		int length = st.countTokens();
		if (length <= 0) {
			return null;
		}
		String[] strArray = new String[length];
		for (int i = 0; i < strArray.length; i++) {
			strArray[i] = st.nextToken().trim();
		}
		return strArray;
	}

	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public void store(String header) {
		try {
			OutputStream os = urlConn.getOutputStream();
			props.store(os, header);
			os.close();
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Error in writing file ");
		}
	}

	public void list() {
		props.list(System.out);
	}

	@SuppressWarnings({ "rawtypes" })
	public Enumeration propertyNames() {
		return props.propertyNames();
	}

	public Properties getProperties() {
		return props;
	}

}
