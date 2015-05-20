/**
 * 
 */
package com.alphasystem.xml.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author sali
 * 
 */
public class DateXmlAdapter extends XmlAdapter<String, Date> {

	public static final String DATE_PATTERN = "yyyy-MM-dd";

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			DATE_PATTERN);

	@Override
	public String marshal(Date date) throws Exception {
		if (date == null) {
			return null;
		}
		return FORMAT.format(date);
	}

	@Override
	public Date unmarshal(String v) throws Exception {
		Date date = null;
		try {
			date = FORMAT.parse(v);
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}

}
