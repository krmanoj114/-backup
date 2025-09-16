package com.tpex.admin.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.tpex.admin.dto.AddressMasterDTO;
import com.tpex.admin.dto.ContainerRequisitionRequestDTO;
import com.tpex.admin.dto.ContainerRequisitionResponseDto;
import com.tpex.admin.dto.LotPartPriceMasterDto;
import com.tpex.admin.dto.LotPartShortageReportsRequestDto;
import com.tpex.admin.dto.LotPartShortageReportsResponseDto;
import com.tpex.admin.dto.NatCalDTO;
import com.tpex.admin.dto.ProcessBatchDTO;
import com.tpex.admin.dto.PxpPartPriceMasterDto;
import com.tpex.admin.dto.ReportNameDTO;
import com.tpex.admin.dto.UploadAddressMasterJobDto;
import com.tpex.admin.dto.UploadCountryOfOriginJobDto;
import com.tpex.admin.dto.UploadPartMasterJobDto;
import com.tpex.admin.dto.UploadPxpPriceMasterJobDto;
import com.tpex.admin.dto.UploadWrkPlanMasterJobDto;
import com.tpex.admin.entity.AddressMasterEntity;
import com.tpex.admin.entity.CarFamilyDestinationMasterEntity;
import com.tpex.admin.entity.CarFamilyMasterEntity;
import com.tpex.admin.entity.CountryMasterEntity;
import com.tpex.admin.entity.FinalDestEntity;
import com.tpex.admin.entity.NatCalEntity;
import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.entity.OemProcessCtrlIdEntity;
import com.tpex.admin.entity.OemProgDtlsEntity;
import com.tpex.admin.entity.RddDownLocDtlEntity;
import com.tpex.admin.entity.TbMPlant;
import com.tpex.admin.exception.DateFormateException;
import com.tpex.admin.exception.InvalidFileException;
import com.tpex.admin.exception.InvalidInputParametersException;
import com.tpex.admin.exception.MyResourceNotFoundException;
import com.tpex.admin.repository.AddressMasterRepository;
import com.tpex.admin.repository.CarFamilyDestinationMasterRepository;
import com.tpex.admin.repository.CarFamilyMastRepository;
import com.tpex.admin.repository.CountryMasterRepository;
import com.tpex.admin.repository.CountryOriginMstRepository;
import com.tpex.admin.repository.FinalDestRepository;
import com.tpex.admin.repository.LotPartShortageRepository;
import com.tpex.admin.repository.NatCalRepository;
import com.tpex.admin.repository.OemProcessCtrlRepository;
import com.tpex.admin.repository.OemProgDtlsRepository;
import com.tpex.admin.repository.PlantMasterRepository;
import com.tpex.admin.repository.TpexConfigRepository;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.DateUtil;
import com.tpex.admin.util.DownlaodExcelTemplate;
import com.tpex.admin.util.TpexConfigurationUtil;

import io.netty.util.internal.StringUtil;
import net.sf.jasperreports.engine.JRException;

/**
 * The Class CommonUploadAndDownloadServiceImpl.
 */
