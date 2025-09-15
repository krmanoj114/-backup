package com.tpex.batchjob.cuountrycodeoriginupload;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.tpex.dto.CountryCodeOriginBatchInputErrorDto;
import com.tpex.entity.CountryOriginMstEntity;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.repository.CountryMasterRepository;
import com.tpex.repository.CountryOriginMstRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.util.BatchUtil;
import com.tpex.util.ConstantUtils;

@Component
public class ValidateCountryCodeOriginUploadBatchFileTasklet  implements Tasklet{

	@Autowired
	CountryOriginMstRepository countryOriginMstRepository;

	@Autowired
	PartMasterRespository partMasterRespository;

	@Autowired
	CountryMasterRepository countryMasterRepository;

	@Autowired
	OemProcessCtrlRepository oemProcessCtrlRepository;

	@Autowired
	BatchUtil batchUtil;

	static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(ConstantUtils.DATE_FORMAT).withResolverStyle(ResolverStyle.STRICT);

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Optional<StepExecution> stepExecution = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getStepExecutions().stream().findFirst();

		if (stepExecution.isPresent()) {
			JobParameters jobParameters = stepExecution.get().getJobParameters();
			String filePath = batchUtil.getInputFile(jobParameters.getString("batchName"), jobParameters.getString("fileName"));
			File inputFile = ResourceUtils.getFile(filePath);
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(inputFile));
			XSSFSheet worksheet = workbook.getSheetAt(0);

