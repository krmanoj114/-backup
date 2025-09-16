package com.tpex.admin.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.admin.commonfiles.ApiResponseMessage;
import com.tpex.admin.dto.ColumnsDTO;
import com.tpex.admin.dto.CommonUploadAndDownloadDTO;
import com.tpex.admin.dto.ContainerRequisitionRequestDTO;
import com.tpex.admin.dto.CountryMasterDto;
import com.tpex.admin.dto.LotPartShortageReportsRequestDto;
import com.tpex.admin.dto.ProcessBatchDTO;
import com.tpex.admin.dto.ReportNameDTO;
import com.tpex.admin.dto.ReportNamesDTO;
import com.tpex.admin.dto.UploadAddressMasterJobDto;
import com.tpex.admin.dto.UploadCountryOfOriginJobDto;
import com.tpex.admin.dto.UploadLotPartPriceMasterJobDto;
import com.tpex.admin.dto.UploadWrkPlanMasterJobDto;
import com.tpex.admin.entity.CountryMasterEntity;
import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.exception.DateFormateException;
import com.tpex.admin.exception.InvalidFileException;
import com.tpex.admin.exception.InvalidInputParametersException;
import com.tpex.admin.exception.MyResourceNotFoundException;
import com.tpex.admin.repository.CountryMasterRepository;
import com.tpex.admin.service.CommonUploadAndDownloadService;
import com.tpex.admin.service.FileService;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.DateUtil;
import com.tpex.admin.util.TpexConfigurationUtil;

import net.sf.jasperreports.engine.JRException;

/**
 * The Class CommonUploadAndDownloadController.
 */
@RestController
@CrossOrigin
@RequestMapping("/commonUploadDownload")
public class CommonUploadAndDownloadController {

	 
	@Autowired
	CommonUploadAndDownloadService commonUploadAndDownloadService;

	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;

	@Autowired
	FileService fileService;

	@Autowired
	CountryMasterRepository countryMasterRepository;

	@Value("${invoice.batch.service.host}")
	String invoiceBatchServiceHost;

	@Autowired
	private WebClient webClient;


	/**
	 * Fetch report names and process details.
	 *
	 * @param commonUploadAndDownloadDTO the common upload and download DTO
	 * @return the response entity
	 * @throws IOException
	 * @throws DatabindException
	 * @throws StreamReadException
	 * @throws Exception           the exception
	 */
	@PostMapping(value = "/reportsAndprocessDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonUploadAndDownloadDTO> fetchReportNamesAndProcessDetails(
			@RequestBody CommonUploadAndDownloadDTO commonUploadAndDownloadDTO)
					throws MyResourceNotFoundException, IOException {

		String filePath = tpexConfigurationUtil.getFilePath(commonUploadAndDownloadDTO.getJsonFileName());

		ObjectMapper objectMapper = new ObjectMapper();
		File file = ResourceUtils.getFile(filePath);
		ReportNamesDTO reportNames = objectMapper.readValue(file, ReportNamesDTO.class);
		String userid = StringUtils.isNoneBlank(commonUploadAndDownloadDTO.getUserId())
				? commonUploadAndDownloadDTO.getUserId()
						: "";
		List<ProcessBatchDTO> listofProcessBatch = getProcessDetails(userid);
		commonUploadAndDownloadDTO.setListofProcessCtrl(listofProcessBatch);
		commonUploadAndDownloadDTO.setReportNames(reportNames);

		return new ResponseEntity<>(commonUploadAndDownloadDTO, HttpStatus.OK);
	}

	/**
	 * Code master name
	 *
	 * @param reportNameDTO the report name DTO
	 * @return the response entity
	 * @throws IOException
	 * @throws Exception   the exception
	 */
	@PostMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ColumnsDTO> codeMasterName(@RequestBody ReportNameDTO reportNameDTO) throws IOException {
		ColumnsDTO columnsModel = tpexConfigurationUtil.readDataFromJson(reportNameDTO.getReportJsonPath());
		return new ResponseEntity<>(columnsModel, HttpStatus.OK);
	}

