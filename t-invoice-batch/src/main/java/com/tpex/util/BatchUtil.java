package com.tpex.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.dto.BatchJobDTO;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.TpexConfigRepository;

@Component
public class BatchUtil {

	@Autowired
	private TpexConfigurationUtil tpexConfigurationUtil;
	
	@Autowired
	private TpexConfigRepository  tpexConfigRepository;
	
	@Autowired
	private JasperReportService jasperReportService;

	public String getStringCellValue(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return "";
		}
		if (cell.getCellType().equals(CellType.STRING)) {
			return cell.getStringCellValue().trim();
		} else {
			
			return String.valueOf((int) cell.getNumericCellValue()).trim();
		}
	}

	public Date getDateCellValue(Cell cell) {

		if (DateUtil.isCellDateFormatted(cell)){
			return cell.getDateCellValue();
		}else
		{
			return null;
		}
	}

	public String getInputFile(String batchName, String fileName) throws IOException {
		String filePath = tpexConfigurationUtil.getFilePath(batchName);
		File file = ResourceUtils.getFile(filePath);
		if (!file.exists())
			throw new FileNotFoundException("File Not exist in path = " + filePath);

		ObjectMapper objMapper = new ObjectMapper();
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		BatchJobDTO batchJobDTO = objMapper.readValue(file, new TypeReference<BatchJobDTO>() {});
		return batchJobDTO.getBatchJobInputFilePath() + fileName;
	}
	
	public void generateUploadErrorFile(
			List<?> errorDtoList, String fileName, String fileTemplateConfig) {
		//Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setAutoFitPageHeight", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", "xlsx");
		config.put("storeInDB", "true");
		config.put("loginUserId", ConstantUtils.TEST_USER);
				
		String fileTemplateName = tpexConfigRepository.findByName(fileTemplateConfig).getValue();
		
		StringBuilder filePath = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(fileName);
        
		RddDownLocDtlEntity savedRddDownLocDtlEntity = jasperReportService.saveOfflineDownloadDetail(fileTemplateName, config, filePath);
		int reportId = savedRddDownLocDtlEntity != null ? savedRddDownLocDtlEntity.getReportId() :0;
    	
    	//If file size is greater than configured size then store file in directory and return path
    	jasperReportService.getJasperReportDownloadOffline(errorDtoList, "xlsx", fileTemplateConfig, tpexConfigurationUtil.getReportDynamicPrameters(), config, reportId, filePath);
	}
	
	public Workbook getWorkbook(String fileName, File inputFile) throws IOException {
		Workbook workbook = null;
		if (fileName != null && fileName.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(new FileInputStream(inputFile));
		} else if (fileName != null && fileName.endsWith("xls")) {
			workbook = new HSSFWorkbook(new FileInputStream(inputFile));
		}
		return workbook;
    }
	
	public BatchJobDTO initProcessBatch(String batchName) throws IOException {
		String filePath = tpexConfigurationUtil.getFilePath(batchName);
		File file = ResourceUtils.getFile(filePath);
		if (!file.exists()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("filePath", filePath);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018, errorMessageParams);
		}
		ObjectMapper objMapper = new ObjectMapper();
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objMapper.readValue(file, new TypeReference<BatchJobDTO>() {});
	}
	
	public double getDoubleCellValue(Cell cell) {
	    if (cell.getCellType().equals(CellType.NUMERIC)) {
	    	return cell.getNumericCellValue();
	    } else {
	    	return Double.valueOf(cell.getStringCellValue());
	    }
    }

}
