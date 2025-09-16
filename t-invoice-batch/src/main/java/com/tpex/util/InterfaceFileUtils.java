package com.tpex.util;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class InterfaceFileUtils {

	/**
	 * check string is it can parse to LocalDate
	 * 
	 * @param strDate
	 * @param format
	 * @return boolean
	 */
	public static boolean isDate(String strDate, String format) {
		try {
			LocalDate.parse(strDate, DateTimeFormatter.ofPattern(format));
		} catch (IllegalArgumentException | DateTimeParseException ex) {
			return false;
		}
		return true;
	}

	/**
	 * sub the string to BigDeciaml
	 * 
	 * @param strNumberic   999999
	 * @param decimalPlaces 2
	 * @return BigDecimal 9999.99
	 */
	public static BigDecimal getBigDecimal(String strNumberic, int decimalPlaces) {
		int len = strNumberic.length();
		StringBuilder sbufTemp = new StringBuilder(strNumberic.substring(0, len - decimalPlaces));
		sbufTemp.append(".");
		sbufTemp.append(strNumberic.substring(len - decimalPlaces));
		return new BigDecimal(sbufTemp.toString());
	}

	public static String getContSNo(String contSNo) {
		if (contSNo != null && contSNo.length() == 5) {
			contSNo = " " + contSNo;
		}
		return contSNo;
	}

	public static LocalDate convertStringToLocalDate(String strDate, String format) {
		if (StringUtils.isBlank(strDate)) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDate.parse(strDate, formatter);
	}

	public static LocalDateTime convertStringToLocalDateTime(String strDate, String format) {
		if (StringUtils.isBlank(strDate)) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.parse(strDate, formatter);
	}

	public static String rtrim(String str) {
		if (str == null) {
			return str;
		}
		while (str.length() > 0) {
			char ch = str.charAt(str.length() - 1);
			if (ch == ' ') {
				str = str.substring(0, str.length() - 1);
			} else {
				return str;
			}
		}
		return str;
	}
	
	public static String getNewFileNameIfExists(String path, String fileName) {
		int num = 0;
		int idxExt = fileName.lastIndexOf(".") != -1 ? fileName.lastIndexOf(".") : fileName.length();
		String fileNameNoExt = fileName.substring(0, idxExt);
		String ext = fileName.substring(idxExt);
		File file = new File(path, fileName);
		while (file.exists()) {
			fileName = fileNameNoExt + " (" + (++num) + ")" + ext;
			file = new File(path, fileName);
		}
		return fileName;
	}
}
