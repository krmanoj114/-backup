package com.tpex.batchjob.lotpartpriceupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.tpex.dto.LotPartPriceUploadBatchErrorDto;
import com.tpex.entity.OemLotSizeMstEntity;
import com.tpex.entity.OemLotSizeMstIDEntity;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.LotSizeMasterRepository;
import com.tpex.repository.NoemPackSpecRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.BatchUtil;
import com.tpex.util.ConstantUtils;
import com.tpex.util.TpexConfigurationUtil;

/**
 * The Class ValidateLotPartPriceUploadBatchFileTasklet.
 */
@Component
public class ValidateLotPartPriceUploadBatchFileTasklet implements Tasklet {

	/** The Constant CURRENCY_CODE. */
	private static final String CURRENCY_CODE = "currencyCode";
	
	/** The Constant LOT. */
	private static final String LOT = "lot";
	
	/** The Constant CFC_CODE. */
	private static final String CFC_CODE = "cfcCode";
	
	/** The Constant IMP_CODE. */
	private static final String IMP_CODE = "impCode";
	
	/** The Constant PART_NO. */
	private static final String PART_NO = "partNo";
	
	/** The Constant PART_NAME. */
	private static final String PART_NAME = "partName";
	
	/** The Constant USAGE. */
	private static final String USAGE = "usage";
	
	/** The tpex configuration util. */
	@Autowired
	private TpexConfigurationUtil tpexConfigurationUtil;
	
	/** The car family mast repository. */
	@Autowired
	private CarFamilyMastRepository carFamilyMastRepository;
	
	/** The oem fnl dst mst repository. */
	@Autowired
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	/** The oem currency mst repository. */
	@Autowired
	private OemCurrencyMstRepository oemCurrencyMstRepository;
	
	/** The lot size master repository. */
	@Autowired
	private LotSizeMasterRepository lotSizeMasterRepository;
	
	/** The part master respository. */
	@Autowired
	private PartMasterRespository partMasterRespository;
	
	/** The tpex config repository. */
	@Autowired
	private TpexConfigRepository tpexConfigRepository;
	
	/** The jasper report service. */
	@Autowired
	private JasperReportService jasperReportService;
	
	/** The oem process ctrl repository. */
	@Autowired
	private OemProcessCtrlRepository oemProcessCtrlRepository;
	
	/** The noem pack spec repository. */
	@Autowired
	private NoemPackSpecRepository noemPackSpecRepository;
	
	/** The batch util. */
	@Autowired
	private BatchUtil batchUtil;
	
	/**
	 * Execute.
	 *
	 * @param contribution the contribution
	 * @param chunkContext the chunk context
	 * @return the repeat status
	 * @throws Exception the exception
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Optional<StepExecution> stepExecution = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getStepExecutions().stream().findFirst();
		
		if (stepExecution.isPresent()) {
			JobParameters jobParameters = stepExecution.get().getJobParameters();
			String fileName = jobParameters.getString("fileName");
			String filePath = batchUtil.getInputFile(jobParameters.getString("batchName"), fileName);
			File inputFile = ResourceUtils.getFile(filePath);
			Workbook workbook = batchUtil.getWorkbook(fileName, inputFile);
			if (workbook == null) {
				stepExecution.get().setExitStatus(new ExitStatus("FAILED"));
				return null;
			}
			Sheet worksheet = workbook.getSheetAt(0);
				
				if (worksheet.getPhysicalNumberOfRows() > 1) {
					
					validateCsvDataAndUpdateStatus(stepExecution.get(), workbook, worksheet, jobParameters, fileName);
				}
				
				workbook.close();
		}

		return RepeatStatus.FINISHED;
	}
	
	private void validateCsvDataAndUpdateStatus(StepExecution stepExecution, Workbook workbook, Sheet worksheet, JobParameters jobParameters, String fileName) throws IOException {
		List<Map<String, Object>> csvDataList = fetchCsvDataList(worksheet);
		
		if (!validateCsvData(worksheet, csvDataList, jobParameters.getString("effectiveDate"))) {
			stepExecution.setExitStatus(new ExitStatus("PROCESS"));
		} else {
			StringBuilder outputFilePath = new StringBuilder().append(tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue()).append("/").append(fileName);
			try (FileOutputStream outputStream = new FileOutputStream(outputFilePath.toString())) {
	            workbook.write(outputStream);
	        } catch (IOException e) {
				workbook.close();
			}
			
			stepExecution.setExitStatus(new ExitStatus("FAILED"));
			Optional<OemProcessCtrlEntity> oemProcessCtrlEntityOptional = oemProcessCtrlRepository.findById(new OemProcessCtrlIdEntity(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID), Integer.parseInt(jobParameters.getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID))));
			if (oemProcessCtrlEntityOptional.isPresent()) {
				OemProcessCtrlEntity oemProcessCtrlEntity = oemProcessCtrlEntityOptional.get();
				oemProcessCtrlEntity.setErrorFilePath(outputFilePath.toString());
				oemProcessCtrlRepository.save(oemProcessCtrlEntity);
			}
		}
	}
	
	/**
	 * Fetch csv data list.
	 *
	 * @param itr the itr
	 * @return the list
	 */
	private List<Map<String, Object>> fetchCsvDataList(Sheet worksheet) {
		Iterator<Row> itr = worksheet.iterator(); //iterating over excel file  
		itr.next();
		List<Map<String, Object>> csvDataList = new ArrayList<>();
		while (itr.hasNext()) {  
			Row row = itr.next();
			Map<String, Object> csvDataRow = new HashMap<>();
			csvDataRow.put(CURRENCY_CODE, getStringCellValue(row.getCell(0)));
			csvDataRow.put(LOT, getStringCellValue(row.getCell(1)));
			csvDataRow.put(CFC_CODE, getStringCellValue(row.getCell(2)));
			csvDataRow.put(IMP_CODE, getStringCellValue(row.getCell(3)));
			csvDataRow.put(PART_NO, getStringCellValue(row.getCell(4)));
			csvDataRow.put(PART_NAME, getStringCellValue(row.getCell(5)));
			csvDataRow.put("firstOfPrice", getDoubleCellValue(row.getCell(6)));
			csvDataRow.put(USAGE, getDoubleCellValue(row.getCell(7)));
			csvDataList.add(csvDataRow);
		}
		return csvDataList;
	}
	
