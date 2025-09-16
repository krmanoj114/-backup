package com.tpex.admin.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.admin.dto.ColumnsDTO;
import com.tpex.admin.entity.TpexConfigEntity;
import com.tpex.admin.exception.MyResourceNotFoundException;
import com.tpex.admin.repository.TpexConfigRepository;

import net.sf.jasperreports.engine.JRParameter;
@Component
public class TpexConfigurationUtil {
	
	@Autowired
	TpexConfigRepository tpexConfigRepository;

	public String getFilePath(String filename)  {
		String filePath = "";
		TpexConfigEntity tpexConfigEntity = tpexConfigRepository.findByName(filename);
		filePath = tpexConfigEntity.getValue();
		if(StringUtils.isBlank(filePath)) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("filename", filename);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3017, errorMessageParams); 
		}
		return filePath;
	}
	
	public ColumnsDTO readDataFromJson(String filename) throws IOException{
		ObjectMapper objectMapper = new ObjectMapper();
		File file = null;
		String filePath = getFilePath(filename);
		file = ResourceUtils.getFile(filePath);
		if(!file.exists()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("filePath", filePath);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018, errorMessageParams);
		}
		return objectMapper.readValue(file, ColumnsDTO.class);
	}
	
	public Map<String, Object> getReportDynamicPrameters() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put(ConstantUtils.HEADINGFONTCOLOR,
				tpexConfigRepository.findByName(ConstantUtils.JASPER_REPORT_HEADINGFONTCOLOR).getValue());
		parameters.put(ConstantUtils.DETAILFONTCOLOR,
				tpexConfigRepository.findByName(ConstantUtils.JASPER_REPORT_DETAILFONTCOLOR).getValue());
		parameters.put(ConstantUtils.HEADINGBGCOLOR,
				tpexConfigRepository.findByName(ConstantUtils.JASPER_REPORT_HEADINGBGCOLOR).getValue());
		parameters.put(ConstantUtils.DETAILBGCOLOR,
				tpexConfigRepository.findByName(ConstantUtils.JASPER_REPORT_DETAILBGCOLOR).getValue());
		parameters.put(ConstantUtils.DETAILVALIGN,
				tpexConfigRepository.findByName(ConstantUtils.JASPER_REPORT_DETAILVALIGN).getValue());
		return parameters;
	}
	
	public Map<String, Object> getReportConfig(String loginUserId, boolean isdownloadTemplate) {
		Map<String, Object> config = new HashMap<>();

		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.SET_WHITE_PAGE_BACKGROUND, false);
		config.put(ConstantUtils.SET_DETECT_CELL_TYPE, true);
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_DIRECTORY,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_DIRECTORY).getValue());
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT).getValue());
		config.put(ConstantUtils.NATIONAL_CALENDAR_REPORT_SIZE_LIMIT,
				tpexConfigRepository.findByName(ConstantUtils.NATIONAL_CALENDAR_REPORT_SIZE_LIMIT).getValue());
		config.put(ConstantUtils.LOGIN_USER_ID, loginUserId);

		if (isdownloadTemplate)
			config.put(ConstantUtils.INVOICE_GENERATION_STORE_DB, ConstantUtils.FALSE);
		else
			config.put(ConstantUtils.INVOICE_GENERATION_STORE_DB, "true");
		
		return config;
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

	public boolean checkForValidHeader(Sheet worksheet, List<String> header) {
		boolean isValidHeader = true;
		// Header row validation
		Row headerRow = worksheet.getRow(0);

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
	
	
	public boolean checkForValidFirstRow(Sheet worksheet) {
		boolean isValidFirstRow = false;
		Row dataFirstRow = worksheet.getRow(1);
		
		for(Cell cell : dataFirstRow) {
			if(cell.getCellType() == CellType.BLANK) {
				isValidFirstRow = true;
			}
		}
	
		return isValidFirstRow;		  		
	}
	
}
