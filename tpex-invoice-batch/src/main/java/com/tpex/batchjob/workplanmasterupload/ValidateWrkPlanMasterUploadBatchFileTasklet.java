package com.tpex.batchjob.workplanmasterupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.dto.BatchJobDTO;
import com.tpex.dto.WrkPlanMasterUploadBatchErrorDto;
import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.entity.InvGenWorkPlanMstIdEntity;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvGenWorkPlanMstRepository;
import com.tpex.repository.NoemCbMstRepository;
import com.tpex.repository.OemPortMstRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.TpexConfigurationUtil;

@Component
public class ValidateWrkPlanMasterUploadBatchFileTasklet implements Tasklet{

	private static final String DEST_NO = "destiNo";

	private static final String ISSUE_INVOICE_DATE = "issueInvoiceDate";

	private static final String ORIGINAL_ETD = "originalEtd";

	private static final String CONT_DESTINATION = "contDest";

	private static final String ETD1 = "etd1";

	private static final String ETA1 = "eta1";

	private static final String CONT_20 = "cont20";

	private static final String CONT_40 = "cont40";

	private static final String RENBAN_CODE = "renbanCode";

	private static final String SHIP_COMP = "shipComp";

	private static final String CUSTOM_BROKER = "customBroker";

	private static final String VESSEL1 = "vessel1";

	private static final String VOYAGE1 = "voyage1";

	private static final String ETD2 = "etd2";

	private static final String ETA2 = "eta2";

	private static final String VESSEL2 = "vessel2";

	private static final String VOYAGE2 = "voyage2";

	private static final String ETD3 = "etd3";

	private static final String ETA3 = "eta3";

	private static final String VESSEL3 = "vessel3";

	private static final String VOYAGE3 = "voyage3";

	private static final String BOOKING_NO = "bookingNo";

	private static final String FOLDER_NAME = "folderName";

	private static final String PORT_OF_LOADING = "portOfLoading";

	private static final String PORT_OF_DISCHARGE = "portOfDischarge";

	LocalDate currentDate = LocalDate.now();

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private TpexConfigurationUtil tpexConfigurationUtil;

	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	@Autowired
	private NoemCbMstRepository noemCbMstRepository;

	@Autowired
	private OemPortMstRepository oemPortMstRepository;

	@Autowired
	InvGenWorkPlanMstRepository invGenWorkPlanMstRepository;


	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		Collection<StepExecution> stepExecutions = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getStepExecutions();

