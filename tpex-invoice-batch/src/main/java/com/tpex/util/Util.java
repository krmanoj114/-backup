package com.tpex.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Instantiates a new util.
 */
@UtilityClass
public class Util {

	/**
	 * Date from string date formate.
	 *
	 * @param dateFormate the date formate
	 * @param stringDate the string date
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date dateFromStringDateFormate(String dateFormate, String stringDate) throws ParseException {
		Date requestDate = null;
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate))
			requestDate = new SimpleDateFormat(dateFormate).parse(stringDate);
		return requestDate;
	}

	/**
	 * Date from string date formatefor invoice date.
	 *
	 * @param dateFormate the date formate
	 * @param stringDate the string date
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date dateFromStringDateFormateforInvoiceDate(String dateFormate, String stringDate)
			throws ParseException {
		Date requestDate = new Date();
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate))
			requestDate = new SimpleDateFormat(dateFormate).parse(stringDate);
		return requestDate;
	}

	/**
	 * Null check.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public static boolean nullCheck(String value) {
		return (StringUtils.isNotEmpty(value) && !"null".equalsIgnoreCase(value));
	}

	/**
	 * Three decimal.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String threeDecimal(Double data) {
		return data == null ? null : new DecimalFormat("0.000").format(data);
	}

	/**
	 * Two decimal.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String twoDecimal(Double data) {
		return data == null ? null : new DecimalFormat("0.00").format(data);
	}
}
