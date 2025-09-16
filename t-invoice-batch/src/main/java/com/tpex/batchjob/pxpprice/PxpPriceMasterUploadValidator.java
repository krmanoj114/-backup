package com.tpex.batchjob.pxpprice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
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

import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.entity.PartMasterEntity;
import com.tpex.entity.PartPriceMasterEntity;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.NoemPackSpecRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.repository.PartPriceMasterRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.BatchUtil;
import com.tpex.util.ConstantUtils;

/**
 * The Class PxpPriceMasterUploadValidator.
 */
@Component
public class PxpPriceMasterUploadValidator implements Tasklet {

	private static final String CURRENCY = "currency";
	private static final String CFC_CODE = "cfcCode";
	private static final String IMP_CODE = "impCode";
	private static final String PART_NO = "partNo";
	private static final String ERROR_LIST = "errorList";
	private static final String WARNING_LIST = "warningList";
	
	/** The batch util. */
	@Autowired
	private BatchUtil batchUtil;
	
	/** The car family mast repository. */
	@Autowired
	private CarFamilyMastRepository carFamilyMastRepository;
	
	/** The oem fnl dst mst repository. */
	@Autowired
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	/** The oem currency mst repository. */
	@Autowired
	private OemCurrencyMstRepository oemCurrencyMstRepository;
	
	/** The part master respository. */
	@Autowired
	private PartMasterRespository partMasterRespository;
	
	/** The part price master repository. */
	@Autowired
	private PartPriceMasterRepository partPriceMasterRepository;
	
	/** The oem process ctrl repository. */
	@Autowired
	private OemProcessCtrlRepository oemProcessCtrlRepository;
	
	/** The noem pack spec repository. */
	@Autowired
	private NoemPackSpecRepository noemPackSpecRepository;
	
	/** The tpex config repository. */
	@Autowired
	private TpexConfigRepository tpexConfigRepository;
	
	/** The csv list. */
	List<Map<String, Object>> csvList;
	
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
			