@SuppressWarnings("squid:S3776")
@Service
public class CommonUploadAndDownloadServiceImpl implements CommonUploadAndDownloadService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUploadAndDownloadServiceImpl.class);

	@Autowired
	OemProcessCtrlRepository oemProcessCtrlRepository;

	@Autowired
	NatCalRepository natCalRepository;

	@Autowired
	AddressMasterRepository addressMasterRepository;

	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	OemProgDtlsRepository oemProgDtlsRepository;

	@Autowired
	FileService fileService;

	@Autowired
	CountryMasterRepository countryMasterRepository;

	@Autowired
	CountryOriginMstRepository countryOriginMstRepository;

	@Autowired
	CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;

	@Autowired
	FinalDestRepository finalDestRepository;

	@Autowired
	CarFamilyMastRepository carFamilyMastRepository;

	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;

	@Autowired
	LotPartShortageRepository lotPartShortageRepository;
	
	@Autowired
	PlantMasterRepository plantMasterRepository;

	@Autowired
	private WebClient webClient;

	@Value("${invoice.batch.service.host}")
	String invoiceBatchServiceHost;

	@Autowired
	RetryService retryService;

	@Value("${retry.flag}")
	boolean retryFlag;

	/**
	 * Fetch process details.
	 *
	 * @param userId the user id
	 * @return the list
	 */
	@Override
	public List<OemProcessCtrlEntity> fetchProcessDetails(String userId) {
		return oemProcessCtrlRepository.findByUserId(userId);
	}

	/**
	 * Download template.
	 *
	 * @param listOfReportNames the list of report names
	 * @param reportId          the report id
	 * @param sheetName         the sheet name
	 * @return the input stream
	 * @throws IOException
	 */
	@Override
	public InputStream downloadTemplate(List<ReportNameDTO> listOfReportNames, Integer reportId, String sheetName)
			throws IOException {
		List<String> listOfExcelHeaderNames = new ArrayList<>();
		List<String> listOfColumnNames = listOfReportNames.stream().filter(r -> r.getReportId().equals(reportId))
				.map(s -> s.getExcelHeaderNames()).collect(Collectors.toList());
		if (!listOfColumnNames.isEmpty()) {
			listOfExcelHeaderNames = Arrays.asList(listOfColumnNames.get(0).trim().split(","));
		}
		return DownlaodExcelTemplate.downloadTemplateAsExcel(listOfExcelHeaderNames, sheetName);
	}

	/**
	 * Download nat cal mast.
	 *
	 * @param year               the year
	 * @param loginUserId        the login user id
	 * @param isdownloadTemplate the isdownload template
	 * @return the object
	 * @throws IOException
	 * @throws JRException
	 * @throws FileNotFoundException
	 * @throws Exception             the exception
	 */
	@Override
	public Object downloadNatCalMast(String year, String loginUserId, boolean isdownloadTemplate)
			throws JRException, IOException {

		List<NatCalDTO> list = new ArrayList<>();

		if (year != null) {

			if (isdownloadTemplate) {
				LocalDate startDate = Year.of(Integer.parseInt(year)).atDay(1);
				List<LocalDate> listOfDates = startDate.datesUntil(startDate.plusMonths(12))
						.collect(Collectors.toList());

				list = getNatCalListForTemplate(listOfDates);

			} else {

				List<NatCalEntity> listOfNatCalEntity = natCalRepository.findByIdYear(Integer.parseInt(year));

				list = getNatCalList(listOfNatCalEntity);
			}
		}

		// Set configuration properties
		Map<String, Object> config = tpexConfigurationUtil.getReportConfig(loginUserId, isdownloadTemplate);

		String reportFormat = tpexConfigRepository.findByName(ConstantUtils.NATIONAL_CALENDAR_REPORT_FORMAT).getValue();
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT, reportFormat);

		return jasperReportService.getJasperReportDownloadOnline(list, reportFormat, "NationalCalendar",
				tpexConfigurationUtil.getReportDynamicPrameters(), config);
	}

	private List<NatCalDTO> getNatCalList(List<NatCalEntity> listOfNatCalEntity) {

		List<NatCalDTO> list = new ArrayList<>();

		for (NatCalEntity entity : listOfNatCalEntity) {
			NatCalDTO natCalDTO = new NatCalDTO();

			natCalDTO.setYear(entity.getId().getYear());
			natCalDTO.setMonth(entity.getId().getMonth());
			natCalDTO.setMonthName(new DateFormatSymbols().getShortMonths()[entity.getId().getMonth() - 1]);
			natCalDTO.setDay(entity.getId().getDay());

			LocalDate monthstart = LocalDate.of(entity.getId().getYear(), entity.getId().getMonth(),
					entity.getId().getDay());

			natCalDTO.setDayName(monthstart.getDayOfWeek().toString().substring(0, 3));
			natCalDTO.setWhd(entity.getWhd());
			natCalDTO.setRemarks(entity.getRemarks());

			list.add(natCalDTO);
		}

		return list;
	}

	private List<NatCalDTO> getNatCalListForTemplate(List<LocalDate> listOfDates) {

		List<NatCalDTO> list = new ArrayList<>();

		for (LocalDate date : listOfDates) {
			String dayDisplayName = date.getDayOfWeek().toString().substring(0, 3);
			NatCalDTO natCalDTO = new NatCalDTO();

			natCalDTO.setYear(date.getYear());
			natCalDTO.setMonth(date.getMonthValue());
			natCalDTO.setMonthName(new DateFormatSymbols().getShortMonths()[date.getMonthValue() - 1]);
			natCalDTO.setDay(date.getDayOfMonth());
			natCalDTO.setDayName(dayDisplayName);
			if ("SAT".equalsIgnoreCase(dayDisplayName) || "SUN".equalsIgnoreCase(dayDisplayName))
				natCalDTO.setWhd("H");
			else
				natCalDTO.setWhd("W");
			natCalDTO.setRemarks("");
			list.add(natCalDTO);
		}

		return list;
	}

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("natCaljobReceiver")
	private Job natCaljobReceiver;

	/**
	 * Job service receiving.
	 *
	 * @param batchName            the batch name
	 * @param fileName             the file name
	 * @param oemProcessCtrlEntity the oem process ctrl entity
	 * @throws Exception the exception
	 */
	@Async
	@Override
	public void jobServiceReceiving(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity) {

		JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
				.addString("batchName", batchName).addString("fileName", fileName)
				.addString("batchId", oemProcessCtrlEntity.getId().getBatchId())
				.addString("processControlId", String.valueOf(oemProcessCtrlEntity.getId().getProcessControlId()))
				.toJobParameters();
		try {
			jobLauncher.run(natCaljobReceiver, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {

			LOGGER.debug(e.getMessage());
		}
	}

	/**
	 * Save process details.
	 *
	 * @param userId the user id
	 * @return the oem process ctrl entity
	 */
	@Override
	public OemProcessCtrlEntity saveProcessDetails(String userId, String batchParameter) {

		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		OemProcessCtrlEntity entity = new OemProcessCtrlEntity();
		int idcount = oemProcessCtrlRepository.getIdOfProcessControl();
		id.setBatchId(ConstantUtils.BATCH_ID_NATCALUPLOAD);
		id.setProcessControlId(idcount);
		entity.setId(id);
		entity.setParameter(batchParameter);
		entity.setSubmitTime(new java.sql.Timestamp(System.currentTimeMillis()));
		entity.setProgramId(ConstantUtils.BATCH_ID_NATCALUPLOAD);
		entity.setStatus(2);
		entity.setUserId(userId);
		entity.setDeamon("N");
		entity.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
		oemProcessCtrlRepository.save(oemProcessCtrlRepository.save(entity));
		return entity;
	}

	/**
	 * @author Mohd.Javed method for process batch of lotPartPriceMaster
	 */
	@Override
	public OemProcessCtrlEntity saveProcessDetailsLot(String userId, String batchParameter) {

		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		OemProcessCtrlEntity entity = new OemProcessCtrlEntity();
		int idcount = oemProcessCtrlRepository.getIdOfProcessControl();
		id.setBatchId(ConstantUtils.BATCH_ID_LOTPARTPRICEMASTER);
		id.setProcessControlId(idcount);
		entity.setId(id);
		entity.setParameter(batchParameter);
		entity.setSubmitTime(new java.sql.Timestamp(System.currentTimeMillis()));
		entity.setProgramId(ConstantUtils.BATCH_ID_LOTPARTPRICEMASTER);
		entity.setStatus(2);
		entity.setUserId(userId);
		entity.setDeamon("N");
		entity.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
		oemProcessCtrlRepository.save(oemProcessCtrlRepository.save(entity));
		return entity;

	}

	/**
	 * Gets the process name details.
	 *
	 * @return the process name details
	 */
	@Override
	public Map<String, String> getProcessNameDetails() {
		List<OemProgDtlsEntity> listofOemProgDtlsEntity = oemProgDtlsRepository.findAll();
		return listofOemProgDtlsEntity.stream()
				.collect(Collectors.toMap(u -> u.getId().getProgramId(), u -> u.getProgramDesc()));
	}

	/**
	 * Gets the process name by id.
	 *
	 * @param programId the program id
	 * @return the process name by id
	 */
	@Override
	public String getProcessNameById(String programId) {
		OemProgDtlsEntity oemProgDtlsEntity = oemProgDtlsRepository.findByIdProgramId(programId);
		return oemProgDtlsEntity == null ? "" : oemProgDtlsEntity.getProgramDesc();
	}

	/**
	 * Method for download common LotPartPriceMaster
	 * 
	 * @author Mohd.Javed
	 * @throws IOException
	 * @throws JRException
	 * @throws FileNotFoundException
	 */
	@Override
	public Object downloadLotPartPriceMast(String loginUserId, boolean isdownloadTemplate)
			throws JRException, IOException {

		List<LotPartPriceMasterDto> list = new ArrayList<>();
		list.add(new LotPartPriceMasterDto());

		Map<String, Object> config = tpexConfigurationUtil.getReportConfig(loginUserId, isdownloadTemplate);

		String reportFormat = tpexConfigRepository.findByName(ConstantUtils.LOT_PARTPRICE_REPORT_FORMAT).getValue();
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT, reportFormat);

		return jasperReportService.getJasperReportDownloadOnline(list, reportFormat, "LotPartPriceMaster",
				tpexConfigurationUtil.getReportDynamicPrameters(), config);
	}

	/**
	 * @author Mohd.Javed
	 * @param batchName     BINS107
	 * @param userId
	 * @param effectiveFrom
	 * @param effectiveTo
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	@Override
	public ResponseEntity<List<ProcessBatchDTO>> batchProcess(String batchName, String userId, String effectiveFrom,
			String effectiveTo, MultipartFile file) throws IOException {

		String excelFilePath = file.getOriginalFilename();

		Workbook workbook = getWorkBook(file, excelFilePath);

		Sheet worksheet = workbook.getSheetAt(0);

		if (worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1) {
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}

		if (tpexConfigurationUtil.checkForEmptySheet(worksheet, 1)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}

		// Header row validation
		Row headerRow = worksheet.getRow(0);

		headerRow.getCell(0).getStringCellValue();

		if (headerRow.getPhysicalNumberOfCells() > 7) {
			throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
		}

		List<String> header = Arrays.asList("CURRENCY", "CFC", "IMP code", "Part .", "PART NAME", "FOB1 AMT");

		if (!tpexConfigurationUtil.checkForValidHeader(worksheet, header)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
		}

		workbook.close();

		fileService.save(file);
		StringBuilder batchParameter = new StringBuilder("Effective From - ").append(effectiveFrom)
				.append("Effective To - ").append(effectiveTo);
		OemProcessCtrlEntity oemProcessCtrlEntity = saveProcessCtrlDetail(userId, batchParameter.toString(), batchName);
		UploadPxpPriceMasterJobDto uploadPxpPriceMasterJobDto = new UploadPxpPriceMasterJobDto();
		uploadPxpPriceMasterJobDto.setBatchName(batchName);
		uploadPxpPriceMasterJobDto.setFileName(file.getOriginalFilename());
		uploadPxpPriceMasterJobDto.setEffectiveFromDate(effectiveFrom);
		uploadPxpPriceMasterJobDto.setEffectiveToDate(effectiveTo);
		uploadPxpPriceMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadPxpPriceMasterJobDto.setUserId(userId);

		webClient.post().uri(invoiceBatchServiceHost + "/invoice/uploadPxpPriceMaster")
		.bodyValue(uploadPxpPriceMasterJobDto).retrieve().bodyToMono(String.class).block();

		List<ProcessBatchDTO> listofProcessBatch = getProcessDetail(userId, batchName);
		return new ResponseEntity<>(listofProcessBatch, HttpStatus.OK);
	}

	private Workbook getWorkBook(MultipartFile file, String excelFilePath) throws IOException {

		Workbook workbook = null;

		if (excelFilePath != null && excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else if (excelFilePath != null && excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		} else {
			throw new InvalidFileException(ConstantUtils.ERR_CM_3021);
		}

		return workbook;
	}

	private List<ProcessBatchDTO> getProcessDetail(String userId, String batchName) {
		String processName = getProcessNameById(batchName);
		List<OemProcessCtrlEntity> listOfProcess = fetchProcessDetails(userId);
		return listOfProcess.stream()
				.map(p -> new ProcessBatchDTO(p.getId().getProcessControlId(), p.getId().getBatchId(), processName,
						p.getParameter(), p.getUserId(), DateUtil.convertToDatabaseColumn(p.getSubmitTime()),
						getProcessStatus(p.getStatus()), p.getErrorFilePath()))
				.collect(Collectors.toList());
	}

	private String getProcessStatus(int status) {
		if (status == 1) {
			return ConstantUtils.STS_SUCCESS;
		}
		return status == 0 ? ConstantUtils.STS_ERROR : ConstantUtils.STS_PROCESSING;
	}

	/**
	 * Method for download common PxpPartPriceMaster
	 * 
	 * @author Mohd.Javed
	 * @throws IOException
	 * @throws JRException
	 * @throws FileNotFoundException
	 */
	@Override
	public Object downloadPxpPartPriceMast(String loginUserId, boolean isdownloadTemplate)
			throws JRException, IOException {

		List<PxpPartPriceMasterDto> list = new ArrayList<>();
		list.add(new PxpPartPriceMasterDto());

		Map<String, Object> config = tpexConfigurationUtil.getReportConfig(loginUserId, isdownloadTemplate);
		String reportFormat = tpexConfigRepository.findByName(ConstantUtils.PXP_PARTPRICE_REPORT_FORMAT).getValue();
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT, reportFormat);

		return jasperReportService.getJasperReportDownloadOnline(list, reportFormat, "PxpPartPriceMaster",
				tpexConfigurationUtil.getReportDynamicPrameters(), config);

	}

	@Override
	public Object downloadAddressMaster(String cmpCode, String loginUserId, boolean isdownloadTemplate)
			throws JRException, IOException {

		List<AddressMasterDTO> list = new ArrayList<>();
		List<AddressMasterEntity> listOfAddressMasterEntity = addressMasterRepository
				.findByCmpCodeOrderByIdCodeAscIdBranchAscNameAscAddress1Asc(cmpCode);
		for (AddressMasterEntity entity : listOfAddressMasterEntity) {
			AddressMasterDTO addressMasterDTO = populateAddressMasterDTO(entity);
			list.add(addressMasterDTO);
		}

		Map<String, Object> config = tpexConfigurationUtil.getReportConfig(loginUserId, isdownloadTemplate);

		String reportFormat = tpexConfigRepository.findByName(ConstantUtils.ADDRESS_MASTER_REPORT_FORMAT).getValue();
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT, reportFormat);

		return jasperReportService.getJasperReportDownloadOnline(list, reportFormat, "AddressMaster",
				tpexConfigurationUtil.getReportDynamicPrameters(), config);
	}

	@Override
	public Object downloadAddressMasterTemplate(String loginUserId) throws JRException, IOException {

		List<AddressMasterDTO> list = new ArrayList<>();
		list.add(new AddressMasterDTO());

		Map<String, Object> config = tpexConfigurationUtil.getReportConfig(loginUserId, true);

		String reportFormat = tpexConfigRepository.findByName(ConstantUtils.ADDRESS_MASTER_REPORT_FORMAT).getValue();
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT, reportFormat);

		return jasperReportService.getJasperReportDownloadOnline(list, reportFormat, "AddressMasterTemplate",
				tpexConfigurationUtil.getReportDynamicPrameters(), config);
	}

	/**
	 * Method for download CountryofOriginMastListReport
	 * 
	 * @author Anurag Tiwari
	 */
	public byte[] getCountryofOriginMastListReport(String partPriceNo, List<String> countryOfOriginCode,
			String vanDateFrom, String vanDateTo) throws IOException, DecoderException, ParseException {

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet cOOMaster = workbook.createSheet("COO_Master");
			Sheet countryCode = workbook.createSheet("Country_Code");

			Font cooMasterFont = workbook.createFont();
			cooMasterFont.setFontName(ConstantUtils.CALIBRIFONT);
			cooMasterFont.setFontHeightInPoints((short) 12);
			cooMasterFont.setBold(false);

			CellStyle cooMasterStyle = workbook.createCellStyle();

			cooMasterStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			cooMasterStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cooMasterStyle.setBorderBottom(BorderStyle.THIN);
			cooMasterStyle.setBorderLeft(BorderStyle.THIN);
			cooMasterStyle.setBorderRight(BorderStyle.THIN);
			cooMasterStyle.setBorderTop(BorderStyle.THIN);
			cooMasterStyle.setFont(cooMasterFont);
			cooMasterStyle.setAlignment(HorizontalAlignment.CENTER);

			Font countryCodeFont = workbook.createFont();
			countryCodeFont.setFontName(ConstantUtils.CALIBRIFONT);
			countryCodeFont.setFontHeightInPoints((short) 11);
			countryCodeFont.setBold(true);

			CellStyle countryCodeStyle = workbook.createCellStyle();

			countryCodeStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			countryCodeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			countryCodeStyle.setBorderBottom(BorderStyle.THIN);
			countryCodeStyle.setBorderLeft(BorderStyle.THIN);
			countryCodeStyle.setBorderRight(BorderStyle.THIN);
			countryCodeStyle.setBorderTop(BorderStyle.THIN);
			countryCodeStyle.setFont(countryCodeFont);

			Font gridFont = workbook.createFont();
			gridFont.setFontName(ConstantUtils.CALIBRIFONT);
			gridFont.setFontHeightInPoints((short) 11);
			gridFont.setBold(false);

			CellStyle gridDataStyle = workbook.createCellStyle();

			gridDataStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			gridDataStyle.setFillPattern(FillPatternType.NO_FILL);
			gridDataStyle.setFont(gridFont);

			CreationHelper creationHelper = workbook.getCreationHelper();

			Font dateFont = workbook.createFont();
			dateFont.setFontName(ConstantUtils.CALIBRIFONT);
			dateFont.setFontHeightInPoints((short) 11);
			dateFont.setBold(false);

			CellStyle cellstyleforDate = workbook.createCellStyle();

			cellstyleforDate.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			cellstyleforDate.setFillPattern(FillPatternType.NO_FILL);
			cellstyleforDate.setDataFormat(creationHelper.createDataFormat().getFormat(ConstantUtils.POIDATEFORMATE));
			cellstyleforDate.setFont(dateFont);

			cOOMaster.setDisplayGridlines(false);
			countryCode.setDisplayGridlines(true);

			Row cOOMasterrow = cOOMaster.createRow((short) 0);

			cOOMasterrow.createCell(0).setCellValue(ConstantUtils.PART_NO);
			cOOMasterrow.getCell(0).setCellStyle(cooMasterStyle);
			cOOMasterrow.createCell(1).setCellValue(ConstantUtils.PART_NAME);
			cOOMasterrow.getCell(1).setCellStyle(cooMasterStyle);
			cOOMasterrow.createCell(2).setCellValue(ConstantUtils.COUNTRY_OF_ORIGIN_CODE_COOC);
			cOOMasterrow.getCell(2).setCellStyle(cooMasterStyle);
			cOOMasterrow.createCell(3).setCellValue(ConstantUtils.VAN_DATE_FROM);
			cOOMasterrow.getCell(3).setCellStyle(cooMasterStyle);
			cOOMasterrow.createCell(4).setCellValue(ConstantUtils.VAN_DATE_TO);
			cOOMasterrow.getCell(4).setCellStyle(cooMasterStyle);

			Row cOOMasterrow1 = cOOMaster.createRow(1);

			cOOMasterrow1.createCell(0).setCellValue(ConstantUtils.XX12);
			cOOMasterrow1.getCell(0).setCellStyle(cooMasterStyle);
			cOOMasterrow1.createCell(1).setCellValue(ConstantUtils.XX40);
			cOOMasterrow1.getCell(1).setCellStyle(cooMasterStyle);
			cOOMasterrow1.createCell(2).setCellValue(ConstantUtils.CODE_COUNTRY_NAME);
			cOOMasterrow1.getCell(2).setCellStyle(cooMasterStyle);
			cOOMasterrow1.createCell(3).setCellValue(ConstantUtils.POIDATEFORMATEFORCELL);
			cOOMasterrow1.getCell(3).setCellStyle(cooMasterStyle);
			cOOMasterrow1.createCell(4).setCellValue(ConstantUtils.POIDATEFORMATEFORCELL);
			cOOMasterrow1.getCell(4).setCellStyle(cooMasterStyle);

			int initialColumn = cOOMasterrow1.getFirstCellNum();
			int lastColumn = cOOMasterrow1.getLastCellNum() - 1;

			Row countryCoderow = countryCode.createRow((short) 0);

			countryCoderow.createCell(0).setCellValue(ConstantUtils.COUNTRY_OF_ORIGIN_CODE);
			countryCoderow.getCell(0).setCellStyle(countryCodeStyle);
			countryCoderow.createCell(1).setCellValue(ConstantUtils.COUNTRY_NAME);
			countryCoderow.getCell(1).setCellStyle(countryCodeStyle);

			SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.SIMPLEDATEFORMATOUT);

			SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.SIMPLEDATEFORMATINPUT);

			Date vanDateFrm = null;
			Date vanDateToo = null;

			if (!vanDateFrom.isBlank() && (vanDateFrom != null)) {
				vanDateFrm = Date.valueOf(outPutFormat.format(inputFormat.parse(vanDateFrom)));
			}

			if (!vanDateTo.isBlank() && (vanDateTo != null)) {
				vanDateToo = Date.valueOf(outPutFormat.format(inputFormat.parse(vanDateTo)));
			}

			if ((!StringUtils.isBlank(vanDateTo)) && (!StringUtils.isBlank(vanDateFrom))
					&& (vanDateFrm != null && vanDateFrm.compareTo(vanDateToo) > 0)) {
				throw new DateFormateException(ConstantUtils.ERR_CM_3024);
			}

			List<String[]> countryOriginMstList = getCountryOriginMstList(partPriceNo, countryOfOriginCode, vanDateFrom,
					vanDateTo, vanDateFrm, vanDateToo);

			int rowIndex = 2;
			int rowIndexInitial = 2;
			int columnIndex = 0;

			List<CountryMasterEntity> countryMasterEntity1 = countryMasterRepository.findAll();
			Map<String, String> valueMap = new HashMap<>();
			for (CountryMasterEntity obj : countryMasterEntity1) {
				valueMap.put(obj.getCountryCode(), obj.getCountryName());
			}

			for (String[] countryOriginMstListt : countryOriginMstList) {
				Row dataRow = cOOMaster.createRow(rowIndex);
				dataRow.createCell(0).setCellValue(countryOriginMstListt[0]);
				dataRow.getCell(0).setCellStyle(gridDataStyle);
				dataRow.createCell(1).setCellValue(countryOriginMstListt[1]);

				String getCodeName = countryOriginMstListt[2];

				if (valueMap.get(countryOriginMstListt[2]) != null) {
					getCodeName = countryOriginMstListt[2] + "-" + valueMap.get(countryOriginMstListt[2]);
				}

				dataRow.getCell(1).setCellStyle(gridDataStyle);
				dataRow.createCell(2).setCellValue(getCodeName);
				dataRow.getCell(2).setCellStyle(gridDataStyle);
				dataRow.createCell(3).setCellValue(
						new SimpleDateFormat(ConstantUtils.SQL_DATE_FORMAT).parse(countryOriginMstListt[3]));
				dataRow.getCell(3).setCellStyle(cellstyleforDate);
				dataRow.createCell(4).setCellValue(
						new SimpleDateFormat(ConstantUtils.SQL_DATE_FORMAT).parse(countryOriginMstListt[4]));
				dataRow.getCell(4).setCellStyle(cellstyleforDate);
				cOOMaster.autoSizeColumn(columnIndex);
				rowIndex++;
				columnIndex++;
			}

			cOOMaster.setSelected(true);

			CellRangeAddress region = new CellRangeAddress(rowIndexInitial,
					rowIndexInitial + countryOriginMstList.size(), initialColumn, lastColumn);
			RegionUtil.setBorderBottom(BorderStyle.THICK, region, cOOMaster);
			RegionUtil.setBorderLeft(BorderStyle.THICK, region, cOOMaster);
			RegionUtil.setBorderRight(BorderStyle.THICK, region, cOOMaster);
			RegionUtil.setBorderTop(BorderStyle.THICK, region, cOOMaster);

			cOOMaster.setSelected(false);

			List<CountryMasterEntity> countryMasterEntity = countryMasterRepository
					.findAll(Sort.by("countryName").ascending());

			List<String> countryNameWithCodesDto = countryMasterEntity.stream()
					.map(u -> (u.getCountryCode() + "-" + u.getCountryName())).collect(Collectors.toList());

			int countryCodeRowIndex = 1;
			int countryCodeColumnIndex = 0;

			for (CountryMasterEntity countryMasterEntityy : countryMasterEntity) {

				Row countryCodeDataRow = countryCode.createRow(countryCodeRowIndex);

				countryCodeDataRow.createCell(0).setCellValue(countryMasterEntityy.getCountryCode());
				countryCodeDataRow.getCell(0).setCellStyle(gridDataStyle);
				countryCodeDataRow.createCell(1).setCellValue(countryMasterEntityy.getCountryName());
				countryCodeDataRow.getCell(1).setCellStyle(gridDataStyle);
				countryCode.autoSizeColumn(countryCodeColumnIndex);
				countryCodeRowIndex++;
				countryCodeColumnIndex++;
			}

			cOOMaster.setSelected(true);

			XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) countryCode);
			XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
					.createExplicitListConstraint(countryNameWithCodesDto.toArray(new String[0]));

			int cOOMasterColumnValue = 2;
			CellRangeAddressList addressList = new CellRangeAddressList(cOOMaster.getFirstRowNum() + 2,
					countryOriginMstList.size() + 1, cOOMasterColumnValue, cOOMasterColumnValue);
			XSSFDataValidation datavalidation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint,
					addressList);
			datavalidation.setShowErrorBox(true);
			cOOMaster.addValidationData(datavalidation);

			cOOMaster.autoSizeColumn(0);
			cOOMaster.autoSizeColumn(1);
			cOOMaster.autoSizeColumn(2);
			cOOMaster.autoSizeColumn(3);
			cOOMaster.autoSizeColumn(4);

			cOOMaster.setSelected(false);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			return bos.toByteArray();
		}

	}

	private List<String[]> getCountryOriginMstList(String partPriceNo, List<String> countryOfOriginCode,
			String vanDateFrom, String vanDateTo, Date vanDateFrm, Date vanDateToo) {

		List<String[]> countryOriginMstList;

		if (!StringUtils.isBlank(vanDateFrom)
				&& (!StringUtils.isBlank(vanDateTo) && countryOfOriginCode != null && partPriceNo != null)) {
			countryOriginMstList = countryOriginMstRepository.getPartNameDetails(partPriceNo, countryOfOriginCode);
		} else if (StringUtils.isBlank(vanDateFrom) && !StringUtils.isBlank(vanDateTo) && countryOfOriginCode != null
				&& countryOfOriginCode.isEmpty() && StringUtils.isBlank(partPriceNo)) {
			countryOriginMstList = countryOriginMstRepository.getPartNameDetailsForVanDateTo(vanDateToo);
		} else if (StringUtils.isBlank(vanDateTo) && !StringUtils.isBlank(vanDateFrom) && countryOfOriginCode != null
				&& countryOfOriginCode.isEmpty() && StringUtils.isBlank(partPriceNo)) {
			countryOriginMstList = countryOriginMstRepository.getPartNameDetailsForVanDateFrom(vanDateFrm);
		} else if (!StringUtils.isBlank(partPriceNo) && StringUtils.isBlank(vanDateFrom)
				&& StringUtils.isBlank(vanDateTo) && countryOfOriginCode != null && countryOfOriginCode.isEmpty()) {
			countryOriginMstList = countryOriginMstRepository.getPartNameDetailsForPartNo(partPriceNo);
		} else if (StringUtils.isBlank(vanDateFrom) && StringUtils.isBlank(vanDateTo)
				&& StringUtils.isBlank(partPriceNo) && countryOfOriginCode != null && !countryOfOriginCode.isEmpty()) {
			countryOriginMstList = countryOriginMstRepository.getPartNameDetailsForCountryCode(countryOfOriginCode);
		} else if (StringUtils.isBlank(vanDateFrom) && StringUtils.isBlank(vanDateTo)
				&& StringUtils.isBlank(partPriceNo) && countryOfOriginCode != null && countryOfOriginCode.isEmpty()) {
			countryOriginMstList = countryOriginMstRepository.getPartNameDetailsForAllData();
		} else {
			countryOriginMstList = countryOriginMstRepository.getPartNameDetailsForOtherCase(partPriceNo,
					countryOfOriginCode, vanDateFrm, vanDateToo);
		}

		if (countryOriginMstList.isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		return countryOriginMstList;
	}

	/**
	 * @param entity
	 * @return
	 */
	private AddressMasterDTO populateAddressMasterDTO(AddressMasterEntity entity) {
		AddressMasterDTO addressMasterDTO = new AddressMasterDTO();
		addressMasterDTO.setCode(entity.getId().getCode());
		addressMasterDTO.setCmpCode(entity.getCmpCode());
		addressMasterDTO.setBranch(entity.getId().getBranch());
		addressMasterDTO.setName(entity.getName());
		addressMasterDTO.setAddress1(entity.getAddress1());
		addressMasterDTO.setAddress2(entity.getAddress2());
		addressMasterDTO.setAddress3(entity.getAddress3());
		addressMasterDTO.setAddress4(entity.getAddress4());
		addressMasterDTO.setZip(entity.getZip());
		addressMasterDTO.setShortName(entity.getShortName());
		addressMasterDTO.setSapCode(entity.getSapCode());
		addressMasterDTO.setConsigneeCode(entity.getConsigneeCode());
		addressMasterDTO.setCountryCode(entity.getCountryCode());
		addressMasterDTO.setTelephoneNumber(entity.getTelephoneNumber());
		addressMasterDTO.setFaxNumber(entity.getFaxNumber());
		addressMasterDTO.setEmail(entity.getEmail());
		addressMasterDTO.setTelexNumber(entity.getTelexNumber());
		addressMasterDTO.setContactPerson(entity.getContactPerson());
		addressMasterDTO.setInvoiceFlag(entity.getInvoiceFlag());
		addressMasterDTO.setScFlag(entity.getScFlag());
		addressMasterDTO.setScRemarks(entity.getScRemarks());
		addressMasterDTO.setEmployee1(entity.getEmployee1());
		addressMasterDTO.setEmployee2(entity.getEmployee2());
		addressMasterDTO.setEmployee3(entity.getEmployee3());
		addressMasterDTO.setCompany(entity.getId().getCompany());
		addressMasterDTO.setUpdateBy(entity.getUpdateBy());
		addressMasterDTO.setUpdateDate(entity.getUpdateDate());
		return addressMasterDTO;
	}

	@Override
	public OemProcessCtrlEntity saveProcessCtrlDetail(String userId, String batchParameter, String batchId) {

		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity
		.setId(new OemProcessCtrlIdEntity(batchId, oemProcessCtrlRepository.getIdOfProcessControl()));
		oemProcessCtrlEntity.setParameter(batchParameter);
		oemProcessCtrlEntity.setSubmitTime(new java.sql.Timestamp(System.currentTimeMillis()));
		oemProcessCtrlEntity.setProgramId(batchId);
		oemProcessCtrlEntity.setStatus(2);
		oemProcessCtrlEntity.setUserId(userId);
		oemProcessCtrlEntity.setDeamon("N");
		oemProcessCtrlEntity.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
		oemProcessCtrlEntity.setProgramName(getProcessNameById(batchId));
		oemProcessCtrlEntity.setSystemName(ConstantUtils.SYSTEM_NAME);
		oemProcessCtrlRepository.save(oemProcessCtrlRepository.save(oemProcessCtrlEntity));
		return oemProcessCtrlEntity;
	}

	@Autowired
	private AddressMasterService addressMasterService;

	@Override
	public void uploadAddressMaster(String batchName, String originalFilename,
			OemProcessCtrlEntity oemProcessCtrlEntity, String companyCode, String userId) {
		addressMasterService.addressMasterUploadBatchJob(batchName, originalFilename, oemProcessCtrlEntity, companyCode,
				userId);
	}

	public byte[] getCarFmlyDestnMstListReport(HttpServletResponse response, String cmpCode, String userId)
			throws IOException, DecoderException, ParseException {

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet carFmlyDestMaster = workbook.createSheet("Car Family Destination");
			Sheet carFmlyCode = workbook.createSheet("CFC");
			Sheet destination = workbook.createSheet("Destination");

			String headerRgbS = ConstantUtils.CAR_FMLY_DEST_HEADER_COLOR_CODE;
			byte[] headerRgbB = Hex.decodeHex(headerRgbS);
			XSSFColor headerColor = new XSSFColor(headerRgbB, null);

			String headerBorderRgbS = ConstantUtils.CAR_FMLY_DEST_HEADER_BORDER_COLOR_CODE;
			byte[] headerBorderRgbB = Hex.decodeHex(headerBorderRgbS);
			XSSFColor headerBorderColor = new XSSFColor(headerBorderRgbB, null);

			Font carFmlyDestMasterFont = workbook.createFont();
			carFmlyDestMasterFont.setFontName(ConstantUtils.ARIAL);
			carFmlyDestMasterFont.setFontHeightInPoints((short) 10);
			carFmlyDestMasterFont.setBold(true);

			XSSFCellStyle carFmlyDestMasterStyle = (XSSFCellStyle) workbook.createCellStyle();
			carFmlyDestMasterStyle.setFillForegroundColor(headerColor);
			carFmlyDestMasterStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			carFmlyDestMasterStyle.setBorderBottom(BorderStyle.THICK);
			carFmlyDestMasterStyle.setBorderLeft(BorderStyle.THICK);
			carFmlyDestMasterStyle.setBorderRight(BorderStyle.THICK);
			carFmlyDestMasterStyle.setBorderTop(BorderStyle.THICK);
			carFmlyDestMasterStyle.setFont(carFmlyDestMasterFont);
			carFmlyDestMasterStyle.setAlignment(HorizontalAlignment.CENTER);
			carFmlyDestMasterStyle.setBorderColor(BorderSide.RIGHT, headerBorderColor);
			carFmlyDestMasterStyle.setBorderColor(BorderSide.LEFT, headerBorderColor);
			carFmlyDestMasterStyle.setBorderColor(BorderSide.TOP, headerBorderColor);
			carFmlyDestMasterStyle.setBorderColor(BorderSide.BOTTOM, headerBorderColor);

			Font carFmlyCodeFont = workbook.createFont();
			carFmlyCodeFont.setFontName(ConstantUtils.ARIAL);
			carFmlyCodeFont.setFontHeightInPoints((short) 10);
			carFmlyCodeFont.setBold(true);

			XSSFCellStyle carFmlyCodeStyle = (XSSFCellStyle) workbook.createCellStyle();

			carFmlyCodeStyle.setFillForegroundColor(headerColor);
			carFmlyCodeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			carFmlyCodeStyle.setBorderBottom(BorderStyle.THICK);
			carFmlyCodeStyle.setBorderLeft(BorderStyle.THICK);
			carFmlyCodeStyle.setBorderRight(BorderStyle.THICK);
			carFmlyCodeStyle.setBorderTop(BorderStyle.THICK);
			carFmlyCodeStyle.setFont(carFmlyCodeFont);
			carFmlyCodeStyle.setBorderColor(BorderSide.RIGHT, headerBorderColor);
			carFmlyCodeStyle.setBorderColor(BorderSide.LEFT, headerBorderColor);
			carFmlyCodeStyle.setBorderColor(BorderSide.TOP, headerBorderColor);
			carFmlyCodeStyle.setBorderColor(BorderSide.BOTTOM, headerBorderColor);

			Font destinationFont = workbook.createFont();
			destinationFont.setFontName(ConstantUtils.ARIAL);
			destinationFont.setFontHeightInPoints((short) 10);
			destinationFont.setBold(true);

			XSSFCellStyle destinationStyle = (XSSFCellStyle) workbook.createCellStyle();

			destinationStyle.setFillForegroundColor(headerColor);
			destinationStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			destinationStyle.setBorderBottom(BorderStyle.THICK);
			destinationStyle.setBorderLeft(BorderStyle.THICK);
			destinationStyle.setBorderRight(BorderStyle.THICK);
			destinationStyle.setBorderTop(BorderStyle.THICK);
			destinationStyle.setFont(carFmlyCodeFont);
			destinationStyle.setBorderColor(BorderSide.RIGHT, headerBorderColor);
			destinationStyle.setBorderColor(BorderSide.LEFT, headerBorderColor);
			destinationStyle.setBorderColor(BorderSide.TOP, headerBorderColor);
			destinationStyle.setBorderColor(BorderSide.BOTTOM, headerBorderColor);

			Font gridFont = workbook.createFont();
			gridFont.setFontName(ConstantUtils.ARIAL);
			gridFont.setFontHeightInPoints((short) 10);
			gridFont.setBold(false);

			String gridRightBorderRgbS = ConstantUtils.CAR_FMLY_DEST_GRID_RIGHT_COLOR_CODE;
			byte[] gridRightBorderRgbB = Hex.decodeHex(gridRightBorderRgbS);
			XSSFColor gridRightBorderColor = new XSSFColor(gridRightBorderRgbB, null);

			String gridBottomBorderRgbS = ConstantUtils.CAR_FMLY_DEST_GRID_BOTTOM_COLOR_CODE;
			byte[] gridBottomBorderRgbB = Hex.decodeHex(gridBottomBorderRgbS);
			XSSFColor gridBottomBorderColor = new XSSFColor(gridBottomBorderRgbB, null);

			XSSFCellStyle gridDataStyle = (XSSFCellStyle) workbook.createCellStyle();
			gridDataStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			gridDataStyle.setFillPattern(FillPatternType.NO_FILL);
			gridDataStyle.setFont(gridFont);
			gridDataStyle.setBorderRight(BorderStyle.MEDIUM);
			gridDataStyle.setBorderColor(BorderSide.RIGHT, gridRightBorderColor);
			gridDataStyle.setBorderBottom(BorderStyle.MEDIUM);
			gridDataStyle.setBorderColor(BorderSide.BOTTOM, gridBottomBorderColor);

			carFmlyDestMaster.setDisplayGridlines(true);
			carFmlyCode.setDisplayGridlines(true);
			destination.setDisplayGridlines(true);

			Row carFmlyDestMasterrow = carFmlyDestMaster.createRow((short) 0);
			carFmlyDestMasterrow.createCell(0).setCellValue(ConstantUtils.CAR_FAMILY_CODE);
			carFmlyDestMasterrow.getCell(0).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow.createCell(1).setCellValue(ConstantUtils.DESTINATION);
			carFmlyDestMasterrow.getCell(1).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow.createCell(2).setCellValue(ConstantUtils.RE_EXPORTER_CODE);
			carFmlyDestMasterrow.getCell(2).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow.createCell(3).setCellValue(ConstantUtils.SERIES_NAME);
			carFmlyDestMasterrow.getCell(3).setCellStyle(carFmlyDestMasterStyle);

			Row carFmlyDestMasterrow1 = carFmlyDestMaster.createRow(1);
			carFmlyDestMasterrow1.createCell(0).setCellValue(ConstantUtils.CFC);
			carFmlyDestMasterrow1.getCell(0).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow1.createCell(1).setCellValue(ConstantUtils.DESTINATION_1);
			carFmlyDestMasterrow1.getCell(1).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow1.createCell(2).setCellValue(ConstantUtils.XX1);
			carFmlyDestMasterrow1.getCell(2).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow1.createCell(3).setCellValue(ConstantUtils.XX15);
			carFmlyDestMasterrow1.getCell(3).setCellStyle(carFmlyDestMasterStyle);

			Row carFmlyCoderow = carFmlyCode.createRow((short) 0);
			carFmlyCoderow.createCell(0).setCellValue(ConstantUtils.CAR_FAMILY_CODE_1);
			carFmlyCoderow.getCell(0).setCellStyle(carFmlyCodeStyle);
			carFmlyCoderow.createCell(1).setCellValue(ConstantUtils.SERIES_NAME_1);
			carFmlyCoderow.getCell(1).setCellStyle(carFmlyCodeStyle);

			Row destinationrow = destination.createRow((short) 0);
			destinationrow.createCell(0).setCellValue(ConstantUtils.DESTINATION_CODE);
			destinationrow.getCell(0).setCellStyle(destinationStyle);
			destinationrow.createCell(1).setCellValue(ConstantUtils.DESTINATION_NAME);
			destinationrow.getCell(1).setCellStyle(destinationStyle);

			List<CarFamilyDestinationMasterEntity> carFmlyDestMstList = getCarFmlyDestList(cmpCode);

			int rowIndex = 2;
			int columnIndex = 0;

			for (CarFamilyDestinationMasterEntity carFmlyDestMstDetail : carFmlyDestMstList) {
				Row dataRow = carFmlyDestMaster.createRow(rowIndex);
				dataRow.createCell(0).setCellValue(carFmlyDestMstDetail.getId().getCarFmlyCode());
				dataRow.getCell(0).setCellStyle(gridDataStyle);

				String destCode = carFmlyDestMstDetail.getId().getDestinationCode();
				FinalDestEntity destEntity = finalDestRepository.findAllByDestinationCd(destCode);
				String destCodeAndName = "";
				destCodeAndName = destCode;
				if (destEntity != null) {
					String destName = destEntity.getDestinationName();
					destCodeAndName = destCode + "-" + destName;
				}
				dataRow.createCell(1).setCellValue(destCodeAndName);
				dataRow.getCell(1).setCellStyle(gridDataStyle);
				dataRow.createCell(2).setCellValue(carFmlyDestMstDetail.getId().getReExporterCode());
				dataRow.getCell(2).setCellStyle(gridDataStyle);
				dataRow.createCell(3).setCellValue(carFmlyDestMstDetail.getSrsName());
				dataRow.getCell(3).setCellStyle(gridDataStyle);

				carFmlyDestMaster.autoSizeColumn(columnIndex);
				rowIndex++;
				columnIndex++;
			}

			List<CarFamilyMasterEntity> carFamilyMasterEntity = carFamilyMastRepository
					.findAllByOrderByCarFmlyCodeAsc();

			List<String> carFmlyCodes = null;

			int carFmlyCodeRowIndex = 1;
			int carFmlyCodeColumnIndex = 0;

			if (!carFamilyMasterEntity.isEmpty()) {

				carFmlyCodes = carFamilyMasterEntity.stream().map(u -> (u.getCarFmlyCode()))
						.collect(Collectors.toList());

				for (CarFamilyMasterEntity cfmEntity : carFamilyMasterEntity) {

					Row carFmlyCodeDataRow = carFmlyCode.createRow(carFmlyCodeRowIndex);

					carFmlyCodeDataRow.createCell(0).setCellValue(cfmEntity.getCarFmlyCode());
					carFmlyCodeDataRow.getCell(0).setCellStyle(gridDataStyle);
					carFmlyCodeDataRow.createCell(1).setCellValue(cfmEntity.getCarFmlySrsName());
					carFmlyCodeDataRow.getCell(1).setCellStyle(gridDataStyle);
					carFmlyCode.autoSizeColumn(carFmlyCodeColumnIndex);
					carFmlyCodeRowIndex++;
					carFmlyCodeColumnIndex++;
				}
			}

			List<FinalDestEntity> finalDestEntity = finalDestRepository.findAllByOrderByDestinationCdAsc();

			List<String> destinationNameWithCodes = null;

			int destinationRowIndex = 1;
			int destinationColumnIndex = 0;

			if (!finalDestEntity.isEmpty()) {

				destinationNameWithCodes = finalDestEntity.stream()
						.map(u -> (u.getDestinationCd() + "-" + u.getDestinationName())).collect(Collectors.toList());

				for (FinalDestEntity destEntity : finalDestEntity) {

					Row destinationDataRow = destination.createRow(destinationRowIndex);

					destinationDataRow.createCell(0).setCellValue(destEntity.getDestinationCd());
					destinationDataRow.getCell(0).setCellStyle(gridDataStyle);
					destinationDataRow.createCell(1).setCellValue(destEntity.getDestinationName());
					destinationDataRow.getCell(1).setCellStyle(gridDataStyle);
					destination.autoSizeColumn(destinationColumnIndex);
					destinationRowIndex++;
					destinationColumnIndex++;
				}
			}

			carFmlyDestMaster.setSelected(true);

			Sheet cfcHidden = workbook.createSheet("CFChidden");

			Name namedCell = workbook.createName();
			namedCell.setNameName(ConstantUtils.CFC_HIDDEN_SHEET_NAME);
			namedCell.setRefersToFormula(ConstantUtils.CAR_FMLY_CODE_FORMULA + 1);

			if (carFmlyCodes != null) {
				for (int i = 0, length = carFmlyCodes.size(); i < length; i++) {
					String name = carFmlyCodes.get(i);
					Row row = cfcHidden.createRow(i);
					Cell cell = row.createCell(0);
					cell.setCellValue(name);
				}
				namedCell.setRefersToFormula(ConstantUtils.CAR_FMLY_CODE_FORMULA + carFmlyCodes.size());
			}

			XSSFDataValidationHelper cfcDvHelper = new XSSFDataValidationHelper((XSSFSheet) cfcHidden);
			XSSFDataValidationConstraint cfcConstraint = (XSSFDataValidationConstraint) cfcDvHelper
					.createFormulaListConstraint(ConstantUtils.CFC_HIDDEN_SHEET_NAME);

			int carFmlyDestMasterColumnValue = 0;
			CellRangeAddressList cfcAddressList = new CellRangeAddressList(carFmlyDestMaster.getFirstRowNum() + 2,
					carFmlyDestMstList.size() + 1, carFmlyDestMasterColumnValue, carFmlyDestMasterColumnValue);
			XSSFDataValidation cfcDatavalidation = (XSSFDataValidation) cfcDvHelper.createValidation(cfcConstraint,
					cfcAddressList);
			cfcDatavalidation.setShowErrorBox(true);
			carFmlyDestMaster.addValidationData(cfcDatavalidation);

			workbook.setSheetVisibility(3, SheetVisibility.VERY_HIDDEN);

			Sheet destHidden = workbook.createSheet("Desthidden");

			namedCell = workbook.createName();
			namedCell.setNameName(ConstantUtils.DESTINATION_HIDDEN_SHEET_NAME);
			namedCell.setRefersToFormula(ConstantUtils.DESTINATION_FORMULA + 1);

			if (destinationNameWithCodes != null) {
				for (int i = 0, length = destinationNameWithCodes.size(); i < length; i++) {
					String name = destinationNameWithCodes.get(i);
					Row row = destHidden.createRow(i);
					Cell cell = row.createCell(0);
					cell.setCellValue(name);
				}
				namedCell.setRefersToFormula(ConstantUtils.DESTINATION_FORMULA + destinationNameWithCodes.size());
			}

			XSSFDataValidationHelper destDvHelper = new XSSFDataValidationHelper((XSSFSheet) destHidden);
			XSSFDataValidationConstraint destConstraint = (XSSFDataValidationConstraint) destDvHelper
					.createFormulaListConstraint(ConstantUtils.DESTINATION_HIDDEN_SHEET_NAME);

			carFmlyDestMasterColumnValue = 1;
			CellRangeAddressList destAddressList = new CellRangeAddressList(carFmlyDestMaster.getFirstRowNum() + 2,
					carFmlyDestMstList.size() + 1, carFmlyDestMasterColumnValue, carFmlyDestMasterColumnValue);
			XSSFDataValidation destDatavalidation = (XSSFDataValidation) destDvHelper.createValidation(destConstraint,
					destAddressList);
			destDatavalidation.setShowErrorBox(true);
			carFmlyDestMaster.addValidationData(destDatavalidation);

			workbook.setSheetVisibility(4, SheetVisibility.VERY_HIDDEN);

			carFmlyDestMaster.autoSizeColumn(0);
			carFmlyDestMaster.autoSizeColumn(1);
			carFmlyDestMaster.autoSizeColumn(2);
			carFmlyDestMaster.autoSizeColumn(3);

			carFmlyDestMaster.setSelected(false);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			return bos.toByteArray();
		}

	}

	private List<CarFamilyDestinationMasterEntity> getCarFmlyDestList(String cmpCode) {
		List<CarFamilyDestinationMasterEntity> carFmlyDestMstList = carFamilyDestinationMasterRepository
				.findAllByCompanyCode(cmpCode);
		if (carFmlyDestMstList.isEmpty()) {
			throw new InvalidInputParametersException(ConstantUtils.INFO_CM_3001);
		}
		return carFmlyDestMstList;
	}

	@Override
	public List<ProcessBatchDTO> uplaodCarFamilyDestinationMaster(String batchName, MultipartFile file,
			String companyCode, String userId) throws IOException {
		if (batchName.isBlank() || file.isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}

		String filename = file.getOriginalFilename();
		if (filename != null && (!(filename.endsWith(".xls") || filename.endsWith(".xlsx")))) {
			throw new InvalidFileException(ConstantUtils.ERR_CM_3021);
		}

		String excelFilePath = file.getOriginalFilename();

		Workbook workbook = getWorkBook(file, excelFilePath);

		Sheet worksheet = workbook.getSheetAt(0);

		List<String> header = Arrays.asList("*Car Family Code", "*Destination", "*Re Exp. Code", "*Series Name");

		if (worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1
				|| !tpexConfigurationUtil.checkForValidHeader(worksheet, header)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
		}

		if (tpexConfigurationUtil.checkForEmptySheet(worksheet, 2)) {
			workbook.close();
			throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
		}

		workbook.close();

		// Save file
		fileService.save(file);
		OemProcessCtrlEntity oemProcessCtrlEntity = saveProcessCtrlDetail(userId, null, batchName);
		UploadAddressMasterJobDto uploadAddressMasterJobDto = new UploadAddressMasterJobDto();
		uploadAddressMasterJobDto.setBatchName(batchName);
		uploadAddressMasterJobDto.setFileName(file.getOriginalFilename());
		uploadAddressMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadAddressMasterJobDto.setCompanyCode(companyCode);
		uploadAddressMasterJobDto.setUserId(userId);

		webClient.post().uri(invoiceBatchServiceHost + "/invoice/uplaodCarFamilyDstMaster")
		.bodyValue(uploadAddressMasterJobDto).retrieve().bodyToMono(String.class).block();

		return getProcessDetails(userId);

	}

	private List<ProcessBatchDTO> getProcessDetails(String userId) {
		List<OemProcessCtrlEntity> listOfProcess = fetchProcessDetails(userId);
		return listOfProcess.stream()
				.map(p -> new ProcessBatchDTO(p.getId().getProcessControlId(), p.getId().getBatchId(),
						getProcessNameById(p.getId().getBatchId()), p.getParameter(), p.getUserId(),
						DateUtil.convertToDatabaseColumn(p.getSubmitTime()), getProcessStatus(p.getStatus()),
						p.getErrorFilePath()))
				.collect(Collectors.toList());
	}

	@Override
	public byte[] downloadCarFamilyDestinationMaster(String loginUserId) throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet carFmlyDestMaster = workbook.createSheet("Car Family Destination");
			Sheet carFmlyCode = workbook.createSheet("CFC");
			Sheet destination = workbook.createSheet("Destination");

			Font carFmlyDestMasterFont = workbook.createFont();
			carFmlyDestMasterFont.setFontName(ConstantUtils.CALIBRIFONT);
			carFmlyDestMasterFont.setFontHeightInPoints((short) 12);
			carFmlyDestMasterFont.setBold(false);

			CellStyle carFmlyDestMasterStyle = workbook.createCellStyle();

			carFmlyDestMasterStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			carFmlyDestMasterStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			carFmlyDestMasterStyle.setBorderBottom(BorderStyle.THIN);
			carFmlyDestMasterStyle.setBorderLeft(BorderStyle.THIN);
			carFmlyDestMasterStyle.setBorderRight(BorderStyle.THIN);
			carFmlyDestMasterStyle.setBorderTop(BorderStyle.THIN);
			carFmlyDestMasterStyle.setFont(carFmlyDestMasterFont);
			carFmlyDestMasterStyle.setAlignment(HorizontalAlignment.CENTER);

			Font carFmlyCodeFont = workbook.createFont();
			carFmlyCodeFont.setFontName(ConstantUtils.CALIBRIFONT);
			carFmlyCodeFont.setFontHeightInPoints((short) 11);
			carFmlyCodeFont.setBold(true);

			CellStyle carFmlyCodeStyle = workbook.createCellStyle();

			carFmlyCodeStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			carFmlyCodeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			carFmlyCodeStyle.setBorderBottom(BorderStyle.THIN);
			carFmlyCodeStyle.setBorderLeft(BorderStyle.THIN);
			carFmlyCodeStyle.setBorderRight(BorderStyle.THIN);
			carFmlyCodeStyle.setBorderTop(BorderStyle.THIN);
			carFmlyCodeStyle.setFont(carFmlyCodeFont);

			Font destinationFont = workbook.createFont();
			destinationFont.setFontName(ConstantUtils.CALIBRIFONT);
			destinationFont.setFontHeightInPoints((short) 11);
			destinationFont.setBold(true);

			CellStyle destinationStyle = workbook.createCellStyle();

			destinationStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			destinationStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			destinationStyle.setBorderBottom(BorderStyle.THIN);
			destinationStyle.setBorderLeft(BorderStyle.THIN);
			destinationStyle.setBorderRight(BorderStyle.THIN);
			destinationStyle.setBorderTop(BorderStyle.THIN);
			destinationStyle.setFont(carFmlyCodeFont);

			Font gridFont = workbook.createFont();
			gridFont.setFontName(ConstantUtils.CALIBRIFONT);
			gridFont.setFontHeightInPoints((short) 11);
			gridFont.setBold(false);

			CellStyle gridDataStyle = workbook.createCellStyle();

			gridDataStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			gridDataStyle.setFillPattern(FillPatternType.NO_FILL);
			gridDataStyle.setFont(gridFont);

			carFmlyDestMaster.setDisplayGridlines(true);
			carFmlyCode.setDisplayGridlines(true);
			destination.setDisplayGridlines(true);

			Row carFmlyDestMasterrow = carFmlyDestMaster.createRow((short) 0);

			carFmlyDestMasterrow.createCell(0).setCellValue(ConstantUtils.CAR_FAMILY_CODE);
			carFmlyDestMasterrow.getCell(0).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow.createCell(1).setCellValue(ConstantUtils.DESTINATION);
			carFmlyDestMasterrow.getCell(1).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow.createCell(2).setCellValue(ConstantUtils.RE_EXPORTER_CODE);
			carFmlyDestMasterrow.getCell(2).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow.createCell(3).setCellValue(ConstantUtils.SERIES_NAME);
			carFmlyDestMasterrow.getCell(3).setCellStyle(carFmlyDestMasterStyle);

			Row carFmlyDestMasterrow1 = carFmlyDestMaster.createRow(1);

			carFmlyDestMasterrow1.createCell(0).setCellValue(ConstantUtils.CFC);
			carFmlyDestMasterrow1.getCell(0).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow1.createCell(1).setCellValue(ConstantUtils.DESTINATION_1);
			carFmlyDestMasterrow1.getCell(1).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow1.createCell(2).setCellValue(ConstantUtils.XX1);
			carFmlyDestMasterrow1.getCell(2).setCellStyle(carFmlyDestMasterStyle);
			carFmlyDestMasterrow1.createCell(3).setCellValue(ConstantUtils.XX15);
			carFmlyDestMasterrow1.getCell(3).setCellStyle(carFmlyDestMasterStyle);

			Row carFmlyCoderow = carFmlyCode.createRow((short) 0);
			carFmlyCoderow.createCell(0).setCellValue(ConstantUtils.CAR_FAMILY_CODE_1);
			carFmlyCoderow.getCell(0).setCellStyle(carFmlyCodeStyle);
			carFmlyCoderow.createCell(1).setCellValue(ConstantUtils.SERIES_NAME_1);
			carFmlyCoderow.getCell(1).setCellStyle(carFmlyCodeStyle);

			Row destinationrow = destination.createRow((short) 0);
			destinationrow.createCell(0).setCellValue(ConstantUtils.DESTINATION_CODE);
			destinationrow.getCell(0).setCellStyle(destinationStyle);
			destinationrow.createCell(1).setCellValue(ConstantUtils.DESTINATION_NAME);
			destinationrow.getCell(1).setCellStyle(destinationStyle);

			List<CarFamilyMasterEntity> carFamilyMasterEntity = carFamilyMastRepository
					.findAllByOrderByCarFmlyCodeAsc();
			List<String> carFmlyCodes = carFamilyMasterEntity.stream().map(u -> (u.getCarFmlyCode()))
					.collect(Collectors.toList());

			List<FinalDestEntity> finalDestEntity = finalDestRepository.findAllByOrderByDestinationCdAsc();
			List<String> destinationNameWithCodes = finalDestEntity.stream()
					.map(u -> (u.getDestinationCd() + "-" + u.getDestinationName())).collect(Collectors.toList());

			carFmlyDestMaster.setSelected(true);

			int carFmlyCodeRowIndex = 1;
			int carFmlyCodeColumnIndex = 0;

			for (CarFamilyMasterEntity cfmEntity : carFamilyMasterEntity) {
				Row carFmlyCodeDataRow = carFmlyCode.createRow(carFmlyCodeRowIndex);
				carFmlyCodeDataRow.createCell(0).setCellValue(cfmEntity.getCarFmlyCode());
				carFmlyCodeDataRow.getCell(0).setCellStyle(gridDataStyle);
				carFmlyCodeDataRow.createCell(1).setCellValue(cfmEntity.getCarFmlySrsName());
				carFmlyCodeDataRow.getCell(1).setCellStyle(gridDataStyle);
				carFmlyCode.autoSizeColumn(carFmlyCodeColumnIndex);
				carFmlyCodeRowIndex++;
				carFmlyCodeColumnIndex++;
			}

			int destinationRowIndex = 1;
			int destinationColumnIndex = 0;

			for (FinalDestEntity destEntity : finalDestEntity) {
				Row destinationDataRow = destination.createRow(destinationRowIndex);
				destinationDataRow.createCell(0).setCellValue(destEntity.getDestinationCd());
				destinationDataRow.getCell(0).setCellStyle(gridDataStyle);
				destinationDataRow.createCell(1).setCellValue(destEntity.getDestinationName());
				destinationDataRow.getCell(1).setCellStyle(gridDataStyle);
				destination.autoSizeColumn(destinationColumnIndex);
				destinationRowIndex++;
				destinationColumnIndex++;
			}

			carFmlyDestMaster.setSelected(true);

			Sheet cfcHidden = workbook.createSheet("CFChidden");
			for (int i = 0, length = carFmlyCodes.size(); i < length; i++) {
				String name = carFmlyCodes.get(i);
				Row row = cfcHidden.createRow(i);
				Cell cell = row.createCell(0);
				cell.setCellValue(name);
			}

			Name namedCell = workbook.createName();
			namedCell.setNameName("cfcHidden");
			namedCell.setRefersToFormula("cfcHidden!$A$1:$A$" + carFmlyCodes.size());
			XSSFDataValidationHelper cfcDvHelper = new XSSFDataValidationHelper((XSSFSheet) cfcHidden);
			XSSFDataValidationConstraint cfcConstraint = (XSSFDataValidationConstraint) cfcDvHelper
					.createFormulaListConstraint("cfcHidden");
			workbook.setSheetVisibility(3, SheetVisibility.VERY_HIDDEN);

			int carFmlyDestMasterColumnValue = 0;
			CellRangeAddressList cfcAddressList = new CellRangeAddressList(carFmlyDestMaster.getFirstRowNum() + 2, 10,
					carFmlyDestMasterColumnValue, carFmlyDestMasterColumnValue);
			XSSFDataValidation cfcDatavalidation = (XSSFDataValidation) cfcDvHelper.createValidation(cfcConstraint,
					cfcAddressList);
			cfcDatavalidation.setShowErrorBox(true);
			carFmlyDestMaster.addValidationData(cfcDatavalidation);

			Sheet destHidden = workbook.createSheet("Desthidden");
			for (int i = 0, length = destinationNameWithCodes.size(); i < length; i++) {
				String name = destinationNameWithCodes.get(i);
				Row row = destHidden.createRow(i);
				Cell cell = row.createCell(0);
				cell.setCellValue(name);
			}

			namedCell = workbook.createName();
			namedCell.setNameName("destHidden");
			namedCell.setRefersToFormula("destHidden!$A$1:$A$" + destinationNameWithCodes.size());
			XSSFDataValidationHelper destDvHelper = new XSSFDataValidationHelper((XSSFSheet) destHidden);
			XSSFDataValidationConstraint destConstraint = (XSSFDataValidationConstraint) destDvHelper
					.createFormulaListConstraint("destHidden");
			workbook.setSheetVisibility(4, SheetVisibility.VERY_HIDDEN);

			carFmlyDestMasterColumnValue = 1;
			CellRangeAddressList destAddressList = new CellRangeAddressList(carFmlyDestMaster.getFirstRowNum() + 2, 10,
					carFmlyDestMasterColumnValue, carFmlyDestMasterColumnValue);
			XSSFDataValidation destDatavalidation = (XSSFDataValidation) destDvHelper.createValidation(destConstraint,
					destAddressList);
			destDatavalidation.setShowErrorBox(true);
			carFmlyDestMaster.addValidationData(destDatavalidation);

			carFmlyDestMaster.autoSizeColumn(0);
			carFmlyDestMaster.autoSizeColumn(1);
			carFmlyDestMaster.autoSizeColumn(2);
			carFmlyDestMaster.autoSizeColumn(3);

			carFmlyDestMaster.setSelected(false);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			return bos.toByteArray();
		}
	}

	@Override
	public byte[] downloadCountryOfOriginTemplate(String loginUserId, HttpServletResponse response)
			throws JRException, IOException {

		try (Workbook workbook = new XSSFWorkbook()) {

			Sheet cOOMaster = workbook.createSheet("COO_Master");
			Sheet countryCode = workbook.createSheet("Country_Code");

			Font cooMasterFont = workbook.createFont();
			cooMasterFont.setFontName(ConstantUtils.CALIBRIFONT);
			cooMasterFont.setFontHeightInPoints((short) 12);
			cooMasterFont.setBold(false);

			CellStyle cooMasterStyle = workbook.createCellStyle();

			cooMasterStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			cooMasterStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cooMasterStyle.setBorderBottom(BorderStyle.THIN);
			cooMasterStyle.setBorderLeft(BorderStyle.THIN);
			cooMasterStyle.setBorderRight(BorderStyle.THIN);
			cooMasterStyle.setBorderTop(BorderStyle.THIN);
			cooMasterStyle.setFont(cooMasterFont);
			cooMasterStyle.setAlignment(HorizontalAlignment.CENTER);

			Font countryCodeFont = workbook.createFont();
			countryCodeFont.setFontName(ConstantUtils.CALIBRIFONT);
			countryCodeFont.setFontHeightInPoints((short) 11);
			countryCodeFont.setBold(true);

			CellStyle countryCodeStyle = workbook.createCellStyle();

			countryCodeStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			countryCodeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			countryCodeStyle.setBorderBottom(BorderStyle.THIN);
			countryCodeStyle.setBorderLeft(BorderStyle.THIN);
			countryCodeStyle.setBorderRight(BorderStyle.THIN);
			countryCodeStyle.setBorderTop(BorderStyle.THIN);
			countryCodeStyle.setFont(countryCodeFont);

			Font gridFont = workbook.createFont();
			gridFont.setFontName(ConstantUtils.CALIBRIFONT);
			gridFont.setFontHeightInPoints((short) 11);
			gridFont.setBold(false);

			CellStyle gridDataStyle = workbook.createCellStyle();

			gridDataStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			gridDataStyle.setFillPattern(FillPatternType.NO_FILL);
			gridDataStyle.setFont(gridFont);

			CreationHelper creationHelper = workbook.getCreationHelper();

			Font dateFont = workbook.createFont();
			dateFont.setFontName(ConstantUtils.CALIBRIFONT);
			dateFont.setFontHeightInPoints((short) 11);
			dateFont.setBold(false);

			CellStyle cellstyleforDate = workbook.createCellStyle();

			cellstyleforDate.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			cellstyleforDate.setFillPattern(FillPatternType.NO_FILL);
			cellstyleforDate.setDataFormat(creationHelper.createDataFormat().getFormat(ConstantUtils.POIDATEFORMATE));
			cellstyleforDate.setFont(dateFont);

			cOOMaster.setDisplayGridlines(false);
			countryCode.setDisplayGridlines(true);

			Row cOOMasterrow = cOOMaster.createRow((short) 0);

			cOOMasterrow.createCell(0).setCellValue(ConstantUtils.PART_NO);
			cOOMasterrow.getCell(0).setCellStyle(cooMasterStyle);
			cOOMasterrow.createCell(1).setCellValue(ConstantUtils.PART_NAME);
			cOOMasterrow.getCell(1).setCellStyle(cooMasterStyle);
			cOOMasterrow.createCell(2).setCellValue(ConstantUtils.COUNTRY_OF_ORIGIN_CODE_COOC);
			cOOMasterrow.getCell(2).setCellStyle(cooMasterStyle);
			cOOMasterrow.createCell(3).setCellValue(ConstantUtils.VAN_DATE_FROM);
			cOOMasterrow.getCell(3).setCellStyle(cooMasterStyle);
			cOOMasterrow.createCell(4).setCellValue(ConstantUtils.VAN_DATE_TO);
			cOOMasterrow.getCell(4).setCellStyle(cooMasterStyle);

			Row cOOMasterrow1 = cOOMaster.createRow(1);

			cOOMasterrow1.createCell(0).setCellValue(ConstantUtils.XX12);
			cOOMasterrow1.getCell(0).setCellStyle(cooMasterStyle);
			cOOMasterrow1.createCell(1).setCellValue(ConstantUtils.XX40);
			cOOMasterrow1.getCell(1).setCellStyle(cooMasterStyle);
			cOOMasterrow1.createCell(2).setCellValue(ConstantUtils.CODE_COUNTRY_NAME);
			cOOMasterrow1.getCell(2).setCellStyle(cooMasterStyle);
			cOOMasterrow1.createCell(3).setCellValue(ConstantUtils.POIDATEFORMATEFORCELL);
			cOOMasterrow1.getCell(3).setCellStyle(cooMasterStyle);
			cOOMasterrow1.createCell(4).setCellValue(ConstantUtils.POIDATEFORMATEFORCELL);
			cOOMasterrow1.getCell(4).setCellStyle(cooMasterStyle);

			int initialColumn = cOOMasterrow1.getFirstCellNum();
			int lastColumn = cOOMasterrow1.getLastCellNum() - 1;

			Row countryCoderow = countryCode.createRow((short) 0);

			countryCoderow.createCell(0).setCellValue(ConstantUtils.COUNTRY_OF_ORIGIN_CODE);
			countryCoderow.getCell(0).setCellStyle(countryCodeStyle);
			countryCoderow.createCell(1).setCellValue(ConstantUtils.COUNTRY_NAME);
			countryCoderow.getCell(1).setCellStyle(countryCodeStyle);

			cOOMaster.setSelected(true);

			CellRangeAddress region = new CellRangeAddress(ConstantUtils.ROWINDEXINITIAL, ConstantUtils.CELLRANGE,
					initialColumn, lastColumn);
			RegionUtil.setBorderBottom(BorderStyle.THICK, region, cOOMaster);
			RegionUtil.setBorderLeft(BorderStyle.THICK, region, cOOMaster);
			RegionUtil.setBorderRight(BorderStyle.THICK, region, cOOMaster);
			RegionUtil.setBorderTop(BorderStyle.THICK, region, cOOMaster);

			cOOMaster.setSelected(false);

			List<CountryMasterEntity> countryMasterEntity = countryMasterRepository
					.findAll(Sort.by("countryName").ascending());

			List<String> countryNameWithCodesDto = countryMasterEntity.stream()
					.map(u -> (u.getCountryCode() + "-" + u.getCountryName())).collect(Collectors.toList());

			int countryCodeRowIndex = 1;
			int countryCodeColumnIndex = 0;

			for (CountryMasterEntity countryMasterEntityy : countryMasterEntity) {

				Row countryCodeDataRow = countryCode.createRow(countryCodeRowIndex);

				countryCodeDataRow.createCell(0).setCellValue(countryMasterEntityy.getCountryCode());
				countryCodeDataRow.getCell(0).setCellStyle(gridDataStyle);
				countryCodeDataRow.createCell(1).setCellValue(countryMasterEntityy.getCountryName());
				countryCodeDataRow.getCell(1).setCellStyle(gridDataStyle);
				countryCode.autoSizeColumn(countryCodeColumnIndex);
				countryCodeRowIndex++;
				countryCodeColumnIndex++;
			}

			cOOMaster.setSelected(true);

			XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) countryCode);
			XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
					.createExplicitListConstraint(countryNameWithCodesDto.toArray(new String[0]));

			int cOOMasterColumnValue = 2;
			CellRangeAddressList addressList = new CellRangeAddressList(cOOMaster.getFirstRowNum() + 2, 50,
					cOOMasterColumnValue, cOOMasterColumnValue);
			XSSFDataValidation datavalidation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint,
					addressList);
			datavalidation.setShowErrorBox(true);
			cOOMaster.addValidationData(datavalidation);

			cOOMaster.autoSizeColumn(0);
			cOOMaster.autoSizeColumn(1);
			cOOMaster.autoSizeColumn(2);
			cOOMaster.autoSizeColumn(3);
			cOOMaster.autoSizeColumn(4);

			cOOMaster.setSelected(false);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				workbook.write(bos);
			} finally {
				bos.close();
			}
			return bos.toByteArray();
		}

	}

	/**
	 * Method for generate excell report of Lot Part Shortage
	 * 
	 * @author Mohd.Javed
	 */
	public Object generateLostPartShortageReport(LotPartShortageReportsRequestDto request)
			throws JRException, IOException, ParseException {

		String etd = null;
		String stringEtd = null;
		String etdMonth = request.getEtdMonth().replace("/", "");

		if (request != null && !(StringUtils.isEmpty(request.getEtdDate()))) {
			etd = request.getEtdDate();
			SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.SIMPLEDATEFORMATINPUT);
			java.util.Date date = sdf.parse(etd);
			sdf = new SimpleDateFormat(ConstantUtils.DATE);
			stringEtd = sdf.format(date);
		}

		Object jasperResponse = null;
		String formatedEtdMonth = etdMonth.substring(2, 6) + "/" + etdMonth.substring(0, 2);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put(ConstantUtils.P_I_V_ETD_MONTH, formatedEtdMonth);
		parameters.put(ConstantUtils.P_I_V_DEST, request.getDestination());
		parameters.put(ConstantUtils.P_I_V_CF, request.getCarFamilyCode());
		parameters.put(ConstantUtils.P_I_V_PKG_MNTH, request.getPkgMonth());
		parameters.put(ConstantUtils.P_I_V_ETD, request.getEtdDate());

		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,
				tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue());
		config.put(ConstantUtils.REPORT_FORMAT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT).getValue());
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_SIZE_LIMIT).getValue());
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		String fileFormat = "xlsx";

		List<LotPartShortageReportsResponseDto> response = new ArrayList<>();

		List<Object[]> dataList = getDataList(request);

		int destCounter = 0;
		String destCd = null;
		int etdCounter = 0;
		String etdValue = null;

		if (!dataList.isEmpty()) {
			for (Object[] obj : dataList) {
				LotPartShortageReportsResponseDto dto = new LotPartShortageReportsResponseDto();

				if (obj[0] != null)
					dto.setItemNo(Integer.valueOf(obj[0].toString()));
				if (obj[1] != null)
					dto.setInvNo(obj[1].toString());
				if (obj[2] != null)
					dto.setRevNo(obj[2].toString());
				if (obj[3] != null) {
					dto.setEtd(obj[3].toString());
					if (etdValue != null && !etdValue.equals(obj[3].toString())) {
						etdCounter++;
					}
					etdValue = obj[3].toString();
				}
				if (obj[4] != null) {
					dto.setDestCd(obj[4].toString());
					if (destCd != null && !destCd.equals(obj[4].toString())) {
						destCounter++;
					}
					destCd = obj[4].toString();
				}
				if (obj[5] != null)
					dto.setOrdNo(obj[5].toString());
				if (obj[6] != null)
					dto.setCfCd(obj[6].toString());
				if (obj[7] != null)
					dto.setContSno(obj[7].toString());
				if (obj[8] != null)
					dto.setLotNo(obj[8].toString());
				if (obj[9] != null)
					dto.setCaseCd(obj[9].toString());
				if (obj[10] != null)
					dto.setPartNo(obj[10].toString());
				if (obj[11] != null)
					dto.setPartName(obj[11].toString());
				if (obj[12] != null)
					dto.setPlanQty(obj[12].toString());
				if (obj[13] != null)
					dto.setActQty(obj[13].toString());
				if (obj[14] != null)
					dto.setDiffQty(Integer.valueOf(obj[14].toString()));

				if (obj[15] != null)
					dto.setSno(Integer.valueOf(obj[15].toString()));

				response.add(dto);
			}

		}
		if (etdValue != null) {
			etdValue = DateUtil.getDate(etdValue);

		}
		String reportName = request.getReportName();

		String fileName = request.getReportName();
		if (destCounter > 1 && etdCounter > 1) {
			fileName = fileName + "_All_ETD" + etdMonth + ConstantUtils.EXCEL_FORMAT;
		} else if (destCounter > 1 && etdCounter == 0 && stringEtd != null) {
			fileName = fileName + "_All_ETD" + stringEtd + ConstantUtils.EXCEL_FORMAT;
		} else if (destCounter == 0 && etdCounter > 1 && request.getDestination() != null) {
			fileName = fileName + "_" + request.getDestination() + "_ETD" + etdMonth + ConstantUtils.EXCEL_FORMAT;
		} else if (destCounter == 0 && etdCounter == 0 && destCd != null && etdValue != null) {
			fileName = fileName + "_" + destCd + "_ETD" + etdValue + ConstantUtils.EXCEL_FORMAT;
		} else {
			fileName = fileName + ConstantUtils.EXCEL_FORMAT;
		}

		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/")
				.append(fileName);

		RddDownLocDtlEntity savedRddDownLocDtlEntity = jasperReportService.saveOfflineDownloadDetail(reportName, config,
				sb);
		int reportId = savedRddDownLocDtlEntity != null ? savedRddDownLocDtlEntity.getReportId() : 0;
		jasperReportService.getJasperReportDownloadOffline(response, fileFormat, reportName, parameters, config,
				reportId, sb);
		return jasperResponse;

	}

	private List<Object[]> getDataList(LotPartShortageReportsRequestDto request) {

		List<Object[]> dataList = new ArrayList<>();

		if (request.getRevisionNo().equals("All")) {
			dataList = lotPartShortageRepository.getAllData(request.getEtdMonth(), request.getDestination(),
					request.getCarFamilyCode(), request.getPkgMonth(), request.getInvoiceNo(), request.getEtdDate());
		} else if (request.getRevisionNo().equals("Latest")) {
			dataList = lotPartShortageRepository.getLatestData(request.getEtdMonth(), request.getDestination(),
					request.getCarFamilyCode(), request.getPkgMonth(), request.getInvoiceNo(), request.getEtdDate());
		} else if (request.getRevisionNo() != null) {
			dataList = lotPartShortageRepository.getData(request.getEtdMonth(), request.getDestination(),
					request.getCarFamilyCode(), request.getPkgMonth(), request.getInvoiceNo(), request.getEtdDate(),
					request.getRevisionNo());
		}

		return dataList;
	}

	@Override
	public String uploadWrkPlanMasterBatchServiceCall(UploadWrkPlanMasterJobDto uploadWrkPlanMasterJobDto) {

		try {
			webClient.post().uri(invoiceBatchServiceHost + "/invoice/uplaodWrkPlanMaster")
			.bodyValue(uploadWrkPlanMasterJobDto).retrieve().bodyToMono(String.class).block();
		} catch (Exception e) {
			throw new InvalidInputParametersException();
		}
		if (retryFlag) {

			Map<String, Object> output = retryService.getBatchStatus(uploadWrkPlanMasterJobDto.getUserId(),
					uploadWrkPlanMasterJobDto.getBatchName());

			String batchStatus = (String) output.get(ConstantUtils.BATCH_STATUS);
			String warningFlag = (String) output.get("warningFlag");

			if (batchStatus.equals(ConstantUtils.STS_SUCCESS)) {
				if (warningFlag.equals("Y")) {
					return ConstantUtils.INFO_AD_4002;
				} else {
					return ConstantUtils.INFO_CM_3004;
				}
			} else if (batchStatus.equals(ConstantUtils.STS_ERROR)) {
				return ConstantUtils.ERR_AD_4003;
			}

			return ConstantUtils.INFO_AD_4004;
		} else {
			return ConstantUtils.INFO_AD_4005;
		}

	}

	@Override
	public String countryCodeofOriginBatchServiceCall(UploadCountryOfOriginJobDto uploadCountryOfOriginJobDto) {

		webClient.post().uri(invoiceBatchServiceHost + "/invoice/countryCodeOriginUpload")
		.bodyValue(uploadCountryOfOriginJobDto).retrieve().bodyToMono(String.class).block();

		if (retryFlag) {

			Map<String, Object> output = retryService.getBatchStatus(uploadCountryOfOriginJobDto.getUserId(),
					uploadCountryOfOriginJobDto.getBatchName());

			String batchStatus = (String) output.get(ConstantUtils.BATCH_STATUS);

			if (batchStatus.equals(ConstantUtils.STS_SUCCESS)) {

				return ConstantUtils.INFO_CM_3004;

			} else if (batchStatus.equals(ConstantUtils.STS_ERROR)) {
				return ConstantUtils.ERR_AD_4003;
			}

			return ConstantUtils.INFO_AD_4004;
		} else {
			return ConstantUtils.INFO_AD_4005;
		}

	}

	@Override
	public String uploadPartMasterBatchServiceCall(String batchName, MultipartFile file, String companyCode,
			String userId) {

		try {

			if (batchName.isBlank() || file.isEmpty()) {
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
			}

			String filename = file.getOriginalFilename();
			if (filename != null && (!(filename.endsWith(".xls") || filename.endsWith(".xlsx")))) {
				throw new InvalidFileException(ConstantUtils.ERR_CM_3021);
			}

			String excelFilePath = file.getOriginalFilename();

			Workbook workbook = getWorkBook(file, excelFilePath);

			Sheet worksheet = workbook.getSheetAt(0);

			if (worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1) {
				workbook.close();
				throw new InvalidFileException(ConstantUtils.ERR_CM_3009);
			}

			if (tpexConfigurationUtil.checkForEmptySheet(worksheet, 2)) {
				workbook.close();
				throw new InvalidFileException(ConstantUtils.ERR_AD_2001);
			}

			workbook.close();

			// Save file
			fileService.save(file);
			OemProcessCtrlEntity oemProcessCtrlEntity = saveProcessCtrlDetail(userId, null, batchName);
			UploadPartMasterJobDto uploadPartMasterJobDto = new UploadPartMasterJobDto();
			uploadPartMasterJobDto.setBatchName(batchName);
			uploadPartMasterJobDto.setFileName(file.getOriginalFilename());
			uploadPartMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
			uploadPartMasterJobDto.setCompanyCode(companyCode);
			uploadPartMasterJobDto.setUserId(userId);

			webClient.post().uri(invoiceBatchServiceHost + "/invoice/uploadPartMaster")
			.bodyValue(uploadPartMasterJobDto).retrieve().bodyToMono(String.class).block();

		} catch (Exception e) {
			throw new InvalidInputParametersException();
		}
		if (retryFlag) {

			Map<String, Object> output = retryService.getBatchStatus(userId, batchName);

			String batchStatus = (String) output.get(ConstantUtils.BATCH_STATUS);

			if (batchStatus.equals(ConstantUtils.STS_SUCCESS)) {
				return ConstantUtils.INFO_CM_3004;
			} else if (batchStatus.equals(ConstantUtils.STS_ERROR)) {
				return ConstantUtils.ERR_AD_4003;
			}

			return ConstantUtils.INFO_AD_4004;
		} else {
			return ConstantUtils.INFO_AD_4005;
		}

	}

	@Override
	public Object generateContainerRequisitionSheet(ContainerRequisitionRequestDTO containerRequisitionRequestDTO)
			throws ParseException, JRException, IOException {
		String vanDate = null;
		String formattedVanDate = null;
		String formattedVanDateForDb = null;
		String etdMonth = containerRequisitionRequestDTO.getVanMonth().replace("/", "");

		if (containerRequisitionRequestDTO != null && !(StringUtils.isEmpty(containerRequisitionRequestDTO.getVanDate()))) {
			vanDate = containerRequisitionRequestDTO.getVanDate();
			SimpleDateFormat sdf = new SimpleDateFormat(ConstantUtils.SIMPLEDATEFORMATINPUT);
			java.util.Date date = sdf.parse(vanDate);
			sdf = new SimpleDateFormat(ConstantUtils.DATE);
			formattedVanDate = sdf.format(date);
		}

		if (containerRequisitionRequestDTO != null && !(StringUtils.isEmpty(containerRequisitionRequestDTO.getVanDate()))) {
			vanDate = containerRequisitionRequestDTO.getVanDate();
			SimpleDateFormat sdfDb = new SimpleDateFormat(ConstantUtils.SIMPLEDATEFORMATINPUT);
			java.util.Date dateForDb = sdfDb.parse(vanDate);
			sdfDb = new SimpleDateFormat(ConstantUtils.DATE_SQL);
			formattedVanDateForDb = sdfDb.format(dateForDb);
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDateTime now = LocalDateTime.now();
		String formattedMonth = etdMonth.substring(2, 6) + etdMonth.substring(0, 2);

		Optional<TbMPlant> plantName = plantMasterRepository.findById(containerRequisitionRequestDTO.getVanPlant());
		String plantNameVar = null;
		if (!plantName.isEmpty()) {
			plantNameVar = plantName.get().getPlantName();
		}

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("P_I_V_CONT_VAN_MTH", formattedMonth);
		parameters.put("P_I_V_CONT_VAN_DATE", formattedVanDate);
		parameters.put("P_I_V_VAN_PLANT", containerRequisitionRequestDTO.getVanPlant());
		parameters.put("P_I_V_VAN_PLANT_NAME", plantNameVar);
		parameters.put("P_I_V_USER_ID", "Test");
		parameters.put("prmSysdate", dtf.format(now));

		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,
				tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue());
		config.put(ConstantUtils.REPORT_FORMAT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT).getValue());
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_SIZE_LIMIT).getValue());
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		String fileFormat = "xlsx";

		List<ContainerRequisitionResponseDto> response = new ArrayList<>();

		List<Object[]> dataList = null;

		try {
			dataList = finalDestRepository.getContainerRequisitionData(formattedMonth, formattedVanDateForDb,
					containerRequisitionRequestDTO.getVanPlant());
		}

		catch (Exception e) {
			
		}

		if (!dataList.isEmpty()) {
			for (Object[] obj : dataList) {
				ContainerRequisitionResponseDto dto = new ContainerRequisitionResponseDto();
				if (obj[0] != null)
					dto.setPlantCd(obj[0].toString());
				if (obj[1] != null)
					dto.setVanPlntNm(obj[1].toString());
				if (obj[2] != null)
					dto.setShiftCd(obj[2].toString());
				if (obj[3] != null)
					dto.setVanSeq(obj[3].toString());
				if (obj[4] != null)
					dto.setReqDt(obj[4].toString());
				if (obj[5] != null)
					dto.setDelvPlanTm(obj[5].toString());
				if (obj[6] != null)
					dto.setNpcsVanFnlStTm(obj[6].toString());
				if (obj[7] != null)
					dto.setNpcsVanFnlEndTm(obj[7].toString());
				if (obj[8] != null)
					dto.setActVanStrtTm(obj[8].toString());
				if (obj[9] != null)
					dto.setActVanEndTm(obj[9].toString());
				if (obj[10] != null)
					dto.setQty20(Integer.parseInt(obj[10].toString()));
				if (obj[11] != null)
					dto.setQty40(Integer.parseInt(obj[11].toString()));
				if (obj[13] != null)
					dto.setShpComp(obj[13].toString());
				if (obj[14] != null)
					dto.setBookNo(obj[14].toString());
				if (obj[15] != null)
					dto.setVessNm(obj[15].toString());
				if (obj[16] != null)
					dto.setCntIsoNo(obj[16].toString());
				if (obj[17] != null)
					dto.setSealNo(obj[17].toString());
				if (obj[20] != null)
					dto.setRenbanCd(obj[20].toString());
				if (obj[21] != null)
					dto.setGpacClose(obj[21].toString());
				if (obj[22] != null)
					dto.setModQty(Double.parseDouble(obj[22].toString()));
				if (obj[23] != null)
					dto.setNpcsDockNo(obj[23].toString());
				if (obj[24] != null)
					dto.setNpcsCycTm(obj[24].toString());
				if (obj[25] != null)
					dto.setRemark(obj[25].toString());
				if (obj[27] != null)
					dto.setCustomBroker(obj[27].toString());
				if (obj[28] != null)
					dto.setConatainerTareWeight(obj[28].toString());
				if (obj[26] != null)
					dto.setConatainerNetWeight(obj[26].toString());

				response.add(dto);
			}

		}

		String fileName;

		if ("DailyContainerRequisitionSheet".equalsIgnoreCase(containerRequisitionRequestDTO.getReportName())) {
			fileName = "DCR_by_End_Van_Date" + "_" + containerRequisitionRequestDTO.getVanPlant() + "_" + formattedVanDate + "_report"
					+ ".xlsx";
		} else {

			fileName = "DCR_by_End_Van_Date" + "_" + containerRequisitionRequestDTO.getVanPlant() + "_" + formattedVanDate + "_data_only"
					+ ".xlsx";

		}

		Object jasperRresponse = null;
		jasperRresponse = jasperReportService.getJasperReportDownloadOnline(response, fileFormat, fileName, parameters,
				config);

		return jasperRresponse;

	}

}