	/**
	 * Gets the double cell value.
	 *
	 * @param cell the cell
	 * @return the double cell value
	 */
	private double getDoubleCellValue(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return 0;
		}
	    if (cell.getCellType().equals(CellType.NUMERIC)) {
	    	return cell.getNumericCellValue();
	    } else {
	    	return Double.valueOf(cell.getStringCellValue());
	    }
    }
	
	/**
	 * Gets the string cell value.
	 *
	 * @param cell the cell
	 * @return the string cell value
	 */
	private String getStringCellValue(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return "";
		}
	    if (cell.getCellType().equals(CellType.STRING)) {
	    	return cell.getStringCellValue();
	    } else {
	    	return String.valueOf((int) cell.getNumericCellValue());
	    }
    }
	
	/**
	 * Validate csv data.
	 *
	 * @param csvDataList the csv data list
	 * @param fileName the file name
	 * @param effectiveDate the effective date
	 * @return true, if successful
	 */
	@SuppressWarnings("unchecked")
	private boolean validateCsvData(Sheet worksheet, List<Map<String, Object>> csvDataList, String effectiveDate) {
		Iterator<Row> itr = worksheet.iterator(); //iterating over excel file  
		itr.next();
		boolean isError = false;
		while (itr.hasNext()) {
			Row row = itr.next();
			
			Map<String, Object> result = validateCsvRow(row, effectiveDate, csvDataList);
			List<String> errorList = (List<String>) result.get("errorList");
			List<String> warningList = (List<String>) result.get("warningList");
			
			if (!isError && !errorList.isEmpty()) {
				isError = true;
			}
			
			row.createCell(8).setCellValue(errorList.isEmpty() ? null : String.join(",", errorList));
			row.createCell(9).setCellValue(warningList.isEmpty() ? null : String.join(",", warningList));
		}
		
		if (isError) {
			Row row = worksheet.getRow(0);
			Cell errorCell = row.createCell(8);
			errorCell.setCellValue("Error Reason");
			errorCell.setCellStyle(row.getCell(1).getCellStyle());
			
			Cell warningCell = row.createCell(9);
			warningCell.setCellValue("Warning Reason");
			warningCell.setCellStyle(row.getCell(1).getCellStyle());
		}
		
		return isError;
	}
	
	/**
	 * Validate csv row.
	 *
	 * @param csvData the csv data
	 * @param effectiveDate the effective date
	 * @param csvDataList the csv data list
	 * @return the map
	 */
	private Map<String, Object> validateCsvRow(Row row, String effectiveDate, List<Map<String, Object>> csvDataList) {
		String currencyCode = getStringCellValue(row.getCell(0));
		String cfcCode = getStringCellValue(row.getCell(2));
		String impCode = getStringCellValue(row.getCell(3));
		String lot = getStringCellValue(row.getCell(1));
		String partNo = getStringCellValue(row.getCell(4));
		String partName = getStringCellValue(row.getCell(5));
		double usage = 0;
		List<String> errorList = new ArrayList<>();
		List<String> warningList = new ArrayList<>();
		
		errorList.addAll(validateBlankFields(currencyCode, cfcCode, impCode, lot, partNo, partName, row));
		
		if (row.getCell(7).getCellType() == CellType.BLANK) {
			errorList.add(ConstantUtils.ERR_IN_1055);
		} else if (getDoubleCellValue(row.getCell(7)) <= 0) {
			errorList.add(ConstantUtils.ERR_IN_1056);
		} else {
			usage = getDoubleCellValue(row.getCell(7));
		}
		
		//Check, If Car family code does not exist in Car Family Master, log the error at each record level and continue file reading.
		if (carFamilyMastRepository.countByCarFmlyCode(cfcCode) == 0)
			errorList.add(ConstantUtils.ERR_IN_1046);
		
		//Check, If Importer code does not exist in Final Destination Master, log the error at each record level and continue file reading.
		if (oemFnlDstMstRepository.countByFdDstCd(impCode) == 0)
			errorList.add(ConstantUtils.ERR_IN_1048);
		
		//Check, If Currency code does not exist in Currency Master, log the error at each record level and continue file reading.
		if (oemCurrencyMstRepository.countByCrmCd(currencyCode) == 0)
			errorList.add(ConstantUtils.ERR_IN_1042);
		
		//Check, If multiple Currency code under same Importer code and Car family Code in uploaded excel file , log the error at record level continue file reading.
		if (csvDataList.stream().anyMatch(m -> m.get(CFC_CODE).equals(cfcCode) && m.get(IMP_CODE).equals(impCode) && !m.get(CURRENCY_CODE).equals(currencyCode))) {
			errorList.add(String.format(ConstantUtils.ERR_IN_1043, cfcCode + ", " + impCode));
		}
		
		//Check, If Part No. does not exist in Part Master, log the error at each record level and continue file reading.
		if (partMasterRespository.countByPartNo(partNo) == 0)
			errorList.add(ConstantUtils.ERR_IN_1050);
		
		//Check, If Part Name is not same in Part Master for respective Part No., log the warning at each record level and continue file reading.
		if (partMasterRespository.countByPartNoAndPartName(partNo, partName) == 0)
			warningList.add(String.format(ConstantUtils.ERR_IN_1052, partName));
		
		//Check, If Lot Code does not exist in Lot Size Master, log the error at each record level and continue file reading. 
		if (lotSizeMasterRepository.countByLotCode(lot) == 0)
			errorList.add(ConstantUtils.ERR_IN_1057);
		
		//(Key  Car Family Code , Importer Code , Lot Code) 
		//Check, if there are any duplicate row (Means all columns are same excluding Part name & Effective from To), log the error skip the duplicate record and continue file reading
		if (csvDataList.stream().filter(m -> m.get(CFC_CODE).equals(cfcCode) && m.get(IMP_CODE).equals(impCode) 
				&& m.get(CURRENCY_CODE).equals(currencyCode) && m.get(LOT).equals(lot) && m.get(PART_NO).equals(partNo)).count() > 1) {
			errorList.add(String.format(ConstantUtils.ERR_IN_1063, cfcCode + ", " + impCode + ", " + lot + ", " + partNo + ", " + effectiveDate));
		}
		
		String lotKeyError = validateLotPartKey(cfcCode, impCode, lot, partNo, effectiveDate, csvDataList);
		if (!lotKeyError.isBlank()) 
			errorList.add(lotKeyError);

		String usageError = calculateUsage(cfcCode, impCode, lot, partNo, usage, effectiveDate);
		if (!usageError.isBlank()) 
			errorList.add(usageError);
		
		Map<String, Object> result = new HashMap<>();
		result.put("errorList", errorList);
		result.put("warningList", warningList);
		return result;
	}
	
	private List<String> validateBlankFields(String currencyCode, String cfcCode, String impCode, String lot, String partNo, String partName, Row row) {
		List<String> errorList = new ArrayList<>();
		if (currencyCode.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1041);
		
		if (lot.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1044);
		
		if (cfcCode.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1045);
		
		if (impCode.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1047);
		
		if (partNo.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1049);
		
		if (partName.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1051);
		
		if (row.getCell(6).getCellType() == CellType.BLANK) {
			errorList.add(ConstantUtils.ERR_IN_1053);
		} else if (getDoubleCellValue(row.getCell(6)) <= 0) {
			errorList.add(ConstantUtils.ERR_IN_1054);
		}
		
		return errorList;
	}
	
	/**
	 * Validate lot part key.
	 *
	 * @param cfcCode the cfc code
	 * @param impCode the imp code
	 * @param lot the lot
	 * @param partNo the part no
	 * @param effectiveDate the effective date
	 * @param csvDataList the csv data list
	 * @return the string
	 */
	private String validateLotPartKey(String cfcCode, String impCode, String lot, String partNo, String effectiveDate, List<Map<String, Object>> csvDataList) {
		if (noemPackSpecRepository.getCountUsingLotKey(cfcCode, impCode, lot, effectiveDate) == 0)
			return ConstantUtils.ERR_IN_1058;
		
		if (noemPackSpecRepository.getCountUsingLotPartKey(cfcCode, impCode, lot, partNo, effectiveDate) == 0)
			return ConstantUtils.ERR_IN_1059;
		
		List<String> csvList = csvDataList.stream().filter(m -> m.get(CFC_CODE).equals(cfcCode) && m.get(IMP_CODE).equals(impCode) && m.get(LOT).equals(lot) && m.get(PART_NO) != null)
				.map(m -> m.get(PART_NO).toString()).distinct().collect(Collectors.toList());
		List<String> noemVprPkgSpecEntities = noemPackSpecRepository.findPartNoByNvpsCfCdAndNvpsLotCdAndNvpsModImpCd(cfcCode, impCode, lot);
		for (String noemVprPkgSpecEntity : noemVprPkgSpecEntities) {
			if (!csvList.contains(noemVprPkgSpecEntity))
				return String.format(ConstantUtils.ERR_IN_1060, noemVprPkgSpecEntity,  
						impCode + ", " + cfcCode + ", " + lot);
		}
		return "";
		
	}
	
	/**
	 * Calculate usage.
	 *
	 * @param cfcCode the cfc code
	 * @param impCode the imp code
	 * @param lot the lot
	 * @param partNo the part no
	 * @param usage the usage
	 * @param effectiveDate the effective date
	 * @return the string
	 */
	private String calculateUsage(String cfcCode, String impCode, String lot, String partNo, Double usage, String effectiveDate) {
		List<Integer> listOfQtyBox = noemPackSpecRepository.findPckSpecDetails(cfcCode, impCode, lot, partNo, effectiveDate);

		Integer sumOfQty = listOfQtyBox.stream().reduce(0, (a, b) -> a + b);
		
		Optional<OemLotSizeMstEntity> oemLotSizeMstEntityOptional = lotSizeMasterRepository.findById(
				new OemLotSizeMstIDEntity(impCode, cfcCode, lot, partNo));
		
		double lotSize = 0;
		if(oemLotSizeMstEntityOptional.isPresent())
			lotSize = oemLotSizeMstEntityOptional.get().getLotSizeCode();
		
		if (lotSize == 0) {
			OemLotSizeMstEntity oemLotSizeMstEntity = lotSizeMasterRepository.findTopByLotModImpAndCarFamilyCodeAndLotCode(
					impCode, cfcCode, lot);
			
			if (oemLotSizeMstEntity != null) {
				lotSize = oemLotSizeMstEntity.getLotSizeCode();
			} else {
				return String.format(ConstantUtils.ERR_IN_1062, lot + ", " + impCode);
			}
		}
			
		Double calValUsage = sumOfQty / lotSize;
		if (!calValUsage.equals(usage)) {
			return ConstantUtils.ERR_IN_1061;
		}

		return "";
	}

	/**
	 * Generate lot part price upload batch error file.
	 *
	 * @param lotPartPriceUploadBatchErrorDtoList the lot part price upload batch error dto list
	 * @param fileName the file name
	 */
	public void generateLotPartPriceUploadBatchErrorFile(List<LotPartPriceUploadBatchErrorDto> lotPartPriceUploadBatchErrorDtoList, String fileName) {
		//Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setAutoFitPageHeight", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", "xlsx");
		config.put("storeInDB", "true");
		config.put("loginUserId", ConstantUtils.TEST_USER);
				
		String fileTemplateName = tpexConfigRepository.findByName("lot.part.price.error").getValue();
		
		StringBuilder filePath = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(fileName);
        
		RddDownLocDtlEntity savedRddDownLocDtlEntity = jasperReportService.saveOfflineDownloadDetail(fileTemplateName, config, filePath);
		int reportId = savedRddDownLocDtlEntity != null ? savedRddDownLocDtlEntity.getReportId() :0;
    	
    	//If file size is greater than configured size then store file in directory and return path
    	jasperReportService.getJasperReportDownloadOffline(lotPartPriceUploadBatchErrorDtoList, "xlsx", "lot.part.price.error", tpexConfigurationUtil.getReportDynamicPrameters(), config, reportId, filePath);
	}
	
}