	/**
	 * method for get CountryCode and Name form country code master table
	 * 
	 * @return
	 * @author Mohd.Javed
	 * @throws Exception
	 */
	@GetMapping(value = "/countrycodename", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CountryMasterDto>> countryCodeName() {

		List<CountryMasterEntity> countryMasterEntity = countryMasterRepository.findAll();
		List<CountryMasterDto> response = new ArrayList<>();
		for (CountryMasterEntity enttity : countryMasterEntity) {
			CountryMasterDto countryMasterDto = new CountryMasterDto();
			countryMasterDto.setCountryCodeName(enttity.getCountryCode() + "-" + enttity.getCountryName());
			response.add(countryMasterDto);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Fetch process details.
	 *
	 * @param reportNameDTO the report name DTO
	 * @return the response entity
	 * @throws StreamReadException the stream read exception
	 * @throws DatabindException   the databind exception
	 * @throws IOException         Signals that an I/O exception has occurred.
	 */
	@PostMapping(value = "/processDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OemProcessCtrlEntity>> fetchProcessDetails(@RequestBody ReportNameDTO reportNameDTO)
			throws IOException {
		String userid = StringUtils.isNoneBlank(reportNameDTO.getUserId()) ? reportNameDTO.getUserId() : "";
		List<OemProcessCtrlEntity> listOfProcess = commonUploadAndDownloadService.fetchProcessDetails(userid);
		return new ResponseEntity<>(listOfProcess, HttpStatus.OK);
	}

	/**
	 * Gets the nal cal mast list template report.
	 *
	 * @param year   the year
	 * @param userId the user id
	 * @return the nal cal mast list template report
	 * @throws IOException
	 * @throws JRException
	 * @throws Exception   the exception
	 */
	@GetMapping("/downloadNatCalMasterTemplate")
	public ResponseEntity<Object> getNalCalMastListTemplateReport(@RequestParam String year,
			@RequestParam String userId) throws JRException, IOException {
		Object obj = this.commonUploadAndDownloadService.downloadNatCalMast(year, userId, true);

		if (obj instanceof String) {
			return ResponseEntity.ok().body(obj);
		} else {
			HashMap<String, Object> map = (HashMap<String, Object>) obj;
			if (map.get(ConstantUtils.OUTSTREAM) instanceof byte[]) {
				String fileName = (String) map.get(ConstantUtils.FILENAME);
				HttpHeaders headers = new HttpHeaders();
				headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT_FILENAME + fileName);
				return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
						.body(map.get(ConstantUtils.OUTSTREAM));
			} else {
				return ResponseEntity.internalServerError().build();
			}

		}

	}

	/**
	 * Gets the nal cal mast list report.
	 *
	 * @param year   the year
	 * @param userId the user id
	 * @return the nal cal mast list report
	 * @throws IOException
	 * @throws JRException
	 * @throws Exception   the exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/downlaodNatCalMaster", produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public ResponseEntity<Object> getNalCalMastListReport(@RequestParam String year, @RequestParam String userId)
			throws JRException, IOException {

		Object obj = this.commonUploadAndDownloadService.downloadNatCalMast(year, userId, false);
		Map<String, Object> response = new HashMap<>();
		if (obj instanceof String) {
			response.put(ConstantUtils.MESSAGE, ConstantUtils.INFO_AD_2001);
			response.put(ConstantUtils.STATUS, ConstantUtils.OFFLINE);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			HashMap<String, Object> map = (HashMap<String, Object>) obj;
			if (map.get("outStream") instanceof byte[]) {
				String fileName = (String) map.get(ConstantUtils.FILENAME);
				HttpHeaders headers = new HttpHeaders();
				headers.add("file_name", fileName);
				headers.add(ConstantUtils.CONTENT_DISPOSITION, "attachment; filename= " + fileName);
				return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
						.body(map.get("outStream"));
			} else {
				return (ResponseEntity<Object>) ResponseEntity.internalServerError();
			}
		}
	}

	/**
	 * Method for download common LopPartPriceMaster
	 * 
	 * @author Mohd.Javed
	 * @param userId
	 * @return
	 * @throws IOException
	 * @throws JRException
	 * @throws Exception
	 */
	@GetMapping("/downloadLotPartPriceMstTemplate")
	public ResponseEntity<Object> getLotPartPriceMasterTemplateReport(@RequestParam String userId)
			throws JRException, IOException {
		return getDownloadObjResponse(this.commonUploadAndDownloadService.downloadLotPartPriceMast(userId, true));
	}
	
	private ResponseEntity<Object> getDownloadObjResponse(Object obj) {
		if (obj instanceof String) {
			return ResponseEntity.ok().body(obj);
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>) obj;
			if (map.get(ConstantUtils.OUTSTREAM) instanceof byte[]) {
				String fileName = (String) map.get(ConstantUtils.FILENAME);
				HttpHeaders headers = new HttpHeaders();
				headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT_FILENAME + fileName);
				return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
						.body(map.get(ConstantUtils.OUTSTREAM));
			} else {
				return ResponseEntity.internalServerError().build();
			}

		}
	}

	@GetMapping("/downloadAddressMaster")
	public ResponseEntity<Object> getAddressMasterReport(@RequestParam String cmpCode, @RequestParam String userId)
			throws JRException, IOException {
		return getDownloadObjResponse(this.commonUploadAndDownloadService.downloadAddressMaster(cmpCode, userId, true));
	}

	/**
	 * Method for download common PxpPartPriceMaster
	 * 
	 * @author Mohd.Javed
	 * @param userId
	 * @return
	 * @throws IOException
	 * @throws JRException
	 * @throws Exception
	 */
	@GetMapping("/downloadPxpPartPriceTemplate")
	public ResponseEntity<Object> getPxpPartPriceMasterTemplateReport(@RequestParam String userId)
			throws JRException, IOException {
		return getDownloadObjResponse(this.commonUploadAndDownloadService.downloadPxpPartPriceMast(userId, true));
	}

	/**
	 * Batch receiver.
	 *
	 * @param batchName the batch name
	 * @param userId    the user id
	 * @param year      the year
	 * @param file      the file
	 * @return the response entity
	 * @throws IOException
	 * @throws Exception   the exception
	 */
	@PostMapping(path = "/uplaodNatCalMaster") // Start batch process path
	public ResponseEntity<List<ProcessBatchDTO>> batchReceiver(@RequestParam String batchName,
			@RequestParam String userId, @RequestParam Integer year, @RequestParam("file") MultipartFile file)
					throws IOException {

		// First validate file content
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);

		validateExcel(workbook, worksheet);

		// File Data validation
		Iterator<Row> iterator = worksheet.iterator();// iterating over excel file
		iterator.next();

		Set<String> set = new HashSet<>();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			// If Additional Parameter (Year) does not match with Year mentioned in the file
			Cell yearCell = row.getCell(0);

			String excelYearValue = String.valueOf((int) yearCell.getNumericCellValue());

			validateExcelYearValue(year, workbook, excelYearValue);

			// Month is not valid
			Cell monthCell = row.getCell(1);

			String excelMonthValue = monthCell.getStringCellValue();
			int month;
			try {
				DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("MMM");
				TemporalAccessor temporalAccessor = dtFormatter.parse(excelMonthValue);
				month = temporalAccessor.get(ChronoField.MONTH_OF_YEAR);
			} catch (Exception e) {
				workbook.close();
				throw new InvalidFileException("ERR_AD_2007");
			}

			// Invalid date of month for a given year
			YearMonth date = YearMonth.of(year, Month.of(month));
			Cell dateCell = row.getCell(2);
			int monthDate;
			if (dateCell.getCellType() == CellType.STRING) {
				monthDate = Integer.valueOf(dateCell.getStringCellValue());
			} else {
				monthDate = (int) dateCell.getNumericCellValue();
			}
			if (!date.isValidDay(monthDate)) {
				workbook.close();
				throw new InvalidFileException("ERR_AD_2008");
			}

			// Invalid day of week
			String excelDayValue = row.getCell(3).getStringCellValue();
			String[] dayOfWeek = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
			List<String> list = Arrays.asList(dayOfWeek);

			if (!list.contains(StringUtils.upperCase(excelDayValue))) {
				workbook.close();
				throw new InvalidFileException("ERR_AD_2009");
			}

			// Day Value is not ‘W’ or “H’
			Cell whCell = row.getCell(4);
			String whCellValue = whCell.getStringCellValue();
			if (!("W".equalsIgnoreCase(whCellValue) || "H".equalsIgnoreCase(whCellValue))) {
				workbook.close();
				throw new InvalidFileException("ERR_AD_2010");
			}

			// Check for duplicate value
			String dateStr = excelYearValue + excelMonthValue + (int) row.getCell(2).getNumericCellValue();
			if (!set.add(dateStr)) {
				workbook.close();
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put("dateStr", dateStr);
				throw new InvalidFileException(ConstantUtils.ERR_AD_2012, errorMessageParams);
			}
		}