			if (!(worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1)) {
				csvList = new ArrayList<>();
				Map<String, Object> validateCsvDataResult = validateCsvData(workbook, fileName, jobParameters.getString("effectiveFromDate"), jobParameters.getString("effectiveToDate"));
				if ((boolean) validateCsvDataResult.get("isError")) {
					stepExecution.get().setExitStatus(new ExitStatus("FAILED"));
					updateProcessCtrlEntity(jobParameters);
				} else if ((boolean) validateCsvDataResult.get("isWarning")) {
					stepExecution.get().setExitStatus(new ExitStatus("PROCESS"));
					stepExecution.get().getJobExecution().getExecutionContext().put("successWithWarning", "Y");
					updateProcessCtrlEntity(jobParameters);
				} else {
					stepExecution.get().setExitStatus(new ExitStatus("PROCESS"));
				}
			}
			workbook.close();
		}
		
		csvList.clear();
		
		return null;
	}
	
	private void updateProcessCtrlEntity(JobParameters jobParameters) {
		Optional<OemProcessCtrlEntity> oemProcessCtrlEntityOptional = oemProcessCtrlRepository.findById(new OemProcessCtrlIdEntity(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID), Integer.parseInt(jobParameters.getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID))));
		if (oemProcessCtrlEntityOptional.isPresent()) {
			OemProcessCtrlEntity oemProcessCtrlEntity = oemProcessCtrlEntityOptional.get();
			oemProcessCtrlEntity.setErrorFilePath(tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue() + "/" + jobParameters.getString("fileName"));
			oemProcessCtrlRepository.save(oemProcessCtrlEntity);
		}
	}
	
	/**
	 * Validate csv data.
	 *
	 * @param workbook the workbook
	 * @param fileName the file name
	 * @param effectiveFromDate the effective from date
	 * @param effectiveToDate the effective to date
	 * @return true, if successful
	 * @throws ParseException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> validateCsvData(Workbook workbook, String fileName, String effectiveFromDate, String effectiveToDate) throws ParseException, IOException {
		Sheet worksheet = workbook.getSheetAt(0);
		Iterator<Row> itr = worksheet.iterator(); //iterating over excel file  
		itr.next();
		
		boolean isError = false;
		boolean isWarning = false;
		while (itr.hasNext()) {
			Row row = itr.next();
			Map<String, Object> validateCsvRowResult = validateCsvRow(row, effectiveFromDate, effectiveToDate);
			List<String> errorList = (List<String>) validateCsvRowResult.get(ERROR_LIST);
			List<String> warningList = (List<String>) validateCsvRowResult.get(WARNING_LIST);
			if (!isError && !errorList.isEmpty()) {
				isError = true;
			}
			
			if (!isWarning && !warningList.isEmpty()) {
				isWarning = true;
			}
			row.createCell(6).setCellValue(errorList.isEmpty() ? null : String.join(",", errorList));
			row.createCell(7).setCellValue(warningList.isEmpty() ? null : String.join(",", warningList));
		}
		
		isWarning = getAndAddMissingPartNo(worksheet, isWarning, effectiveFromDate, effectiveToDate);
		
		if(isError || isWarning) {
			generateFile(workbook, worksheet, fileName);
		}
		Map<String, Object> result = new HashMap<>();
		result.put("isError", isError);
		result.put("isWarning", isWarning);
		return result;
	}
	
	private void generateFile(Workbook workbook, Sheet worksheet, String fileName) throws IOException {
		Row row = worksheet.getRow(0);
		Cell errorCell = row.createCell(6);
		errorCell.setCellValue("Error Reason");
		errorCell.setCellStyle(row.getCell(3).getCellStyle());
		Cell warningCell = row.createCell(7);
		warningCell.setCellValue("Warning Reason");
		warningCell.setCellStyle(row.getCell(3).getCellStyle());
		String directory = tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue();
		StringBuilder filePath = new StringBuilder(directory);
		if (!directory.equals("classpath:")) {
			filePath.append("/");
		}
		filePath.append(fileName);
		try (FileOutputStream outputStream = new FileOutputStream(filePath.toString())) {
            workbook.write(outputStream);
        } catch (IOException e) {
			workbook.close();
		}
	}

	/**
	 * Validate csv row.
	 *
	 * @param row the row
	 * @param effectiveToDate 
	 * @param effectiveFromDate 
	 * @return the map
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> validateCsvRow(Row row, String effectiveFromDate, String effectiveToDate) throws ParseException {
		String currency = batchUtil.getStringCellValue(row.getCell(0));
		String cfcCode = batchUtil.getStringCellValue(row.getCell(1));
		String impCode = batchUtil.getStringCellValue(row.getCell(2));
		String partNo = batchUtil.getStringCellValue(row.getCell(3));
		String partName = batchUtil.getStringCellValue(row.getCell(4));
		
		List<String> errorList = new ArrayList<>();
		List<String> warningList = new ArrayList<>();
		if (currency.isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1041);
		} else if (oemCurrencyMstRepository.countByCrmCd(currency) == 0) {
			errorList.add(ConstantUtils.ERR_IN_1042);
		}
		if (!currency.isBlank() && !cfcCode.isBlank() && !impCode.isBlank() && csvList.stream().anyMatch(m -> m.get(CFC_CODE).equals(cfcCode) && m.get(IMP_CODE).equals(impCode) && !m.get(CURRENCY).toString().isBlank() && !m.get(CURRENCY).equals(currency))) {
			errorList.add(String.format(ConstantUtils.ERR_IN_1043, impCode + ", " + cfcCode));
		}
		
		if (!currency.isBlank() && !cfcCode.isBlank() && !impCode.isBlank() && !partNo.isBlank() && csvList.stream().anyMatch(m -> m.get(CFC_CODE).equals(cfcCode) && m.get(IMP_CODE).equals(impCode) && m.get(CURRENCY).equals(currency) && m.get(PART_NO).equals(partNo))) {
			warningList.add(ConstantUtils.ERR_IN_1134);
		}
		
		Map<String, Object> csvMap = new HashMap<>();
		csvMap.put(CURRENCY, currency);
		csvMap.put(CFC_CODE, cfcCode);
		csvMap.put(IMP_CODE, impCode);
		csvMap.put(PART_NO, partNo);
		csvMap.put("partName", partName);
		csvList.add(csvMap);
		
		if (cfcCode.isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1045);
		} else if (carFamilyMastRepository.countByCarFmlyCode(cfcCode) == 0) {
			errorList.add(ConstantUtils.ERR_IN_1130);
		}
		
		if (impCode.isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1047);
		} else if (oemFnlDstMstRepository.countByFdDstCd(impCode) == 0) {
			errorList.add(ConstantUtils.ERR_IN_1048);
		}
		
		if (partNo.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1049);
		
		if (partName.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1051);
		
		if (partName.length() > 40)
			errorList.add(ConstantUtils.ERR_IN_1131);
		
		Map<String, Object> validateTwelveDigitPartNoResult = validateTwelveDigitPartNo(currency, cfcCode, impCode, partNo, partName, effectiveFromDate, effectiveToDate);
		errorList.addAll((List<String>) validateTwelveDigitPartNoResult.get(ERROR_LIST));
		warningList.addAll((List<String>) validateTwelveDigitPartNoResult.get(WARNING_LIST));

		errorList.addAll(validatePartPrice(row.getCell(5)));
		
		Map<String, Object> result = new HashMap<>();
		result.put(ERROR_LIST, errorList);
		result.put(WARNING_LIST, warningList);
		return result;
	}
	
	private Map<String, Object> validateTwelveDigitPartNo(String currency, String cfcCode, String impCode,
			String partNo, String partName, String effectiveFromDate, String effectiveToDate) throws ParseException {
		List<String> errorList = new ArrayList<>();
		List<String> warningList = new ArrayList<>();
		List<String> finalPartNoList = getTwelveDigitPartNoList(cfcCode, impCode, partNo, effectiveFromDate, effectiveToDate, warningList);
		Map<String, Object> result = new HashMap<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		
		for (String finalPartNo : finalPartNoList) {
			if (partMasterRespository.countByPartNo(finalPartNo) == 0 && !errorList.contains(ConstantUtils.ERR_IN_1050))
				errorList.add(ConstantUtils.ERR_IN_1050);
				
			Optional<PartMasterEntity> partMasterEntityOptional = partMasterRespository.findById(finalPartNo);
			if (partMasterEntityOptional.isPresent() && !(partMasterEntityOptional.get().getPartName().equalsIgnoreCase(partName))) {
				warningList.add(String.format(ConstantUtils.ERR_IN_1052, partMasterEntityOptional.get().getPartName()));
			}
			
			if (partPriceMasterRepository.countByCurrencyCodeAndIdCfCodeAndIdDestCodeAndIdPartNoAndIdEffFromMonthAndEffToMonth(
					currency, cfcCode, impCode, finalPartNo, effectiveFromDate, effectiveToDate) == 0) {
				PartPriceMasterEntity partPriceMaster = partPriceMasterRepository.findMaxControlRecordByCurrencyAndCfcAndImpAndPartNo(
						currency, cfcCode, impCode, finalPartNo);
				if (partPriceMaster != null && (formatter.parse(effectiveFromDate.replace("/", "")).before(formatter.parse(partPriceMaster.getId().getEffFromMonth().replace("/", "")))
						&& !warningList.contains(ConstantUtils.ERR_IN_1136))) {
					warningList.add(ConstantUtils.ERR_IN_1136);
				}
			}
		}
		
		result.put(ERROR_LIST, errorList);
		result.put(WARNING_LIST, warningList);
		return result;
	}

	/**
	 * Validate part price.
	 *
	 * @param cell the cell
	 * @return the list
	 */
	private List<String> validatePartPrice(Cell cell) {
		List<String> errorList = new ArrayList<>();
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			errorList.add(ConstantUtils.ERR_IN_1132);
		} else {
			try {
				double partPrice = batchUtil.getDoubleCellValue(cell);
				if (partPrice <= 0)
					errorList.add(ConstantUtils.ERR_IN_1054);
			} catch (NumberFormatException e) {
				errorList.add(ConstantUtils.ERR_IN_1133);
			}
		}
		return errorList;
	}
	
	/**
	 * Gets the and add missing part no.
	 *
	 * @param worksheet the worksheet
	 * @param isWarning the is warning
	 * @param effectiveFromDate the effective from date
	 * @param effectiveToDate the effective to date
	 * @return the and add missing part no
	 */
	@SuppressWarnings("unchecked")
	private boolean getAndAddMissingPartNo(Sheet worksheet, boolean isWarning, String effectiveFromDate,
			String effectiveToDate) {
		if (!csvList.isEmpty()) {
			Map<Object, List<String>> csvDataGroupedList = csvList.stream()
					.filter(m -> !m.get(CFC_CODE).toString().isBlank() && !m.get(IMP_CODE).toString().isBlank())
					.collect(Collectors.groupingBy(m -> Arrays.asList(m.get(CFC_CODE).toString(), m.get(IMP_CODE).toString()),
							Collectors.mapping(m -> m.get(PART_NO).toString(), Collectors.toList())));
			for (Entry<Object, List<String>> entry : csvDataGroupedList.entrySet()) {
				List<String> mapKey = (List<String>) entry.getKey();
				List<String> finalPartNoList = getPartNoList(mapKey.get(0), mapKey.get(1), entry.getValue(), effectiveFromDate, effectiveToDate);
				List<String> packSpecPartNoList = noemPackSpecRepository.findPartNoByNvpsCfCdAndNvpsModImpCd(mapKey.get(0), mapKey.get(1));
				List<String> missingPartNoList = packSpecPartNoList.stream().filter(e -> !finalPartNoList.contains(e)).distinct().collect(Collectors.toList());
				if (!missingPartNoList.isEmpty()) {
					if (!isWarning) {
						isWarning = true;
					}
					addMissingPartNo(worksheet, missingPartNoList, mapKey);
				}
			}
			
		}
		
		return isWarning;
	}
	
	/**
	 * Gets the part no list.
	 *
	 * @param cfcCode the cfc code
	 * @param impCode the imp code
	 * @param partNoList the part no list
	 * @param effectiveFromDate the effective from date
	 * @param effectiveToDate the effective to date
	 * @return the part no list
	 */
	private List<String> getPartNoList(String cfcCode, String impCode, List<String> partNoList, String effectiveFromDate,
			String effectiveToDate) {
		List<String> distinctPartNoList = partNoList.stream().filter(m -> !m.isBlank()).distinct().collect(Collectors.toList());
		List<String> finalPartNoList = new ArrayList<>();
		for (String partNo : distinctPartNoList) {
			finalPartNoList.addAll(getTwelveDigitPartNoList(cfcCode, impCode, partNo, effectiveFromDate, effectiveToDate, null));
		}
		return finalPartNoList;
	}
	
	private List<String> getTwelveDigitPartNoList(String cfcCode, String impCode, String partNo, String effectiveFromDate, String effectiveToDate, List<String> warningList) {
		List<String> finalPartNoList = new ArrayList<>();
		List<String> packingSpecPartNoList = noemPackSpecRepository.findPartNoList(cfcCode, impCode, partNo, effectiveFromDate, effectiveToDate);
		if (packingSpecPartNoList.isEmpty()) {
			finalPartNoList.add(partNo.concat("00"));
			if (warningList != null) {
				warningList.add(ConstantUtils.ERR_IN_1137);
			}
		} else {
			finalPartNoList.addAll(packingSpecPartNoList);
		}
		return finalPartNoList;
	}
	
	/**
	 * Adds the missing part no.
	 *
	 * @param worksheet the worksheet
	 * @param missingPartNoList the missing part no list
	 * @param mapKey the map key
	 */
	private void addMissingPartNo(Sheet worksheet, List<String> missingPartNoList, List<String> mapKey) {
		int lastRowIndex = worksheet.getLastRowNum();
		for (String missingPartNo : missingPartNoList) {
			StringBuilder warning = new StringBuilder(String.format(ConstantUtils.ERR_IN_1135, missingPartNo));
			Row row = worksheet.createRow(++lastRowIndex);
			row.createCell(1).setCellValue(mapKey.get(0));
			row.createCell(2).setCellValue(mapKey.get(1));
			row.createCell(3).setCellValue(missingPartNo);
			Optional<PartMasterEntity> partMasterEntityOptional = partMasterRespository.findById(missingPartNo);
			if (partMasterEntityOptional.isPresent()) {
				row.createCell(4).setCellValue(partMasterEntityOptional.get().getPartName());
			} else {
				warning.append(",").append(ConstantUtils.ERR_IN_1050);
			}
			row.createCell(7).setCellValue(warning.toString());
		}
	}
	
}
