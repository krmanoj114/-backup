package com.tpex.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class InterfaceFileUtilsTest {

	@Test
	void isDateTest() {
		assertTrue(InterfaceFileUtils.isDate("20/10/2020", "dd/MM/yyyy"));
		assertTrue(InterfaceFileUtils.isDate("2020-10-20", "yyyy-MM-dd"));
		assertFalse(InterfaceFileUtils.isDate("xxxxxxxx", "dd/MM/yyyy"));
		assertFalse(InterfaceFileUtils.isDate("20/10/2020", "xx/xx/xxxx"));
	}
	
	@Test
	void getBigDecimalTest() {
		assertEquals(new BigDecimal("1234567.89"), InterfaceFileUtils.getBigDecimal("123456789", 2));
		assertEquals(new BigDecimal("1.23456789"), InterfaceFileUtils.getBigDecimal("123456789", 8));
	}
	
	@Test
	void getContSNoTest() {
		String contSNo = InterfaceFileUtils.getContSNo("S1234");
		assertEquals(" S1234", contSNo);
		assertEquals(6, contSNo.length());
		assertEquals("CONT_SNO", InterfaceFileUtils.getContSNo("CONT_SNO"));
		assertNull(InterfaceFileUtils.getContSNo(null));
	}
	
	@Test
	void convertStringToLocalDateTest() {
		assertEquals(LocalDate.of(2020, 10, 20), InterfaceFileUtils.convertStringToLocalDate("20/10/2020", "dd/MM/yyyy"));
		assertNull(InterfaceFileUtils.convertStringToLocalDate("", "dd/MM/yyyy"));
	}
	
	@Test
	void convertStringToLocalDateTimeTest() {
		assertEquals(LocalDateTime.of(2020, 10, 20, 12, 34, 56), InterfaceFileUtils.convertStringToLocalDateTime("20/10/2020 12:34:56", "dd/MM/yyyy HH:mm:ss"));
		assertNull(InterfaceFileUtils.convertStringToLocalDateTime("", "dd/MM/yyyy HH:mm:ss"));
	}
	
	@Test
	void rtrimTest() {
		assertNull(InterfaceFileUtils.rtrim(null));
		assertEquals(" ABC", InterfaceFileUtils.rtrim(" ABC  "));
		assertEquals("", InterfaceFileUtils.rtrim("     "));
	}
	
	@Test
	void getNewFileNameIfExistsTest() {
		assertNotNull(InterfaceFileUtils.getNewFileNameIfExists("/home/TMAP/DSD/shared/batch/BINF005/input", "BINF005_configuration.json"));
	}

}