		workbook.close();
		String batchParameter = null;
		batchParameter = "Year" + " " + year;
		OemProcessCtrlEntity oemProcessCtrlEntity = commonUploadAndDownloadService.saveProcessDetails(userId,
				batchParameter);
		fileService.save(file);
		String name = file.getOriginalFilename();
		commonUploadAndDownloadService.jobServiceReceiving(batchName, name, oemProcessCtrlEntity);

		List<ProcessBatchDTO> listofProcessBatch = getProcessDetails(userId);
		return new ResponseEntity<>(listofProcessBatch, HttpStatus.OK);

	}

	private void validateExcelYearValue(Integer year, XSSFWorkbook workbook, String excelYearValue) throws IOException {

		if (excelYearValue.isBlank() || year != Integer.parseInt(excelYearValue)) {
			workbook.close();
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("year", year);
			errorMessageParams.put("excelYearValue", excelYearValue);
			throw new InvalidFileException(ConstantUtils.ERR_AD_2011, errorMessageParams);
		}
	}

	private void validateExcel(XSSFWorkbook workbook, XSSFSheet worksheet) throws IOException {

		// Empty file validation
		if (worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1) {
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}

		// Blank cells validation
		Iterator<Row> itr = worksheet.iterator();// iterating over excel file
		itr.next();
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

		if (isEmptySheet) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}

		// Check for valid headers
		List<String> header = Arrays.asList("year", "month", "date", "day", "w/h", "remarks");
		if (!tpexConfigurationUtil.checkForValidHeader(worksheet, header)) {
			workbook.close();
			throw new InvalidFileException("ERR_AD_2006");
		}
	}

	/**
	 * Gets the process details.
	 *
	 * @param userId the user id
	 * @return the process details
	 */
	private List<ProcessBatchDTO> getProcessDetails(String userId) {
		List<OemProcessCtrlEntity> listOfProcess = commonUploadAndDownloadService.fetchProcessDetails(userId);
		return listOfProcess.stream()
				.map(p -> new ProcessBatchDTO(p.getId().getProcessControlId(), p.getId().getBatchId(),
						commonUploadAndDownloadService.getProcessNameById(p.getId().getBatchId()), p.getParameter(),
						p.getUserId(), DateUtil.convertToDatabaseColumn(p.getSubmitTime()),
						getProcessStatus(p.getStatus()), p.getErrorFilePath()))
				.collect(Collectors.toList());
	}

	/**
	 * Gets the process details list.
	 *
	 * @param userId the user id
	 * @return the process details list
	 * @throws Exception the exception
	 */
	@GetMapping("/processDetails")
	public ResponseEntity<List<ProcessBatchDTO>> getProcessDetailsList(@RequestParam String userId) {
		List<ProcessBatchDTO> listofProcessBatch = getProcessDetails(userId);
		return new ResponseEntity<>(listofProcessBatch, HttpStatus.OK);

	}

	/**
	 * @author Mohd.Javed
	 * @param batchName
	 * @param userId
	 * @param month
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	@PostMapping(path = "/uplaodLotPartPriceMaster")
	public ResponseEntity<List<ProcessBatchDTO>> batchReceiver(@RequestParam String batchName,
			@RequestParam String userId, @RequestParam String month, @RequestParam("file") MultipartFile file)
					throws IOException {
		if (batchName.isBlank() || batchName.isEmpty() || month.isBlank() || file.isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}

		String filename = file.getOriginalFilename();
		if (filename != null && (!(filename.endsWith(".xls") || filename.endsWith(ConstantUtils.EXCEL_FORMAT)))) {
			throw new InvalidFileException(ConstantUtils.ERR_CM_3021);
		}

		Workbook workbook = getWorkbook(file);
		Sheet worksheet = workbook.getSheetAt(0);

		if (worksheet.getPhysicalNumberOfRows() == 0 || tpexConfigurationUtil.checkForEmptySheet(worksheet, 1)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}

		// Check for valid headers
		List<String> header = Arrays.asList("CURRENCY", "LOT", "CFC", "IMP", "PART NO", "PART NAME", "FIRSTOFPRICE", "USAGE");

		if (tpexConfigurationUtil.checkForValidHeader(worksheet, header) && tpexConfigurationUtil.checkForValidFirstRow(worksheet)) {
		    workbook.close();
		    throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
		}


		workbook.close();

		fileService.save(file);
		String batchParameter = null;
		batchParameter = "Effective Date " + month;
		OemProcessCtrlEntity oemProcessCtrlEntity = commonUploadAndDownloadService.saveProcessDetailsLot(userId,
				batchParameter);
		UploadLotPartPriceMasterJobDto uploadLotPartPriceMasterJobDto = new UploadLotPartPriceMasterJobDto();
		uploadLotPartPriceMasterJobDto.setBatchName(batchName);
		uploadLotPartPriceMasterJobDto.setFileName(file.getOriginalFilename());
		uploadLotPartPriceMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadLotPartPriceMasterJobDto.setEffectiveDate(month);
		uploadLotPartPriceMasterJobDto.setUserId(userId);

		webClient.post().uri(invoiceBatchServiceHost + "/invoice/uplaodLotPartPriceMaster")
		.bodyValue(uploadLotPartPriceMasterJobDto).retrieve().bodyToMono(String.class).block();

		List<ProcessBatchDTO> listofProcessBatch = getProcessDetailsLot(userId);
		return new ResponseEntity<>(listofProcessBatch, HttpStatus.OK);

	}

	/**
	 * Gets the process details.
	 * 
	 * @author Mohd.Javed
	 * @param userId the user id
	 * @return the process details
	 */
	private List<ProcessBatchDTO> getProcessDetailsLot(String userId) {
		String processName = commonUploadAndDownloadService.getProcessNameById(ConstantUtils.INFO_AD_2012);
		List<OemProcessCtrlEntity> listOfProcess = commonUploadAndDownloadService.fetchProcessDetails(userId);
		return listOfProcess.stream()
				.map(p -> new ProcessBatchDTO(p.getId().getProcessControlId(), p.getId().getBatchId(), processName,
						p.getParameter(), p.getUserId(), DateUtil.convertToDatabaseColumn(p.getSubmitTime()),
						getProcessStatus(p.getStatus()), p.getErrorFilePath()))
				.collect(Collectors.toList());
	}

	private String getProcessStatus(int status) {
		switch (status) {
		case 0:
			return ConstantUtils.STS_ERROR;
		case 1:
			return ConstantUtils.STS_SUCCESS;
		case 2:
			return ConstantUtils.STS_SUCCESS_WTH_WARN;
		default:
			return ConstantUtils.STS_PROCESSING;
		}
	}

	/**
	 * @author Mohd.Javed
	 * @param batchName
	 * @param userId
	 * @param effectiveFrom
	 * @param effectiveTo
	 * @param file
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws Exception
	 */
	@PostMapping(path = "/uplaodPxpPartPriceMaster")
	public ResponseEntity<List<ProcessBatchDTO>> batchReceiver(@RequestParam String batchName,
			@RequestParam String userId, @RequestParam String effectiveFrom, @RequestParam String effectiveTo,
			@RequestParam("file") MultipartFile file) throws ParseException, IOException {

		String filename = file.getOriginalFilename();
		if (filename != null && (!(filename.endsWith(".xls") || filename.endsWith(ConstantUtils.EXCEL_FORMAT)))) {
			throw new InvalidFileException(ConstantUtils.ERR_CM_3021);
		}
		if (batchName.isBlank() || batchName.isEmpty() || effectiveFrom.isBlank() || effectiveTo.isBlank()
				|| file.isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}
		SimpleDateFormat sdformat = new SimpleDateFormat(ConstantUtils.YYYYMM);
		Date fromDate = sdformat.parse(effectiveFrom);
		Date toDate = sdformat.parse(effectiveTo);
		if (fromDate.compareTo(toDate) > 0) {
			throw new DateFormateException(ConstantUtils.ERR_CM_3002);
		} 
		return commonUploadAndDownloadService.batchProcess(batchName, userId, effectiveFrom, effectiveTo, file);
	}

	/**
	 * Gets the Country of Origin mast list report.
	 *
	 *
	 * @return the Country of Origin mast list report
	 * @throws Exception the exception
	 * @author Anurag.3.Tiwari
	 * @throws ParseException
	 * @throws DecoderException
	 * @throws IOException
	 */
	@GetMapping(value = "/downloadCountryofOriginMaster", produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public ResponseEntity<Object> getCountryofOriginMastListReport(@RequestParam String partPriceNo,
			@RequestParam List<String> countryOfOriginCode, @RequestParam String vanDateFrom,
			@RequestParam String vanDateTo) throws IOException, DecoderException, ParseException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		String currentDateTime = LocalDateTime.now().format(formatter);
		String fileName = ConstantUtils.COUNTRY_OF_ORIGIN_FILENAME + "_" + currentDateTime + ConstantUtils.EXCEL_FORMAT;
		byte[] byteResponse = commonUploadAndDownloadService.getCountryofOriginMastListReport(partPriceNo,
				countryOfOriginCode, vanDateFrom, vanDateTo);
		if (byteResponse != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT_FILENAME + fileName);
			headers.add(ConstantUtils.FILENAME_COO, fileName);
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)

					.body(byteResponse);
		} else {

			return ResponseEntity.internalServerError().build();
		}

	}

	@GetMapping("/downloadAddressMasterTemplate")
	// Exposed header param to be accessible at UI
	@CrossOrigin
	public ResponseEntity<Object> downloadAddressMasterTemplate(@RequestParam String userId) {
		Object obj;
		try {
			obj = this.commonUploadAndDownloadService.downloadAddressMasterTemplate(userId);
		} catch (JRException | IOException e) {
			return ResponseEntity.internalServerError().build();
		}

		if (obj instanceof String) {
			return ResponseEntity.ok().body(obj);
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>) obj;
			if (map.get(ConstantUtils.OUTSTREAM) instanceof byte[]) {
				String fileName = (String) map.get(ConstantUtils.FILENAME);
				HttpHeaders headers = new HttpHeaders();
				headers.add(ConstantUtils.FILENAME, fileName);
				headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT_FILENAME + fileName);
				return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
						.body(map.get(ConstantUtils.OUTSTREAM));
			} else {
				return ResponseEntity.internalServerError().build();
			}

		}
	}

	private Workbook getWorkbook(MultipartFile file) throws IOException {
		Workbook workbook = null;
		String excelFilePath = file.getOriginalFilename();
		if (excelFilePath != null && excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else if (excelFilePath != null && excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		} else {
			throw new InvalidFileException(ConstantUtils.ERR_CM_3021);
		}
		
		return workbook;
	}
	
	@PostMapping(path = "/uplaodAddressMaster")
	public ResponseEntity<List<ProcessBatchDTO>> uploadAddressMaster(@RequestParam String batchName,
			@RequestParam String userId, @RequestParam String companyCode, @RequestParam("file") MultipartFile file)
					throws IOException {

		if (batchName.isBlank() || file.isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}
		String filename = file.getOriginalFilename();
		if (filename != null && (!(filename.endsWith(".xls") || filename.endsWith(ConstantUtils.EXCEL_FORMAT)))) {
			throw new InvalidFileException(ConstantUtils.ERR_CM_3021);
		}
		
		Workbook workbook = getWorkbook(file);
		Sheet worksheet = workbook.getSheetAt(0);

		List<String> header = Arrays.asList("COMPANY CODE", "BRANCH", "COMPANY NAME", "ADDRESS 1", "ADDRESS 2",
				"ADDRESS 3", "ADDRESS 4", "ZIP CODE", "SHORT NAME", "SAP A/C CODE", "CONSIGNEE", "COUNTRY CODE",
				"TELEPHONE NO.", "FAX NO.", "EMAIL ADDRESS", "TELEX NO.", "CONTACT PERSON", "DEFAULT INVOICE ADDRESS",
				"SC FLAG", "SC REMARK", "EMPLOYEE 1", "EMPLOYEE 2", "EMPLOYEE 3");

		if (worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1 || !tpexConfigurationUtil.checkForValidHeader(worksheet, header)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_CM_3030);
		}

		if (tpexConfigurationUtil.checkForEmptySheet(worksheet, 2)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}

		workbook.close();

		// Save file
		fileService.save(file);
		OemProcessCtrlEntity oemProcessCtrlEntity = commonUploadAndDownloadService.saveProcessCtrlDetail(userId, null,
				batchName);
		UploadAddressMasterJobDto uploadAddressMasterJobDto = new UploadAddressMasterJobDto();
		uploadAddressMasterJobDto.setBatchName(batchName);
		uploadAddressMasterJobDto.setFileName(file.getOriginalFilename());
		uploadAddressMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadAddressMasterJobDto.setCompanyCode(companyCode);
		uploadAddressMasterJobDto.setUserId(userId);

		webClient.post().uri(invoiceBatchServiceHost + "/invoice/uplaodAddressMaster")
		.bodyValue(uploadAddressMasterJobDto).retrieve().bodyToMono(String.class).block();

		List<ProcessBatchDTO> listofProcessBatch = getProcessDetails(userId);
		return new ResponseEntity<>(listofProcessBatch, HttpStatus.OK);

	}

	private boolean checkForEmptySheet(XSSFSheet worksheet, int rowsToSkip) {
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


	private boolean checkForValidHeader(XSSFSheet worksheet, List<String> header) {
		boolean isValidHeader = true;
		// Header row validation
		Row headerRow = worksheet.getRow(0);

		if(headerRow.getLastCellNum() > header.size()) {
			isValidHeader = false;
			return isValidHeader;
		}
		// Check for valid headers
		for (int i = 0; i < header.size(); i++) {
			if (headerRow.getCell(i).getCellType() == CellType.BLANK
					|| !(headerRow.getCell(i).getStringCellValue().equalsIgnoreCase(header.get(i)))) {
				isValidHeader = false;
				break;
			}
		}
		return isValidHeader;
	}

	@PostMapping(path = "/uploadWrkPlanMaster")
	public ResponseEntity<ApiResponseMessage> uploadWrkPlanMaster(@RequestParam String batchName,
			@RequestParam String userId, @RequestParam("file") MultipartFile file) throws IOException {
		if (batchName.isBlank() || file.isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);

		if (worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1
				|| checkForEmptySheet(worksheet, 1)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}

		List<String> header = Arrays.asList("DSI /TI(CPO/SPO)", "Issue Invoice Date", "Original ETD",
				"Cont. Destination", "ETD1", "ETA1", "20'", "40'", "RENBAN Code", "Ship.Comp", "Custom Broker",
				"Vessel 1", "Voy 1", "ETD2", "ETA2", "Vessel 2", "Voy 2", "ETD3", "ETA3", "Vessel 3", "Voy 3",
				"Booking No.", "Folder name ", "Port of Loading", "Port of Discharge");

		if (!checkForValidHeader(worksheet, header)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
		}

		workbook.close();

		// Save file
		fileService.save(file);
		OemProcessCtrlEntity oemProcessCtrlEntity = commonUploadAndDownloadService.saveProcessCtrlDetail(userId, null,
				batchName);
		UploadWrkPlanMasterJobDto uploadWrkPlanMasterJobDto = new UploadWrkPlanMasterJobDto();
		uploadWrkPlanMasterJobDto.setBatchName(batchName);
		uploadWrkPlanMasterJobDto.setFileName(file.getOriginalFilename());
		uploadWrkPlanMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadWrkPlanMasterJobDto.setUserId(userId);

		Map<String,Object> errorMessageParams = new HashMap<>();
		String response = null;

		try {
			response = commonUploadAndDownloadService.uploadWrkPlanMasterBatchServiceCall(uploadWrkPlanMasterJobDto);
		}catch (InvalidInputParametersException e) {
			errorMessageParams.put("errorCode",HttpStatus.EXPECTATION_FAILED.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.EXPECTATION_FAILED, ConstantUtils.ERR_CM_3006,errorMessageParams), HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, response), HttpStatus.OK);
	}

	/**
	 * Uplaod car family destination master.
	 *
	 * @param batchName   the batch name
	 * @param userId      the user id
	 * @param companyCode the company code
	 * @param file        the file
	 * @return the response entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@PostMapping(path = "/uplaodCarFamilyDestinationMaster")
	public ResponseEntity<List<ProcessBatchDTO>> uplaodCarFamilyDestinationMaster(@RequestParam String batchName,
			@RequestParam String userId, @RequestParam String companyCode, @RequestParam("file") MultipartFile file)
					throws IOException {
		return new ResponseEntity<>(
				commonUploadAndDownloadService.uplaodCarFamilyDestinationMaster(batchName, file, companyCode, userId),
				HttpStatus.OK);
	}

	/**
	 * Download car family destination master.
	 *
	 * @param reponse the reponse
	 * @param userId  the user id
	 * @return the response entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JRException the JR exception
	 */
	@GetMapping(value = "/downloadCarFamilyDestinationMaster", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> downloadCarFamilyDestinationMaster(HttpServletResponse reponse,
			@RequestParam String userId) throws IOException, JRException {
		String fileName = ConstantUtils.CAR_FAMILY_DESTINATION_FILENAME + ConstantUtils.EXCEL_FORMAT;
		byte[] byteResponse = commonUploadAndDownloadService.downloadCarFamilyDestinationMaster(userId);
		if (byteResponse != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT_FILENAME + fileName);
			headers.add(ConstantUtils.FILENAME, fileName);
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(byteResponse);
		} else {
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * Gets the Car Family Destination Master list report.
	 *
	 * @return the Car Family Destination Master list report
	 * @throws Exception the exception
	 * @author akshatha.me
	 */
	@GetMapping(value = "/downloadCarFmlyDestMasterReport", produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public ResponseEntity<Object> getCarFmlyDestnMstListReport(HttpServletResponse reponse,
			@RequestParam String cmpCode, @RequestParam String userId)
					throws IOException, DecoderException, ParseException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtils.CARFMLYDESTMST_DOWNLOAD_DATE_FORMAT);
		String currentDateTime = LocalDateTime.now().format(formatter);

		String fileName = ConstantUtils.CAR_FAMILY_DESTINATION_FILENAME + "_" + currentDateTime
				+ ConstantUtils.EXCEL_FORMAT;

		byte[] byteResponse = commonUploadAndDownloadService.getCarFmlyDestnMstListReport(reponse, cmpCode, userId);
		if (byteResponse != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT_FILENAME + fileName);
			headers.add(ConstantUtils.FILENAME, fileName);
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(byteResponse);
		} else {
			return ResponseEntity.internalServerError().build();
		}

	}

	@PostMapping(path = "/UploadcountryCodeOrigin")
	public ResponseEntity<ApiResponseMessage> uploadCountryCodeOrigin(@RequestParam String batchName,
			@RequestParam String userId,@RequestParam String companyCode, @RequestParam("file") MultipartFile file) throws IOException {
		if (batchName.isBlank() || file.isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);
		XSSFSheet worksheet1 = workbook.getSheetAt(1);
		if (worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1
				|| checkForEmptySheet(worksheet, 1)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}
		List<String> header = Arrays.asList("*Part no.", "Part Name", "*Country of Origin Code", "*Van Date From",
				"*Van Date To");
		if (!checkForValidHeader(worksheet, header)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
		}
		List<String> headerforSecondSheet = Arrays.asList("Country of Origin code", "Country Name");
		workbook.setActiveSheet(1);
		if (!checkForValidHeader(worksheet1, headerforSecondSheet)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
		}

		// Header row validation
		Row headerRow = worksheet.getRow(1);

		// Check for valid headers
		if (headerRow.getCell(0).getCellType() == CellType.BLANK
				|| !(headerRow.getCell(0).getStringCellValue().equalsIgnoreCase("xx..(12)"))
				|| headerRow.getCell(1).getCellType() == CellType.BLANK
				|| !(headerRow.getCell(1).getStringCellValue().equalsIgnoreCase("xx..(40)"))
				|| headerRow.getCell(2).getCellType() == CellType.BLANK
				|| !(headerRow.getCell(2).getStringCellValue().equalsIgnoreCase("Code+ Country Name"))
				|| headerRow.getCell(3).getCellType() == CellType.BLANK
				|| !(headerRow.getCell(3).getStringCellValue().equalsIgnoreCase("DD/MM/YYYY"))
				|| headerRow.getCell(4).getCellType() == CellType.BLANK
				|| !(headerRow.getCell(4).getStringCellValue().equalsIgnoreCase("DD/MM/YYYY"))) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
		}
		workbook.close();

		// Save file
		fileService.save(file);

		OemProcessCtrlEntity oemProcessCtrlEntity = commonUploadAndDownloadService.saveProcessCtrlDetail(userId, companyCode,
				batchName);

		UploadCountryOfOriginJobDto uploadCountryOfOriginJobDto = new UploadCountryOfOriginJobDto();
		uploadCountryOfOriginJobDto.setBatchName(batchName);
		uploadCountryOfOriginJobDto.setFileName(file.getOriginalFilename());
		uploadCountryOfOriginJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadCountryOfOriginJobDto.setUserId(userId);

		String response = commonUploadAndDownloadService.countryCodeofOriginBatchServiceCall(uploadCountryOfOriginJobDto);

		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, response), HttpStatus.OK);
	}

	@GetMapping("/downloadCountryofOriginTemplate")
	@CrossOrigin
	public ResponseEntity<Object> downloadCountryOfOriginTemplate(@RequestParam String userId,
			HttpServletResponse response) throws JRException, IOException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String currentDateTime = LocalDateTime.now().format(formatter);

		String fileName = ConstantUtils.COUNTRY_OF_ORIGIN_FILENAME + "_" + currentDateTime + ConstantUtils.EXCEL_FORMAT;

		byte[] byteResponse = this.commonUploadAndDownloadService.downloadCountryOfOriginTemplate(userId, response);

		if (byteResponse != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT_FILENAME + fileName);
			headers.add(ConstantUtils.FILENAME, fileName);
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(byteResponse);
		} else {
			return ResponseEntity.internalServerError().build();

		}
	}

	/**
	 * method for generate excell report of Lot Part Shortage
	 * 
	 * @param request
	 * @author Mohd.Javed
	 * @return
	 * @throws JRException
	 * @throws IOException
	 * @throws ParseException
	 */
	@PostMapping(value = "/downloadLotPartShortageReport", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> downloadLotPartShortageReport(@RequestBody LotPartShortageReportsRequestDto request)
			throws JRException, IOException, ParseException {

		if (request != null
				&& (StringUtils.isEmpty(request.getEtdMonth()) || StringUtils.isEmpty(request.getRevisionNo()))) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}

		Object obj = commonUploadAndDownloadService.generateLostPartShortageReport(request);

		Object outStream = null;
		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) obj;
		String fileName = map != null ? (String) map.get(ConstantUtils.FILENAME) : "";
		if (map != null && map.get(ConstantUtils.OUTSTREAM) != null) {
			outStream = map.get(ConstantUtils.OUTSTREAM);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(ConstantUtils.FILENAME, fileName);
		headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT + fileName);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(outStream);

	}
	
	@PostMapping(path = "/uploadPartMaster")
	public ResponseEntity<ApiResponseMessage> uploadPartMaster(@RequestParam String batchName,
			@RequestParam String userId, @RequestParam String companyCode, @RequestParam("file") MultipartFile file){

		String response =  commonUploadAndDownloadService.uploadPartMasterBatchServiceCall( batchName,file,companyCode, userId);

		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, response), HttpStatus.OK);
	}
	
	@PostMapping(value = "/containerRequisition", produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(exposedHeaders = { "filename" })
	public ResponseEntity<Object> downloadContainerRequisitionSheet(@RequestBody ContainerRequisitionRequestDTO containerRequisitionRequestDTO)
			throws JRException, IOException, ParseException {

		if (containerRequisitionRequestDTO != null
				&& (StringUtils.isEmpty(containerRequisitionRequestDTO.getVanMonth())) && (StringUtils.isEmpty(containerRequisitionRequestDTO.getVanDate()))) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}

		Object obj = commonUploadAndDownloadService.generateContainerRequisitionSheet(containerRequisitionRequestDTO);

		Object outStream = null;
		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) obj;
		String fileName = map != null ? (String) map.get(ConstantUtils.FILENAME) : "";
		if (map != null && map.get(ConstantUtils.OUTSTREAM) != null) {
			outStream = map.get(ConstantUtils.OUTSTREAM);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(ConstantUtils.FILENAME, fileName);
		headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT + fileName);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(outStream);

	}
}
