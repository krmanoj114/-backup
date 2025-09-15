package com.tpex.daily.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtil {

	private DateUtil() {
		
	}
	public static LocalDateTime convertSqlDateToLocalDateTimeOfEntityAttribute(java.sql.Timestamp dbData) {

		return dbData == null ? null : dbData.toLocalDateTime();
	}

	public static LocalDate convertSqlDateToLocalDateOfEntityAttribute(java.sql.Date dbDate) {
		return dbDate == null ? null : dbDate.toLocalDate();
	}

}
