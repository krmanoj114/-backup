package com.tpex.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {
	
	private static final String DD_MM_YYYY ="dd/MM/yyyy";

	public static LocalDateTime convertSqlDateToLocalDateTimeOfEntityAttribute(java.sql.Timestamp dbData) {

		return dbData == null ? null : dbData.toLocalDateTime();
	}

	public static Timestamp dateFromStringDateFormate(String dateFormate, String stringDate) {
		LocalDateTime requestDate = null;
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate)
				&& !"null".equalsIgnoreCase(stringDate)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormate);
			requestDate = LocalDateTime.parse(dateFormate, formatter);
		}
		return convertToDatabaseColumn(requestDate);
	}

	public static java.sql.Timestamp convertToDatabaseColumn(java.time.LocalDateTime attribute) {

		return attribute == null ? null : java.sql.Timestamp.valueOf(attribute);
	}

	public static java.sql.Date convertToSqlDate(java.time.LocalDate attribute) {

		return attribute == null ? null : java.sql.Date.valueOf(attribute);
	}

	public static java.sql.Date dateFromStringDateFormateforInvoiceDate(String dateFormate, String stringDate) {
		LocalDate requestDate = LocalDate.now();
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate)
				&& !"null".equalsIgnoreCase(stringDate)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormate);
			requestDate = LocalDate.parse(stringDate, formatter);
		}
		return convertToSqlDate(requestDate);
	}

	public static java.sql.Date dateFromStringSqlDateFormate(String dateFormate, String stringDate) {
		LocalDate requestDate = null;
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate)
				&& !"null".equalsIgnoreCase(stringDate)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormate);
			requestDate = LocalDate.parse(stringDate, formatter);
		}
		return convertToSqlDate(requestDate);
	}

	public static LocalDate convertSqlDateToLocalDateOfEntityAttribute(java.sql.Date dbDate) {
		return dbDate == null ? null : dbDate.toLocalDate();
	}

	/**
	 * method convert LocalDateTime to Date ex 2022-05-12T00:00 convert to
	 * 12/05/2022
	 * 
	 * @param timestamp
	 * @return String
	 * @author Mohd.Javed
	 */
	public static String toDate(LocalDateTime timestamp) {
		long milliSeconds = Timestamp.valueOf(timestamp).getTime();
		Date date = new Date(milliSeconds);
		return new SimpleDateFormat(DD_MM_YYYY).format(date);
	}

	public static String stringDate(String timestamp) {
		DateFormat objFormate = new SimpleDateFormat(DD_MM_YYYY);
		return timestamp == null ? null : objFormate.format(new Date(Long.valueOf(timestamp)));
	}
   /**
    * @author Mohd.Javed
    * Method for convert date format from dd/mm/yyyy to dd-MMM-yy
    * @param date
    * @return String date
    * @throws ParseException
    */
	public static String getStringDate(String date) throws ParseException {

		SimpleDateFormat inputFormat = new SimpleDateFormat(DD_MM_YYYY);
		SimpleDateFormat reqFormat = new SimpleDateFormat("dd-MMM-yy");

		return date == null ? null : reqFormat.format(inputFormat.parse(date));

	}
	
	/**
	 * Gets the string date.
	 *
	 * @param inputDate the input date
	 * @param inputDateFormat the input date format
	 * @param outputDateFormat the output date format
	 * @return the string date
	 * @throws ParseException the parse exception
	 */
	public static String getStringDate(String inputDate, String inputDateFormat, String outputDateFormat) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat(inputDateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);
		return inputDate == null ? null : outputFormat.format(inputFormat.parse(inputDate));
	}
}
