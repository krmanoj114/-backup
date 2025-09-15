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

import lombok.experimental.UtilityClass;

/**
 * Instantiates a new date util.
 */
@UtilityClass
public class DateUtil {
	
	/** The Constant DD_MM_YYYY. */
	private static final String DD_MM_YYYY ="dd/MM/yyyy";

	/**
	 * Convert sql date to local date time of entity attribute.
	 *
	 * @param dbData the db data
	 * @return the local date time
	 */
	public static LocalDateTime convertSqlDateToLocalDateTimeOfEntityAttribute(java.sql.Timestamp dbData) {

		return dbData == null ? null : dbData.toLocalDateTime();
	}

	/**
	 * Date from string date formate.
	 *
	 * @param dateFormate the date formate
	 * @param stringDate the string date
	 * @return the timestamp
	 */
	public static Timestamp dateFromStringDateFormate(String dateFormate, String stringDate) {
		LocalDateTime requestDate = null;
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate)
				&& !"null".equalsIgnoreCase(stringDate)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormate);
			requestDate = LocalDateTime.parse(dateFormate, formatter);
		}
		return convertToDatabaseColumn(requestDate);
	}

	/**
	 * Convert to database column.
	 *
	 * @param attribute the attribute
	 * @return the java.sql. timestamp
	 */
	public static java.sql.Timestamp convertToDatabaseColumn(java.time.LocalDateTime attribute) {

		return attribute == null ? null : java.sql.Timestamp.valueOf(attribute);
	}

	/**
	 * Convert to sql date.
	 *
	 * @param attribute the attribute
	 * @return the java.sql. date
	 */
	public static java.sql.Date convertToSqlDate(java.time.LocalDate attribute) {

		return attribute == null ? null : java.sql.Date.valueOf(attribute);
	}

	/**
	 * Date from string date formatefor invoice date.
	 *
	 * @param dateFormate the date formate
	 * @param stringDate the string date
	 * @return the java.sql. date
	 */
	public static java.sql.Date dateFromStringDateFormateforInvoiceDate(String dateFormate, String stringDate) {
		LocalDate requestDate = LocalDate.now();
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate)
				&& !"null".equalsIgnoreCase(stringDate)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormate);
			requestDate = LocalDate.parse(stringDate, formatter);
		}
		return convertToSqlDate(requestDate);
	}

	/**
	 * Date from string sql date formate.
	 *
	 * @param dateFormate the date formate
	 * @param stringDate the string date
	 * @return the java.sql. date
	 */
	public static java.sql.Date dateFromStringSqlDateFormate(String dateFormate, String stringDate) {
		LocalDate requestDate = null;
		if (StringUtils.isNotBlank(dateFormate) && StringUtils.isNotBlank(stringDate)
				&& !"null".equalsIgnoreCase(stringDate)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormate);
			requestDate = LocalDate.parse(stringDate, formatter);
		}
		return convertToSqlDate(requestDate);
	}

	/**
	 * Convert sql date to local date of entity attribute.
	 *
	 * @param dbDate the db date
	 * @return the local date
	 */
	public static LocalDate convertSqlDateToLocalDateOfEntityAttribute(java.sql.Date dbDate) {
		return dbDate == null ? null : dbDate.toLocalDate();
	}

	/**
	 * To date.
	 *
	 * @param timestamp the timestamp
	 * @return the string
	 */
	public static String toDate(LocalDateTime timestamp) {
		long milliSeconds = Timestamp.valueOf(timestamp).getTime();
		Date date = new Date(milliSeconds);
		return new SimpleDateFormat(DD_MM_YYYY).format(date);
	}

	/**
	 * String date.
	 *
	 * @param timestamp the timestamp
	 * @return the string
	 */
	public static String stringDate(String timestamp) {
		DateFormat objFormate = new SimpleDateFormat(DD_MM_YYYY);
		return timestamp == null ? null : objFormate.format(new Date(Long.valueOf(timestamp)));
	}
	
	/**
	 * Gets the string date.
	 *
	 * @param date the date
	 * @return the string date
	 * @throws ParseException the parse exception
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
