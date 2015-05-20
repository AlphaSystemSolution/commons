/**
 * 
 */
package com.alphasystem.util;

/**
 * @author Syed F Ali
 */
/**
 * Utilities for creating XML output in String Buffers. Used by XML services.
 */
public class XMLStringUtils {
	public static final String START_BRACKET = "<";

	public static final String END_BRACKET = ">";

	public static final String END_SLASH = "/";

	/**
	 * Creates a entry in the string buffer of
	 * <propertyName>object</propertyName>
	 * 
	 * @param output
	 *            StringBuilder of the output.
	 * @param value
	 * @param propertyName
	 */
	public static void outputPropertyNode(StringBuilder output, Object value,
			String propertyName) {
		appendStartNode(output, propertyName);
		if (value != null) {
			addCleanString(value.toString(), output);
		}
		appendEndNode(output, propertyName);
	}

	/**
	 * Creates a entry in the string buffer of
	 * <propertyName>object</propertyName>
	 * 
	 * @param output
	 *            StringBuilder of the output.
	 * @param value
	 * @param propertyName
	 */
	public static void outputPropertyNode(StringBuilder output, String value,
			String propertyName) {
		appendStartNode(output, propertyName);
		if (value != null) {
			addCleanString(value, output);
		}
		appendEndNode(output, propertyName);
	}

	/**
	 * Creates a entry in the string buffer of
	 * <propertyName>object</propertyName>
	 * 
	 * @param output
	 *            StringBuilder of the output.
	 * @param value
	 *            double value
	 * @param propertyName
	 */
	public static void outputPropertyNode(StringBuilder output, double value,
			String propertyName) {
		appendStartNode(output, propertyName);
		output.append(value);
		appendEndNode(output, propertyName);
	}

	/**
	 * Creates a entry in the string buffer of
	 * <propertyName>object</propertyName>
	 * 
	 * @param output
	 *            StringBuilder of the output.
	 * @param value
	 *            double value
	 * @param propertyName
	 */
	public static void outputPropertyNode(StringBuilder output, long value,
			String propertyName) {
		appendStartNode(output, propertyName);
		output.append(value);
		appendEndNode(output, propertyName);
	}

	/**
	 * Creates a entry in the string buffer of
	 * <propertyName>object</propertyName>
	 * 
	 * @param output
	 *            StringBuilder of the output.
	 * @param value
	 *            boolean value
	 * @param propertyName
	 */
	public static void outputPropertyNode(StringBuilder output, boolean value,
			String propertyName) {
		outputPropertyNode(output, "" + value, propertyName);
	}

	/**
	 * Append a start tag to the string buffer.
	 * 
	 * @param output
	 *            StringBuilder of the output.
	 * @param propertyName
	 */
	public static void appendStartNode(StringBuilder output, String propertyName) {
		output.append(START_BRACKET);
		output.append(getCleanPropertyName(propertyName));
		output.append(END_BRACKET);
	}

	/**
	 * @param output
	 * @param propertyName
	 */
	public static void appendEmptyNode(StringBuilder output, String propertyName) {
		output.append(START_BRACKET).append(getCleanPropertyName(propertyName))
				.append(END_SLASH).append(END_BRACKET);
	}

	/**
	 * Append a start tag to the string buffer.
	 * 
	 * @param output
	 *            StringBuilder of the output.
	 * @param propertyName
	 */
	public static void appendStartNodeWithAttribute(StringBuilder output,
			String propertyName, String attrName, Object attrValue) {
		output.append(START_BRACKET);
		output.append(getCleanPropertyName(propertyName));
		output.append(" ");
		output.append(attrName);
		output.append("=\"");
		addCleanString("" + ((attrValue != null) ? attrValue : ""), output);
		output.append("\"");
		output.append(END_BRACKET);
	}

	/**
	 * Append a start tag with given attributes.
	 * 
	 * @param output
	 * @param propertyName
	 * @param attibuteNames
	 * @param attributeValues
	 */
	public static void appendStartNodeWithAttributes(StringBuilder output,
			String propertyName, String[] attibuteNames,
			String[] attributeValues) {
		output.append(START_BRACKET);
		output.append(getCleanPropertyName(propertyName));
		output.append(" ");
		int len = attributeValues.length;
		if (len != attibuteNames.length) {
			len = -1;
		}
		if (len > 0) {
			String attrName = attibuteNames[0];
			String attrValue = attributeValues[0];
			output.append(attrName);
			output.append("=\"");
			addCleanString("" + ((attrValue != null) ? attrValue : ""), output);
			output.append("\" ");
			for (int i = 1; i < len; i++) {
				attrName = attibuteNames[i];
				attrValue = attributeValues[i];
				output.append(attrName);
				output.append("=\"");
				addCleanString("" + ((attrValue != null) ? attrValue : ""),
						output);
				output.append("\"");
			}
		}

		output.append(END_BRACKET);
	}

	/**
	 * Append and end tag.
	 * 
	 * @param output
	 *            StringBuilder of the output.
	 * @param propertyName
	 */
	public static void appendEndNode(StringBuilder output, String propertyName) {
		output.append(START_BRACKET);
		output.append(END_SLASH);
		output.append(getCleanPropertyName(propertyName));
		output.append(END_BRACKET);
	}

	/**
	 * Checks that the first letter in the string is a letter and not a digit.
	 * If this is true is returns the untouched properyName
	 * <p/>
	 * If it starts with something other then a letter then it prepends an 'n'
	 * character to the front of the string.
	 * 
	 * @param propertyName
	 * @return a property name that started with 'n' is it starts with something
	 *         other then a letter.
	 */

	public static String getCleanPropertyName(String propertyName) {
		StringBuilder sb = new StringBuilder();
		if (propertyName != null && propertyName.length() > 0) {
			char firstValue = propertyName.charAt(0);
			if (Character.isDigit(firstValue)
					|| !Character.isLetter(firstValue)) {
				// propertyName = "n" + propertyName;
				sb.append('n');
			}
			for (int i = 0; i < propertyName.length(); i++) {
				char iChar = propertyName.charAt(i);
				if (Character.isLetterOrDigit(iChar) || iChar == '.'
						|| iChar == '-' || iChar == '_' || iChar == ':') {
					sb.append(iChar);
				}
			}
		}

		return sb.toString();

	}

	/**
	 * changes < & > to &lt; &amp; &gt; &
	 * 
	 * @param value
	 * @param output
	 */
	public static void addCleanString(String value, StringBuilder output) {
		for (int i = 0; i < value.length(); i++) {
			char iChar = value.charAt(i);
			if (iChar == '<') {
				output.append("&lt;");
			} else if (iChar == '>') {
				output.append("&gt;");
			} else if (iChar == '&') {
				output.append("&amp;");
			} /*
			 * else if (iChar == '[') { output.append("&#91;"); } else if (iChar
			 * == ']') { output.append("&#93;"); }
			 */else {
				output.append(iChar);
			}
		}
	}

}
