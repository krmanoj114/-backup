package com.tpex.month.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
	
	private DateUtils() {
		
	}
	
	private static final String DATE_STD_PATTERN = "dd/MM/yyyy";
	private static final String DATETIME_PATTERN_YMD = "yyyyMMddHHmmss";
	
	public static LocalDate convertStringToLocalDate(String strDate) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_STD_PATTERN);
		return LocalDate.parse(strDate, format);
	}
	
	public static LocalDateTime convertStringToLocalDateTime(String strDate) {
		return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(DATETIME_PATTERN_YMD));
	}
	
	
	public static LocalDateTime convertSqlDateToLocalDateTimeOfEntityAttribute(java.sql.Timestamp dbData) {

		return dbData == null ? null : dbData.toLocalDateTime();
	}
}
