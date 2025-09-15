package com.tpex.month.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

class DateUtilsTest {

	@Test
	void convertStringToLocalDateTest() {
		LocalDate localDate = LocalDate.of(2023, Month.MARCH, 9);

		LocalDate localDateConv = DateUtils.convertStringToLocalDate("09/03/2023");

		assertEquals(localDate, localDateConv);
	}
	
	@Test
	void convertStringToLocalDateTimeTest() {
		LocalDateTime localDateTime = LocalDateTime.of(2023, Month.MARCH, 9, 16, 17, 18);

		LocalDateTime localDateTimeConv = DateUtils.convertStringToLocalDateTime("20230309161718");

		assertEquals(localDateTime, localDateTimeConv);
	}

}
