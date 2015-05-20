package com.alphasystem.util;

import static java.lang.System.getProperty;

import java.awt.Font;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JTextArea;

import com.alphasystem.BusinessException;

public class Utils {

	public static final Font DEFAULT_FONT = new Font("Courier", Font.PLAIN, 12);
	public static final String NEW_LINE = getProperty("line.separator");
	public static final String TAB = "    ";
	public static final String SEPARATOR = File.separator;
	public static final String USER_DIR = getProperty("user.dir", ".");
	public static final File CURRENT_USER_DIR = new File(USER_DIR);
	public static final String USER_HOME = getProperty("user.home", USER_DIR);
	public static final File USER_TEMP_DIR = new File(getProperty(
			"java.io.tmpdir", USER_HOME));
	public static final File USER_HOME_DIR = new File(USER_HOME);

	private static ClassLoader cl = null;

	static {
		cl = Thread.currentThread().getContextClassLoader();
	}

	// ~ Methods ...............................................................

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static JTextArea createTextArea() {
		return createTextArea(DEFAULT_FONT);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param font
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 */
	public static JTextArea createTextArea(Font font) {
		return createTextArea(font, 30, 150);
	}

	public static JTextArea createTextArea(Font font, int rows, int columns) {
		JTextArea area = new JTextArea(rows, columns);
		area.setWrapStyleWord(true);
		area.setLineWrap(true);
		area.setFont(font);
		return area;
	}

	public static JTextArea createTextArea(int rows, int columns) {
		return createTextArea(DEFAULT_FONT, rows, columns);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 * @throws BusinessException
	 *             DOCUMENT ME!
	 */
	public static ImageIcon getImageIcon(String name) throws BusinessException {
		return new ImageIcon(getURL(name));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param timeConsumed
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 */
	public static String getTimeConsumed(long timeConsumed) {
		long miliSec = timeConsumed % 1000;
		timeConsumed /= 1000;
		long seconds = timeConsumed % 60;
		timeConsumed /= 60;
		long minutes = timeConsumed % 60;
		timeConsumed /= 60;
		long hours = timeConsumed % 60;
		List<Long> temp = new ArrayList<Long>();
		StringBuffer format = new StringBuffer();
		if (hours > 0) {
			format.append("%02d:");
			temp.add(new Long(hours));
		}
		temp.add(new Long(minutes));
		temp.add(new Long(seconds));
		temp.add(new Long(miliSec));
		format.append("%02d:%02d:%03d");
		Object[] params = new Object[temp.size()];
		params = temp.toArray(params);
		return String.format(format.toString(), params);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 * @throws BusinessException
	 *             DOCUMENT ME!
	 */
	public static URL getURL(String name) throws BusinessException {
		URL url = cl.getResource(name);
		if (url == null) {
			throw new BusinessException("UNKNOWN",
					"Could not able to find file " + name);
		}
		return url;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param first
	 *            DOCUMENT ME!
	 * @param second
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 */
	@SuppressWarnings("rawtypes")
	public static Properties mergeProperties(Properties first, Properties second) {
		Properties result = new Properties(second);
		Enumeration enums = first.propertyNames();
		while (enums.hasMoreElements()) {
			String key = (String) enums.nextElement();
			result.setProperty(key, first.getProperty(key));
		}
		return result;
	}
}
