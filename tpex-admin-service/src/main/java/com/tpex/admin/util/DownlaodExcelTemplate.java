package com.tpex.admin.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class DownlaodExcelTemplate {

	private DownlaodExcelTemplate() {
		
	}
	public static ByteArrayInputStream downloadTemplateAsExcel(List<String> headers, String sheetName) throws IOException {

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(sheetName);
			Row headerRow = sheet.createRow(0);
			for (int col = 0; col < headers.size(); col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(headers.get(col));
			}
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new IOException("fail to import data to Excel file: " + e.getMessage());
		}
	}

}
