package com.tpex.month.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.tpex.month.exception.InvalidFileException;
import com.tpex.month.model.entity.TpexConfigEntity;
import com.tpex.month.model.repository.TpexConfigRepository;

@Component
public class TpexConfigUtils {

	@Autowired
	private TpexConfigRepository tpexConfigRepository;
	
	private static Map<String, String> configMap = new HashMap<>();

	@PostConstruct
	public void init() {
		List<TpexConfigEntity> allConfig = tpexConfigRepository.findAll();
		for (TpexConfigEntity config : allConfig) {
			configMap.put(config.getName(), config.getValue());
		}
	}

	public String getConfigValue(String key){
		return configMap.get(key);
	}
	
	
	public Workbook getWorkbook(MultipartFile file) throws IOException {
		Workbook workbook = null;
		String excelFilePath = file.getOriginalFilename();
		if (excelFilePath != null && excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else if (excelFilePath != null && excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		} else {
			throw new InvalidFileException(ConstantUtil.ERR_CM_3021);
		}

		
		return workbook;
	}

	public boolean checkForValidHeader(Sheet worksheet, List<String> header, int h) {
		boolean isValidHeader = true;
		// Header row validation
		Row headerRow = worksheet.getRow(h);

		// Check for valid headers
		for (int i = 0; i < header.size(); i++) {
			if (headerRow.getCell(i) == null || headerRow.getCell(i).getCellType() == CellType.BLANK
					|| !(headerRow.getCell(i).getStringCellValue().equalsIgnoreCase(header.get(i)))) {
				isValidHeader = false;
				break;
			}
		}
		return isValidHeader;
	}

	public boolean checkForEmptySheet(Sheet worksheet, int rowsToSkip) {
		// iterating over excel file
		Iterator<Row> itr = worksheet.iterator();

		// Skip header rows
		for (int i = 0; i < rowsToSkip; i++) {
			itr.next();
		}

		boolean isEmptySheet = true;
		while (itr.hasNext()) {
			Row row = itr.next();
			Iterator<Cell> cells = row.cellIterator();
			while (cells.hasNext()) {
				Cell cell = cells.next();
				if (cell != null && cell.getCellType() != CellType.BLANK) {
					isEmptySheet = false;
					break;
				}
			}
		}
		return isEmptySheet;
	}

}