			if (!(worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1)) {
				Map<String, Object> result = validateCsvData(worksheet);
				if ((boolean) result.get("isError")) {
					batchUtil.generateUploadErrorFile((List<CountryCodeOriginBatchInputErrorDto>) result.get("errorDtoList"), jobParameters.getString("fileName"), "country.of.origin.error");
					stepExecution.get().setExitStatus(new ExitStatus("FAILED"));
					Optional<OemProcessCtrlEntity> oemProcessCtrlEntityOptional = oemProcessCtrlRepository.findById(new OemProcessCtrlIdEntity(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID), Integer.parseInt(jobParameters.getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID))));
					if (oemProcessCtrlEntityOptional.isPresent()) {
						OemProcessCtrlEntity oemProcessCtrlEntity = oemProcessCtrlEntityOptional.get();
						oemProcessCtrlEntity.setErrorFilePath(filePath);
						oemProcessCtrlRepository.save(oemProcessCtrlEntity);
					}
				} else {
					stepExecution.get().setExitStatus(new ExitStatus("PROCESS"));
				}
			}
			workbook.close();
		}

		return null;
	}

	private Map<String, Object> validateCsvData(XSSFSheet worksheet) {
		Iterator<Row> itr = worksheet.iterator(); //iterating over excel file  
		itr.next();
		itr.next();

		boolean isError = false;
		List<CountryCodeOriginBatchInputErrorDto> countryCodeOriginBatchInputErrorDtoList = new ArrayList<>();
		while (itr.hasNext()) {
			Row row = itr.next();
			List<String> errorList = validateCsvRow(row);
			if (!isError && !errorList.isEmpty()) {
				isError = true;
			}

			java.util.Date  vanDateFromUtil =  batchUtil.getDateCellValue(row.getCell(3));
			java.util.Date vanDateToUtil = batchUtil.getDateCellValue(row.getCell(4));

			LocalDate date1 = null;
			LocalDate date2= null;

			if(vanDateFromUtil!=null) {
				java.sql.Date  vanDateFrom= new java.sql.Date(vanDateFromUtil.getTime());
				date1 = vanDateFrom.toLocalDate();
			}
			if(vanDateToUtil!=null) {
				java.sql.Date  vanDateTo= new java.sql.Date(vanDateToUtil.getTime());
				date2= vanDateTo.toLocalDate();
			}

			CountryCodeOriginBatchInputErrorDto countryCodeOriginBatchInputErrorDto = new CountryCodeOriginBatchInputErrorDto(
					batchUtil.getStringCellValue(row.getCell(0)), batchUtil.getStringCellValue(row.getCell(1)), 
					batchUtil.getStringCellValue(row.getCell(2)), date1, date2,
					errorList.isEmpty() ? null : String.join(",", errorList));				
			countryCodeOriginBatchInputErrorDtoList.add(countryCodeOriginBatchInputErrorDto);	
		}
		Map<String, Object> result = new HashMap<>();
		result.put("errorDtoList", countryCodeOriginBatchInputErrorDtoList);
		result.put("isError", isError);
		return result;	}


	private List<String> validateCsvRow(Row row) {


		String partNo = getStringCellValue(row.getCell(0));
		String countryOriginCode = batchUtil.getStringCellValue(row.getCell(2));

		DataFormatter dataFormatter = new DataFormatter(); 

		String vanDateFromString = dataFormatter.formatCellValue(row.getCell(3));
		String vanDateToString = dataFormatter.formatCellValue(row.getCell(4));

		java.sql.Date  vanDateFrom= null;
		java.sql.Date  vanDateTo= null;

		List<String> errorList = new ArrayList<>();

		errorList.addAll(validatePartNoAndCountryCode(partNo, countryOriginCode));

		if(vanDateFromString.isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1084);
		}
		else {
			java.util.Date vanDateFromUtil =  batchUtil.getDateCellValue(row.getCell(3));
			vanDateFrom= new java.sql.Date(vanDateFromUtil.getTime());

			Boolean vanDateFromError = isValidLocalDate(vanDateFromString, DATE_TIME_FORMATTER);
			if (vanDateFromError.equals(Boolean.FALSE))
				errorList.add(ConstantUtils.ERR_IN_1088);				
		}

		if(vanDateToString.isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1085);
		}	
		else {

			java.util.Date vanDateToUtil = batchUtil.getDateCellValue(row.getCell(4));
			vanDateTo= new java.sql.Date(vanDateToUtil.getTime());

			Boolean vanDateToError = isValidLocalDate(vanDateToString, DATE_TIME_FORMATTER);
			if (vanDateToError.equals(Boolean.FALSE))
				errorList.add(ConstantUtils.ERR_IN_1089);			
		}

		errorList.addAll(validateVanDate(vanDateFrom, vanDateTo));

		List<CountryOriginMstEntity> countryOriginMstEntityList = countryOriginMstRepository.findByPartNumberAndEffectiveFromdateAndEffectivetodate(partNo,vanDateFrom,vanDateTo);

		if(!countryOriginMstEntityList.isEmpty()) {
			errorList.add(ConstantUtils.ERR_IN_1071);
		}

		//Check. If Van Date From and Van Date To overlapping is not allowed under same Part No.
		if(!partNo.isBlank() && vanDateFrom!=null && vanDateTo!=null ) {
			String errorMsg = checkForOverlappingDates(partNo, vanDateFrom, vanDateTo);
			if(checkForOverlappingDates(partNo, vanDateFrom, vanDateTo) != null) {
				errorList.add(errorMsg);
			}
		}
		return errorList;
	}

	private List<String> validatePartNoAndCountryCode(String partNo, String countryOriginCode) {
		List<String> errorList = new ArrayList<>();
		if(partNo.isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1080);			
		}

		if(partMasterRespository.countByPartNo(partNo) == 0){
			errorList.add(ConstantUtils.ERR_IN_1081);
		}

		if(countryOriginCode.isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1082);
		}

		if (countryMasterRepository.countByCountryCode(countryOriginCode.split("-")[0]) == 0)
		{
			errorList.add(ConstantUtils.ERR_IN_1083);
		}

		return errorList;
	}

	private List<String> validateVanDate(Date vanDateFrom, Date vanDateTo) {
		List<String> errorList = new ArrayList<>();
		Date currentDate = Date.valueOf(LocalDate.now());

		if(vanDateFrom!= null && vanDateFrom.compareTo(currentDate) < 0){
			errorList.add(ConstantUtils.ERR_IN_1086);
		}

		if(vanDateTo!= null && vanDateTo.compareTo(currentDate) < 0){
			errorList.add(ConstantUtils.ERR_IN_1087);
		}


		if(vanDateTo!= null && vanDateFrom!= null && vanDateFrom.compareTo(vanDateTo) > 0){
			errorList.add(ConstantUtils.ERR_IN_1072);
		}

		return errorList;
	}

	private String checkForOverlappingDates(String partNo, Date vanDateFrom, Date vanDateTo) {
		List<CountryOriginMstEntity> countryOriginMstEntityListt = countryOriginMstRepository.findByPartNumber(partNo);

		if(!countryOriginMstEntityListt.isEmpty()) {

			CountryOriginMstEntity countryOriginMstEntity = countryOriginMstEntityListt.get(0);

			LocalDate vandateFrom1 = countryOriginMstEntity.getEffectiveFromdate();
			LocalDate vandateTo1 = countryOriginMstEntity.getEffectivetodate();

			LocalDate vandateFrom2 = convertToLocalDateViaSqlDate(vanDateFrom);
			LocalDate vandateTo2 = convertToLocalDateViaSqlDate(vanDateTo);

			if(vandateFrom1.isBefore(vandateFrom2) && vandateTo1.isAfter(vandateFrom2) ||
					vandateFrom1.isBefore(vandateTo2) && vandateTo1.isAfter(vandateTo2) ||
					vandateFrom1.isBefore(vandateFrom2) && vandateTo1.isAfter(vandateTo2) ||
					vandateFrom1.isAfter(vandateFrom2) && vandateTo1.isBefore(vandateTo2) ) {

				return ConstantUtils.ERR_IN_1074;
			}
		}		

		return null;
	}

	public static boolean isValidLocalDate(String dateStr, DateTimeFormatter dateFormatter) {

		try {
			LocalDate.parse(dateStr, dateFormatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	public LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
		return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
	}

	public String getStringCellValue (Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return "";
		}
		if (cell.getCellType().equals(CellType.STRING)) {
			return cell.getStringCellValue().trim();
		} else {

			return new BigDecimal(String.valueOf(cell.getNumericCellValue())).toPlainString();
		}
	}
}



