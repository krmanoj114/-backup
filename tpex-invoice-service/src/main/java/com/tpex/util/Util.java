package com.tpex.util;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Util {

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

	public static void main(String args[]) {

		// Example 1
		DateTimeFormatter formatter_1 = DateTimeFormatter.ofPattern("d/MM/yyyy");
		String str_date_1 = "24/09/2019";

		LocalDate local_date_1 = LocalDate.parse(str_date_1, formatter_1);


		// Example 2
		DateTimeFormatter formatter_2 = DateTimeFormatter.ofPattern("MMM d yyyy");
		String str_date_2 = "Sep 24 2019";

		LocalDate local_date_2 = LocalDate.parse(str_date_2, formatter_2);


		// Example 3
		DateTimeFormatter formatter_3 = DateTimeFormatter.ofPattern("d-MMM-yyyy");
		String str_date_3 = "24-Sep-2019";

		LocalDate local_date_3 = LocalDate.parse(str_date_3, formatter_3);


	}

	public static String threeDecimal(Double data) {
		return data == null ? null : new DecimalFormat("0.000").format(data);
	}

	public static String twoDecimal(Double data) {
		return data == null ? null : new DecimalFormat("0.00").format(data);
	}
}