		for (StepExecution stepExecution : stepExecutions) {
			JobParameters jobParameters = stepExecution.getJobParameters();
			if (jobParameters.getString("batchName") != null) {
				String filePath = tpexConfigurationUtil.getFilePath(jobParameters.getString("batchName"));
				File file = ResourceUtils.getFile(filePath);
				if (!file.exists())
					throw new FileNotFoundException("File Not exsit in path = " + filePath);

				ObjectMapper objMapper = new ObjectMapper();
				objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				BatchJobDTO batchJobDTO = objMapper.readValue(file, new TypeReference<BatchJobDTO>() {});
				File inputFile = ResourceUtils.getFile(batchJobDTO.getBatchJobInputFilePath() + jobParameters.getString("fileName"));
				XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(inputFile));
				XSSFSheet worksheet = workbook.getSheetAt(0);

				if (!(worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1)) {
					Iterator<Row> itr = worksheet.iterator(); //iterating over excel file  
					itr.next();
					List<Map<String, Object>> csvDataList = fetchCsvDataList(itr);

					Map<String, Object> result = validateCsvData(csvDataList, jobParameters.getString("fileName"));

					if (!(boolean) result.get(ConstantUtils.IS_ERROR_FLAG)) {
						stepExecution.setExitStatus(new ExitStatus("PROCESS"));
					}

					String warningFlag = getWarningFlag(result);
					stepExecution.getJobExecution().getExecutionContext().put("warningFlag",warningFlag);
				}

				workbook.close();
			}
		}

		return RepeatStatus.FINISHED;
	}

	private String getWarningFlag(Map<String, Object> result) {

		String warningFlag = "N";

		if ((boolean) result.get(ConstantUtils.IS_WARNING_FLAG) && !(boolean) result.get(ConstantUtils.IS_ERROR_FLAG)) {
			warningFlag = "Y";
		}
		return warningFlag;
	}


	private List<Map<String, Object>> fetchCsvDataList(Iterator<Row> itr) {
		List<Map<String, Object>> csvDataList = new ArrayList<>();
		while (itr.hasNext()) {  
			Row row = itr.next();
			Map<String, Object> csvDataRow = new HashMap<>();
			csvDataRow.put(DEST_NO, row.getCell(0).getStringCellValue());
			csvDataRow.put(ISSUE_INVOICE_DATE, getStringCellValue(row.getCell(1)));
			csvDataRow.put(ORIGINAL_ETD, getStringCellValue(row.getCell(2)));
			csvDataRow.put(CONT_DESTINATION, row.getCell(3).getStringCellValue());
			csvDataRow.put(ETD1, getStringCellValue(row.getCell(4)));
			csvDataRow.put(ETA1, getStringCellValue(row.getCell(5)));
			csvDataRow.put(CONT_20, getStringCellValue(row.getCell(6)));
			csvDataRow.put(CONT_40, getStringCellValue(row.getCell(7)));
			csvDataRow.put(RENBAN_CODE, row.getCell(8).getStringCellValue());
			csvDataRow.put(SHIP_COMP, row.getCell(9).getStringCellValue());
			csvDataRow.put(CUSTOM_BROKER, row.getCell(10).getStringCellValue());
			csvDataRow.put(VESSEL1, row.getCell(11).getStringCellValue());
			csvDataRow.put(VOYAGE1, row.getCell(12).getStringCellValue());
			csvDataRow.put(ETD2, getStringCellValue(row.getCell(13)));
			csvDataRow.put(ETA2, getStringCellValue(row.getCell(14)));
			csvDataRow.put(VESSEL2, row.getCell(15).getStringCellValue());
			csvDataRow.put(VOYAGE2, row.getCell(16).getStringCellValue());
			csvDataRow.put(ETD3, getStringCellValue(row.getCell(17)));
			csvDataRow.put(ETA3, getStringCellValue(row.getCell(18)));
			csvDataRow.put(VESSEL3, row.getCell(19).getStringCellValue());
			csvDataRow.put(VOYAGE3, row.getCell(20).getStringCellValue());
			csvDataRow.put(BOOKING_NO, row.getCell(21).getStringCellValue());
			csvDataRow.put(FOLDER_NAME, row.getCell(22).getStringCellValue());
			csvDataRow.put(PORT_OF_LOADING, row.getCell(23).getStringCellValue());
			csvDataRow.put(PORT_OF_DISCHARGE, row.getCell(24).getStringCellValue());

			csvDataList.add(csvDataRow);
		}

		return csvDataList;
	}

	public String getStringCellValue(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return "";
		}
		if (cell.getCellType().equals(CellType.STRING)) {
			return cell.getStringCellValue().trim();
		} else {
			return new BigDecimal(String.valueOf(cell.getNumericCellValue())).toPlainString();
		}
	}

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtils.DEFAULT_DATE_FORMATE);


	@SuppressWarnings("unchecked")
	private Map<String, Object> validateCsvData(List<Map<String, Object>> csvDataList, String fileName) {

		List<WrkPlanMasterUploadBatchErrorDto> wrkPlanMasterUploadBatchErrorDtoList = new ArrayList<>();
		boolean isError = false;
		boolean isWarning = false;

		for (Iterator<Map<String, Object>> iterator = csvDataList.iterator(); iterator.hasNext();) {
			Map<String, Object> csvData = iterator.next();

			String destiNo = csvData.get(DEST_NO).toString();

			String contDest = csvData.get(CONT_DESTINATION).toString();
			Integer cont20 = Integer.parseInt(csvData.get(CONT_20).toString());
			Integer cont40 = Integer.parseInt(csvData.get(CONT_40).toString());
			String renbanCode = csvData.get(RENBAN_CODE).toString();
			String shipComp = csvData.get(SHIP_COMP).toString();
			String customBroker = csvData.get(CUSTOM_BROKER).toString();
			String vessel1 = csvData.get(VESSEL1).toString();
			String voyage1 = csvData.get(VOYAGE1).toString();
			String vessel2 = csvData.get(VESSEL2).toString();
			String voyage2 = csvData.get(VOYAGE2).toString();
			String vessel3 = csvData.get(VESSEL3).toString();
			String voyage3 = csvData.get(VOYAGE3).toString();
			String bookingNo = csvData.get(BOOKING_NO).toString();
			String folderName = csvData.get(FOLDER_NAME).toString();
			String portOfLoading = csvData.get(PORT_OF_LOADING).toString();
			String portOfDischarge = csvData.get(PORT_OF_DISCHARGE).toString();

			LocalDate originalEtd = LocalDate.parse(csvData.get(ORIGINAL_ETD).toString(), formatter);
			LocalDate issueInvoiceDate = getIssueInvoiceDate(csvData);
			LocalDate etd1 = getEtd1Date(csvData);
			LocalDate eta1 = getEta1Date(csvData);
			LocalDate etd2 = getEtd2Date(csvData);
			LocalDate eta2 = getEta2Date(csvData);
			LocalDate etd3 = getEtd3Date(csvData);
			LocalDate eta3 = getEta3Date(csvData);

			Map<String, Object> result = validateCsvRow(csvData);
			List<String> errorList = (List<String>) result.get("errorList");
			List<String> warningList = (List<String>) result.get("warningList");

			if (!isError && !errorList.isEmpty()) {
				isError = true;
			}

			if (!warningList.isEmpty()) {
				isWarning = true;
			}

			if (!errorList.isEmpty() || !warningList.isEmpty()) {
				WrkPlanMasterUploadBatchErrorDto wrkPlanMasterUploadBatchErrorDto = new WrkPlanMasterUploadBatchErrorDto(destiNo,issueInvoiceDate,
						originalEtd,contDest,etd1,eta1,cont20,cont40,renbanCode,shipComp,customBroker,vessel1,voyage1,etd2,eta2,vessel2,voyage2,
						etd3,eta3,vessel3,voyage3,bookingNo,folderName,portOfLoading,portOfDischarge,
						String.join(",", errorList),String.join(",", warningList));
				wrkPlanMasterUploadBatchErrorDtoList.add(wrkPlanMasterUploadBatchErrorDto);
			}
		}

		if (!wrkPlanMasterUploadBatchErrorDtoList.isEmpty()) {
			generateWrkPlanMasterUploadBatchErrorFile(wrkPlanMasterUploadBatchErrorDtoList, fileName);
		}

		Map<String, Object> output = new HashMap<>();

		output.put("isError", isError);
		output.put("isWarning", isWarning);
		return output;
	}
	
	private LocalDate getIssueInvoiceDate(Map<String, Object> csvData) {
		LocalDate issueInvoiceDate = null;
		Boolean issueInvoiceDateError = validateDateFormat(csvData.get(ISSUE_INVOICE_DATE).toString());
		if (issueInvoiceDateError.equals(Boolean.TRUE)) {
			issueInvoiceDate = LocalDate.parse(csvData.get(ISSUE_INVOICE_DATE).toString(), formatter); 
		}
		return issueInvoiceDate;
	}
	
	private LocalDate getEtd1Date(Map<String, Object> csvData) {
		LocalDate etd1 = null;
		Boolean etd1Error = validateDateFormat(csvData.get(ETD1).toString());
		if (etd1Error.equals(Boolean.TRUE)) {
			etd1 = parseDate(csvData.get(ETD1).toString());
		}
		return etd1;
	}
	
	private LocalDate getEta1Date(Map<String, Object> csvData) {
		LocalDate eta1 = null;
		Boolean eta1Error = validateDateFormat(csvData.get(ETA1).toString());
		if (eta1Error.equals(Boolean.TRUE)) {
			eta1 =  parseDate(csvData.get(ETA1).toString());
		}
		return eta1;
	}
	
	private LocalDate getEtd2Date(Map<String, Object> csvData) {
		LocalDate etd2 = null;
		Boolean etd2Error = validateDateFormat(csvData.get(ETD2).toString());
		if (etd2Error.equals(Boolean.TRUE)) {
			etd2 = parseDate(csvData.get(ETD2).toString());
		}
		return etd2;
	}
	
	private LocalDate getEta2Date(Map<String, Object> csvData) {
		LocalDate eta2 = null;
		Boolean eta2Error = validateDateFormat(csvData.get(ETA2).toString());
		if (eta2Error.equals(Boolean.TRUE)) {
			eta2 =  parseDate(csvData.get(ETA2).toString());
		}
		return eta2;
	}
	
	private LocalDate getEtd3Date(Map<String, Object> csvData) {
		LocalDate etd3 = null;
		Boolean etd3Error = validateDateFormat(csvData.get(ETD3).toString());
		if (etd3Error.equals(Boolean.TRUE)) {
			etd3 = parseDate(csvData.get(ETD3).toString());
		}
		return etd3;
	}
	
	private LocalDate getEta3Date(Map<String, Object> csvData) {
		LocalDate eta3 = null;
		Boolean eta3Error = validateDateFormat(csvData.get(ETD3).toString());
		if (eta3Error.equals(Boolean.TRUE)) {
			eta3 =  parseDate(csvData.get(ETA3).toString());
		}
		return eta3;
	}

	private Map<String, Object> validateCsvRow(Map<String, Object> csvData) {

		String contDest = csvData.get(CONT_DESTINATION).toString();
		String renbanCode = csvData.get(RENBAN_CODE).toString();
		String customBroker = csvData.get(CUSTOM_BROKER).toString();
		String portOfLoading = csvData.get(PORT_OF_LOADING).toString();
		String portOfDischarge = csvData.get(PORT_OF_DISCHARGE).toString();

		LocalDate originalEtd = LocalDate.parse(csvData.get(ORIGINAL_ETD).toString(), formatter);

		List<String> errorList = new ArrayList<>();
		List<String> warningList = new ArrayList<>();

		errorList.addAll(validateDateFields(csvData));
		errorList.addAll(validateForBlankAndLength(csvData));

		//Check, If Custom Broker code does not exist in Customer Broker Master, log the error at each record level and continue file reading.
		if (!customBroker.isBlank() && noemCbMstRepository.countByCbNm(customBroker) == 0)
			errorList.add(ConstantUtils.ERR_IN_1098);

		//Check, If Port of Loading does not exist in Port master, log the error at each record level and continue file reading.
		if (!portOfLoading.isBlank() && oemPortMstRepository.countByCd(portOfLoading) == 0)
			errorList.add(ConstantUtils.ERR_IN_1115);

		//Check, If Port of Discharge does not exist in Port master, log the error at each record level and continue file reading.
		if (!portOfDischarge.isBlank() && oemPortMstRepository.countByCd(portOfDischarge) == 0)
			errorList.add(ConstantUtils.ERR_IN_1116);

		//(Key :  Original ETD, Cont Dest , Renban Code) 
		Date orgnlEtd = Date.valueOf(originalEtd);
		String notFoundWarning = validateRecordNotFound(orgnlEtd,contDest,renbanCode);
		if (!notFoundWarning.isBlank()) 
			warningList.add(notFoundWarning);

		//Check, If Invoice is already generated for the record
		String invAlrdyGeneratedWarning = validateInvGenFlag(orgnlEtd,contDest,renbanCode);
		if (!invAlrdyGeneratedWarning.isBlank()) 
			warningList.add(invAlrdyGeneratedWarning);

		Map<String, Object> result = new HashMap<>();
		result.put("errorList", errorList);
		result.put("warningList", warningList);
		return result;
	}

	private List<String> validateForBlankAndLength(Map<String, Object> csvData) {
		List<String> errorList = new ArrayList<>();

		String vessel1 = csvData.get(VESSEL1).toString();
		String voyage1 = csvData.get(VOYAGE1).toString();
		String vessel2 = csvData.get(VESSEL2).toString();
		String voyage2 = csvData.get(VOYAGE2).toString();
		String vessel3 = csvData.get(VESSEL3).toString();
		String voyage3 = csvData.get(VOYAGE3).toString();
		String folderName = csvData.get(FOLDER_NAME).toString();
		
		//Check, If Vessel1 Length is not more then 30 
		if(vessel1!= null && vessel1.length() > 30) {
			errorList.add(ConstantUtils.ERR_IN_1099);
		}

		//Check, If Voyage1 Length is not more then 10
		if(voyage1!= null && voyage1.length() > 10) {
			errorList.add(ConstantUtils.ERR_IN_1100);
		}

		//Check, If Vessel2 Length is not more then 30 
		if(vessel2!= null  && vessel2.length() > 30) {
			errorList.add(ConstantUtils.ERR_IN_1105);
		}

		//Check, If Voyage2 Length is not more then 10 
		if(voyage2!= null && voyage2.length() > 10) {
			errorList.add(ConstantUtils.ERR_IN_1106);
		}

		//Check, If Vessel3 Length is not more then 30 
		if(vessel3!= null  && vessel3.length() > 30) {
			errorList.add(ConstantUtils.ERR_IN_1112);
		}

		//Check, If Voyage3 Length is not more then 10 
		if(voyage3!= null && voyage3.length() > 10) {
			errorList.add(ConstantUtils.ERR_IN_1113);
		}

		//Check, If Folder Name Length is not more then 25
		if(folderName!= null && folderName.length() > 25) {
			errorList.add(ConstantUtils.ERR_IN_1114);
		}
		return errorList;
	}
	
	private List<String> validateDateFields(Map<String, Object> csvData) {
		
		List<String> errorList = new ArrayList<>();
		
		LocalDate issueInvoiceDate = null;
		LocalDate etd1 = null;
		LocalDate eta1 = null;
		
		//Check, If Issue Invoice Date is blank
		if(csvData.get(ISSUE_INVOICE_DATE).toString().isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1090);
		}else {
			//Check, If Issue Invoice Date format is dd/MM/yyyy
			Boolean issueInvoiceDateError = validateDateFormat(csvData.get(ISSUE_INVOICE_DATE).toString());
			if (issueInvoiceDateError.equals(Boolean.FALSE)) {
				errorList.add(ConstantUtils.ERR_IN_1091);
			}
			else {
				issueInvoiceDate = LocalDate.parse(csvData.get(ISSUE_INVOICE_DATE).toString(), formatter); 
			}
		}

		//Check, If ETD1 is blank
		if(csvData.get(ETD1).toString().isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1093);
		}else {
			Boolean etd1Error = validateDateFormat(csvData.get(ETD1).toString());
			//Check, If ETD1 format is dd/MM/yyyy
			if (etd1Error.equals(Boolean.FALSE)) {
				errorList.add(ConstantUtils.ERR_IN_1094);
			}else {
				etd1 = parseDate(csvData.get(ETD1).toString());
			}
		}


		//Check, If ETA1 is blank
		if(csvData.get(ETA1).toString().isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1096);
		}else {
			//Check, If ETA1 format is dd/MM/yyyy
			Boolean eta1Error = validateDateFormat(csvData.get(ETA1).toString());
			if (eta1Error.equals(Boolean.FALSE)) {
				errorList.add(ConstantUtils.ERR_IN_1097);
			}else {
				eta1 =  parseDate(csvData.get(ETA1).toString());
			}
		}

		errorList.addAll(validateIssueInvoiceDateAndEtd1(issueInvoiceDate, eta1, etd1));
		errorList.addAll(validateEtd2AndEta2(csvData));
		errorList.addAll(validateEtd3AndEta3(csvData));

		return errorList;

	}
	
	private List<String> validateIssueInvoiceDateAndEtd1(LocalDate issueInvoiceDate, LocalDate eta1, LocalDate etd1) {
		
		List<String> errorList = new ArrayList<>();
		//Check. If Issue Invoice Date is not Greater than ETD1 and Issue Invoice Date is not Past Date  
		if(issueInvoiceDate!=null && etd1!=null && (issueInvoiceDate.compareTo(etd1) > 0 || issueInvoiceDate.compareTo(currentDate) < 0)){
			errorList.add(ConstantUtils.ERR_IN_1092);
		}

		//Check, If ETD1 is less than ETA1
		if(etd1!= null && eta1!= null && etd1.compareTo(eta1) > 0) {
			errorList.add(ConstantUtils.ERR_IN_1095);
		}
		
		return errorList;
	}

	private List<String> validateEtd2AndEta2(Map<String, Object> csvData) {
		
		List<String> errorList = new ArrayList<>();
		LocalDate etd2 = null;
		LocalDate eta2 = null;
		
		//Check, If ETD2 format is dd/MM/yyyy
		if(!csvData.get(ETD2).toString().isBlank()) {
			Boolean etd2Error = validateDateFormat(csvData.get(ETD2).toString());
			if (etd2Error.equals(Boolean.FALSE)) {
				errorList.add(ConstantUtils.ERR_IN_1101);
			}else {
				etd2 = parseDate(csvData.get(ETD2).toString());
			}
		}

		//Check, If ETA2 format is dd/MM/yyyy
		if(!csvData.get(ETA2).toString().isBlank()) {
			Boolean eta2Error = validateDateFormat(csvData.get(ETA2).toString());
			if (eta2Error.equals(Boolean.FALSE)) {
				errorList.add(ConstantUtils.ERR_IN_1104);
			}else {
				eta2 =  parseDate(csvData.get(ETA2).toString());
			}
		}

		//Check, If ETD2 is less than ETA2
		if(etd2!= null && eta2!= null && etd2.compareTo(eta2) > 0) {
			errorList.add(ConstantUtils.ERR_IN_1102);
		}
		return errorList;
	}
	
	private List<String> validateEtd3AndEta3(Map<String, Object> csvData) {
		
		List<String> errorList = new ArrayList<>();
		LocalDate etd3 = null;
		LocalDate eta3 = null;
		
		//Check, If ETD3 format is dd/MM/yyyy
		if(!csvData.get(ETD3).toString().isBlank()) {
			Boolean etd3Error = validateDateFormat(csvData.get(ETD3).toString());
			if (etd3Error.equals(Boolean.FALSE)) {
				errorList.add(ConstantUtils.ERR_IN_1108);
			}else {
				etd3 = parseDate(csvData.get(ETD3).toString());
			}
		}

		//Check, If ETA3 format is dd/MM/yyyy
		if(!csvData.get(ETA3).toString().isBlank()) {
			Boolean eta3Error = validateDateFormat(csvData.get(ETA3).toString());
			if (eta3Error.equals(Boolean.FALSE)) {
				errorList.add(ConstantUtils.ERR_IN_1111);
			}else {
				eta3 =  parseDate(csvData.get(ETA3).toString());
			}
		}

		//Check, If ETD3 is less than ETA3
		if(etd3!= null && eta3!= null && etd3.compareTo(eta3) > 0) {
			errorList.add(ConstantUtils.ERR_IN_1109);
		}
		return errorList;
	}
	
	private String validateRecordNotFound(Date originalEtd, String contDest, String renbanCode) {

		if (originalEtd!=null && contDest!=null && renbanCode!=null && invGenWorkPlanMstRepository.getCountUsingKey(originalEtd, contDest,renbanCode) == 0) {
			return ConstantUtils.WRN_IN_1118;
		}
		return "";
	}

	private String validateInvGenFlag(Date originalEtd,String contDest,String renbanCode) {

		Optional<InvGenWorkPlanMstEntity>  entity = invGenWorkPlanMstRepository.findById(new InvGenWorkPlanMstIdEntity(originalEtd,contDest,renbanCode));

		if(entity.isPresent() && entity.get().getInvGenFlag()!=null && entity.get().getInvGenFlag().equalsIgnoreCase(ConstantUtils.INVOICE_ALREADY_GENERATED)) {
			return ConstantUtils.WRN_IN_1119;
		}

		return "";
	}

	private boolean validateDateFormat(String dateStr){

		SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);
		sdf.setLenient(false);

		try {
			sdf.parse(dateStr);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	private LocalDate parseDate(final String dateAsString) {
		if (StringUtils.isEmpty(dateAsString) || StringUtils.isBlank(dateAsString)) { 
			return null; 
		}
		return LocalDate.parse(dateAsString,formatter);
	}

	public void generateWrkPlanMasterUploadBatchErrorFile(List<WrkPlanMasterUploadBatchErrorDto> wrkPlanMasterUploadBatchErrorDtoList, String fileName) {
		//Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setAutoFitPageHeight", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", "xlsx");
		config.put("storeInDB", "true");
		config.put("loginUserId", ConstantUtils.TEST_USER);

		String fileTemplateName = tpexConfigRepository.findByName("work.plan.master.error").getValue();

		StringBuilder filePath = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(fileName);

		RddDownLocDtlEntity savedRddDownLocDtlEntity = jasperReportService.saveOfflineDownloadDetail(fileTemplateName, config, filePath);
		int reportId = savedRddDownLocDtlEntity != null ? savedRddDownLocDtlEntity.getReportId() :0;

		//If file size is greater than configured size then store file in directory and return path
		jasperReportService.getJasperReportDownloadOffline(wrkPlanMasterUploadBatchErrorDtoList, "xlsx", "work.plan.master.error", tpexConfigurationUtil.getReportDynamicPrameters(), config, reportId, filePath);
	}

}
