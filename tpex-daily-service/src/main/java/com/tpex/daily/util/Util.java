package com.tpex.daily.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class Util {
	
	private Util() {
		
	}

	public static Date dateFromStringDateFormate(String dateFormate, String stringDate) throws ParseException {
		Date requestDate = null;
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate))
			requestDate = new SimpleDateFormat(dateFormate).parse(stringDate);
		return requestDate;
	}

	public static Date dateFromStringDateFormateforInvoiceDate(String dateFormate, String stringDate)
			throws ParseException {
		Date requestDate = new Date();
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate))
			requestDate = new SimpleDateFormat(dateFormate).parse(stringDate);
		return requestDate;
	}

	public static boolean nullCheck(String value) {
		return (StringUtils.isNotEmpty(value) && !"null".equalsIgnoreCase(value));
	}
	
	public static String threeDecimal(Double data) {
		return data == null ? null : new DecimalFormat("0.000").format(data);
	}

	public static String twoDecimal(Double data) {
		return data == null ? null : new DecimalFormat("0.00").format(data);
	}
}
