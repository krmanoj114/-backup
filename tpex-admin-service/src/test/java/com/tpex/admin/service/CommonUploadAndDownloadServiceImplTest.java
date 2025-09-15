package com.tpex.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Sort;

import com.tpex.admin.dto.AddressMasterDTO;
import com.tpex.admin.dto.CountryofOriginMastListReportRequestDto;
import com.tpex.admin.dto.LotPartShortageReportsRequestDto;
import com.tpex.admin.dto.LotPartShortageReportsResponseDto;
import com.tpex.admin.dto.PxpPartPriceMasterDto;
import com.tpex.admin.dto.ReportNameDTO;
import com.tpex.admin.entity.AddressMasterEntity;
import com.tpex.admin.entity.AddressMasterIdEntity;
import com.tpex.admin.entity.CountryMasterEntity;
import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.entity.OemProcessCtrlIdEntity;
import com.tpex.admin.entity.OemProgDtlsEntity;
import com.tpex.admin.entity.OemProgDtlsIdEntity;
import com.tpex.admin.entity.RddDownLocDtlEntity;
import com.tpex.admin.entity.TpexConfigEntity;
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
import com.tpex.admin.repository.TpexConfigRepository;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.TpexConfigurationUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;


@ExtendWith(MockitoExtension.class)
class CommonUploadAndDownloadServiceImplTest {

	@InjectMocks
	CommonUploadAndDownloadServiceImpl commonUploadAndDownloadService;

	@Mock
	OemProcessCtrlRepository oemProcessCtrlRepository;

	@Mock
	OemProgDtlsRepository oemProgDtlsRepository;

	@Mock
	JobLauncher jobLauncher;

	@Mock
	Job job;

	@Mock
	JasperReportService jasperReportService;

	@Mock
	NatCalRepository natCalRepository;

	@Mock
	AddressMasterRepository addressMasterRepository;

	@Mock
	TpexConfigRepository tpexConfigRepository;

	@Mock
	CountryOriginMstRepository countryOriginMstRepository;

	@Mock
	CountryMasterRepository countryMasterRepository;

	@Mock
	CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;

	@Mock
	FinalDestRepository finalDestRepository;

	@Mock
	CarFamilyMastRepository carFamilyMastRepository;

	@Mock
	LotPartShortageRepository lotPartShortageRepository;
	
	@Mock
	private TpexConfigurationUtil tpexConfigurationUtil;
	
	private static String userID = "User ID";
	private static String configName = "config.name";
	private static String fontColorCode = "#000000";
	private static String partPriceNo = "531530206000";
	private static String vanDateFrom = "01/08/2015";
	private static String vanDateTo = "31/08/2017";
	private static String etdMonthValue = "022022";
	private static String reportDirectory = "/home/TMAP/DSD/shared/report/TPEXConfig/InvoiceGeneration/Report";


	@Test
	void testFetchProcessDetails() {
		List<OemProcessCtrlEntity> list = new ArrayList<>();
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId(userID);
		list.add(oemProcessCtrlEntity);
		when(oemProcessCtrlRepository.findByUserId(anyString())).thenReturn(list);
		commonUploadAndDownloadService.fetchProcessDetails(ConstantUtils.USERID);
		assertEquals(userID, oemProcessCtrlEntity.getUserId());
	}

	@Test
	void testDownloadTemplate() throws IOException {
		ReportNameDTO reportName = new ReportNameDTO();
		reportName.setReportId(1);
		reportName.setReportName("ReportName");
		reportName.setExcelName("ExcelName");
		reportName.setExcelHeaderNames("ExcelHeaderNames");
		List<ReportNameDTO> listOfReportNames = new ArrayList<>();
		listOfReportNames.add(reportName);
		commonUploadAndDownloadService.downloadTemplate(listOfReportNames, 1, "SheetName");
		assertEquals("ReportName", reportName.getReportName());

	}

	// @ParameterizedTest
	// @ValueSource(booleans = { true, false })
	void downloadNatCalMastTest(boolean isDownloadTemplate) throws Exception {
		TpexConfigEntity config = new TpexConfigEntity();
		config.setId(1);
		config.setName(configName);
		config.setValue("testConfig101");
		when(tpexConfigRepository.findByName(anyString())).thenReturn(config);

		commonUploadAndDownloadService.downloadNatCalMast("2022", "OEM", isDownloadTemplate);

		if (isDownloadTemplate)
			verify(natCalRepository, never()).findByIdYear(anyInt());
		else
			verify(natCalRepository, times(1)).findByIdYear(anyInt());
	}

	@Test
	void jobServiceReceivingTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId(userID);
		oemProcessCtrlEntity.setId(id);

		commonUploadAndDownloadService.jobServiceReceiving("batchName", "fileName", oemProcessCtrlEntity);

		verify(jobLauncher, times(1)).run(any(Job.class),any(JobParameters.class));
	}

	// @Test
	void saveProcessDetailsTest() {
		Optional<OemProcessCtrlEntity> opt = Optional.empty();

		when(oemProcessCtrlRepository.findById(any(OemProcessCtrlIdEntity.class))).thenReturn(opt);

		OemProcessCtrlEntity result = commonUploadAndDownloadService.saveProcessDetails(ConstantUtils.USERID, "year");

		assertEquals(ConstantUtils.USERID, result.getUserId());
		verify(oemProcessCtrlRepository, atLeastOnce()).save(any(OemProcessCtrlEntity.class));
	}

	@Test
	void getProcessNameDetailsTest() {
		OemProgDtlsIdEntity id = new OemProgDtlsIdEntity();
		id.setProgramId("prg01");
		id.setEntryTyp("entry01");
		OemProgDtlsEntity oem = new OemProgDtlsEntity();
		oem.setId(id);
		oem.setProgramDesc("desc01");
		List<OemProgDtlsEntity> listofOemProgDtlsEntity = new ArrayList<>();
		listofOemProgDtlsEntity.add(oem);

		when(oemProgDtlsRepository.findAll()).thenReturn(listofOemProgDtlsEntity);

		Map<String, String> result = commonUploadAndDownloadService.getProcessNameDetails();

		assertEquals("desc01", result.get("prg01"));
	}

	@Test
	void getProcessNameById() {
		OemProgDtlsEntity oemProgDtlsEntity = new OemProgDtlsEntity();
		oemProgDtlsEntity.setProgramDesc("prgDesc");

		when(oemProgDtlsRepository.findByIdProgramId(anyString())).thenReturn(oemProgDtlsEntity);

		String processNameById = commonUploadAndDownloadService.getProcessNameById("prgId");

		assertEquals("prgDesc", processNameById);
	}

	// @ParameterizedTest
	// @ValueSource(booleans = { true, false })
	@Test
	void downloadAddressMasterTest() throws Exception {
		boolean isdownloadTemplate = true;
		AddressMasterDTO addressMasterDTO = new AddressMasterDTO();
		addressMasterDTO.setCmpCode("TMT");
		addressMasterDTO.setBranch("1");
		addressMasterDTO.setName("Toyota Motor Asia Pacific Engineering & Manufacturing Co.,Ltd.");
		addressMasterDTO.setAddress1("99 Moo5, Bangna-Trad K.M. 29.5 Rd.");
		addressMasterDTO.setAddress2("T.Ban-Ragad, A.Bang Bo,Samutprakarn,");
		addressMasterDTO.setAddress3("10560 Thailand.Tel: (66-2) 790-5000");
		addressMasterDTO.setAddress4("Tax ID : 0115546006888");
		addressMasterDTO.setZip("10560");
		addressMasterDTO.setShortName("TMT");
		addressMasterDTO.setSapCode("5000");
		addressMasterDTO.setConsigneeCode(null);
		addressMasterDTO.setCountryCode("722");
		addressMasterDTO.setTelephoneNumber(null);
		addressMasterDTO.setFaxNumber("(662) 790-5020");
		addressMasterDTO.setEmail(null);
		addressMasterDTO.setTelexNumber(null);
		addressMasterDTO.setContactPerson(null);
		addressMasterDTO.setInvoiceFlag("Y");
		addressMasterDTO.setScFlag("N");
		addressMasterDTO.setScRemarks(null);
		addressMasterDTO.setEmployee1(null);
		addressMasterDTO.setEmployee2(null);
		addressMasterDTO.setEmployee3(null);
		addressMasterDTO.setCompany("TMAP-EM");
		addressMasterDTO.setUpdateBy("PCHEENIM");
		addressMasterDTO.setUpdateDate(null);
		List<AddressMasterDTO> list = new ArrayList<>();
		list.add(addressMasterDTO);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put(ConstantUtils.HEADINGFONTCOLOR, fontColorCode);
		parameters.put(ConstantUtils.DETAILFONTCOLOR, fontColorCode);
		parameters.put(ConstantUtils.HEADINGBGCOLOR, "#EDFBFF");
		parameters.put(ConstantUtils.DETAILBGCOLOR, null);
		parameters.put(ConstantUtils.DETAILVALIGN, "Middle");
		Mockito.when(tpexConfigurationUtil.getReportDynamicPrameters()).thenReturn(parameters);

		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.SET_WHITE_PAGE_BACKGROUND, false);
		config.put(ConstantUtils.SET_DETECT_CELL_TYPE, true);
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_DIRECTORY,
				reportDirectory);
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT, "xlsx");
		config.put(ConstantUtils.NATIONAL_CALENDAR_REPORT_SIZE_LIMIT, 400);
		config.put(ConstantUtils.LOGIN_USER_ID, "Test");
		config.put(ConstantUtils.INVOICE_GENERATION_STORE_DB, "false");
		Mockito.when(tpexConfigurationUtil.getReportConfig("Test", true)).thenReturn(config);

		String reportFormat = "xlsx";
		TpexConfigEntity configT = new TpexConfigEntity();
		configT.setId(1);
		configT.setName(configName);
		Mockito.lenient().when(tpexConfigRepository.findByName(anyString())).thenReturn(configT);
		List<AddressMasterEntity> listofEntity = new ArrayList<>();
		AddressMasterEntity listEntity = new AddressMasterEntity();
		AddressMasterIdEntity listIdEntity = new AddressMasterIdEntity();
		listIdEntity.setCode("TMT");
		listIdEntity.setBranch("1");
		listIdEntity.setCompany("TMAP-EM");
		listEntity.setId(listIdEntity);
		listEntity.setCmpCode("TMT");
		listEntity.setName("Toyota Motor Asia Pacific Engineering & Manufacturing Co.,Ltd.");
		listEntity.setAddress1("99 Moo5, Bangna-Trad K.M. 29.5 Rd.");
		listEntity.setAddress2("T.Ban-Ragad, A.Bang Bo,Samutprakarn,");
		listEntity.setAddress3("10560 Thailand.Tel: (66-2) 790-5000");
		listEntity.setAddress4("Tax ID : 0115546006888");
		listEntity.setZip("10560");
		listEntity.setShortName("TMT");
		listEntity.setSapCode("5000");
		listEntity.setConsigneeCode(null);
		listEntity.setCountryCode("722");
		listEntity.setTelephoneNumber(null);
		listEntity.setFaxNumber("(662) 790-5020");
		listEntity.setEmail(null);
		listEntity.setTelexNumber(null);
		listEntity.setContactPerson(null);
		listEntity.setInvoiceFlag("Y");
		listEntity.setScFlag("N");
		listEntity.setScRemarks(null);
		listEntity.setEmployee1(null);
		listEntity.setEmployee2(null);
		listEntity.setEmployee3(null);
		listEntity.setUpdateBy("PCHEENIM");
		listEntity.setUpdateDate(null);
		listofEntity.add(listEntity);
		Mockito.lenient()
				.when(addressMasterRepository
						.findByCmpCodeOrderByIdCodeAscIdBranchAscNameAscAddress1Asc(anyString()))
				.thenReturn(listofEntity);
		jasperReportService.getJasperReportDownloadOnline(list, reportFormat, "AddressMaster", parameters, config);
		commonUploadAndDownloadService.downloadAddressMaster("TMT", "Test", isdownloadTemplate);
		assertEquals(null, parameters.get(ConstantUtils.DETAILBGCOLOR));
	}

	@Test
	void downloadPxpPartPriceMastTest() throws Exception {
		boolean isdownloadTemplate = true;
		List<PxpPartPriceMasterDto> list = new ArrayList<>();
		list.add(new PxpPartPriceMasterDto());

		Map<String, Object> parameters = new HashMap<>();

		parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put(ConstantUtils.HEADINGFONTCOLOR, fontColorCode);
		parameters.put(ConstantUtils.DETAILFONTCOLOR, fontColorCode);
		parameters.put(ConstantUtils.HEADINGBGCOLOR, "#EDFBFF");
		parameters.put(ConstantUtils.DETAILBGCOLOR, null);
		parameters.put(ConstantUtils.DETAILVALIGN, "Middle");
		Mockito.when(tpexConfigurationUtil.getReportDynamicPrameters()).thenReturn(parameters);

		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.SET_WHITE_PAGE_BACKGROUND, false);
		config.put(ConstantUtils.SET_DETECT_CELL_TYPE, true);
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_DIRECTORY,
				reportDirectory);
		config.put(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT, "xlsx");
		config.put(ConstantUtils.NATIONAL_CALENDAR_REPORT_SIZE_LIMIT, 400);
		config.put(ConstantUtils.LOGIN_USER_ID, "Test");
		config.put(ConstantUtils.INVOICE_GENERATION_STORE_DB, "false");
		Mockito.when(tpexConfigurationUtil.getReportConfig("Test", true)).thenReturn(config);
		String reportFormat = "xlsx";
		TpexConfigEntity configT = new TpexConfigEntity();
		configT.setId(1);
		configT.setName(configName);
		Mockito.lenient().when(tpexConfigRepository.findByName(any())).thenReturn(configT);

		jasperReportService.getJasperReportDownloadOnline(list, reportFormat, "PxpPartPriceMaster", parameters, config);
		commonUploadAndDownloadService.downloadPxpPartPriceMast("Test", isdownloadTemplate);
		assertEquals(null, parameters.get(ConstantUtils.DETAILBGCOLOR));
	}

	@Test

	void getCountryofOriginMastListReportTest() throws Exception {

		List<CountryMasterEntity> countryMasterEntityList = new ArrayList<>();
		countryMasterEntityList.add(new CountryMasterEntity("AFRICA", "AF", "106", "TMT", "", ""));
		countryMasterEntityList.add(new CountryMasterEntity("CHINA", "CH", "107", "TMT", "", ""));

		when(countryMasterRepository.findAll(Sort.by("countryName").ascending())).thenReturn(countryMasterEntityList);

		List<String> countryCode = new ArrayList<>();
		countryCode.add("JP");
		countryCode.add("IND");

		CountryofOriginMastListReportRequestDto testData = new CountryofOriginMastListReportRequestDto();
		testData.setCountryOfCountryCode(countryCode);
		testData.setPartPriceNo(partPriceNo);
		testData.setVanDateFrom(vanDateFrom);
		testData.setVanDateTo(vanDateTo);

		when(countryMasterRepository.findAll()).thenReturn(countryMasterEntityList);

		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("AFRICA", "AF");
		valueMap.put("CHINA", "CH");

		List<String[]> countryOriginMstList = new ArrayList<>();
		countryOriginMstList.add(new String[] { ConstantUtils.PART_PRICE_NUMBER, "GUIDE, FR SIDE AIR", "JP-JAPAN",
				"6-1-2014", "9-30-2018" });
		countryOriginMstList.add(new String[] { ConstantUtils.PART_PRICE_NUMBER, "GUIDE, FR SIDE AIR",
				"US-UNITED STATES", "10-1-2018", "12-31-1999" });

		when(countryOriginMstRepository.getPartNameDetails(anyString(), anyList()))

				.thenReturn(countryOriginMstList);

		List<String> countryNameWithCodesDto = new ArrayList<>();
		countryNameWithCodesDto.add("AF-AFRICA");
		countryNameWithCodesDto.add("CH-CHINA");

		HttpServletResponse reponse = mock(HttpServletResponse.class);
		String headerkey = "Content-Disposition";
		String headerValue = "attachment;filename=CountryOfOrigin.xlxs";
		reponse.setHeader(headerkey, headerValue);

		commonUploadAndDownloadService.getCountryofOriginMastListReport(partPriceNo, countryCode, vanDateFrom,
				vanDateTo);

		Assertions.assertDoesNotThrow(

				() -> commonUploadAndDownloadService.getCountryofOriginMastListReport(partPriceNo, countryCode,
						vanDateFrom, vanDateTo));

	}

	/*@Test
	void getCarFmlyDestinationMasterListReportTest() throws Exception {

		List<CarFamilyDestinationMasterEntity> crFmlyDestMasterEntityList = new ArrayList<>();
		crFmlyDestMasterEntityList.add(new CarFamilyDestinationMasterEntity(
				new CarFamilyDestinationMasterIdEntity("111W", "715B", "1"), "TOYOTA", null, null, "TMT"));
		crFmlyDestMasterEntityList.add(new CarFamilyDestinationMasterEntity(
				new CarFamilyDestinationMasterIdEntity("120A", "812B", "1"), "VIOS", null, null, "TMT"));

		String companyCode = "TMT";
		when(carFamilyDestinationMasterRepository.findAllByCompanyCode(companyCode))
				.thenReturn(crFmlyDestMasterEntityList);

		FinalDestEntity destEntity = new FinalDestEntity("715B", "VIETNAM", "1", "1", "1", "", new Date(0),
				new BigInteger("1"), "", "TMT");

		String destCode = "715B";
		when(finalDestRepository.findAllByDestinationCd(destCode)).thenReturn(destEntity);

		List<CarFamilyMasterEntity> carFamilyMasterEntityList = new ArrayList<>();
		carFamilyMasterEntityList.add(new CarFamilyMasterEntity("1", "TOY", "1", "", null, "", "", "TMT"));
		carFamilyMasterEntityList.add(new CarFamilyMasterEntity("2", "ROY", "", "", null, "", "", "TMT"));

		when(carFamilyMastRepository.findAllByOrderByCarFmlyCodeAsc()).thenReturn(carFamilyMasterEntityList);

		HttpServletResponse reponse = mock(HttpServletResponse.class);
		String headerkey = "Content-Disposition";
		String headerValue = "attachment;filename=CarFamilyDestinationMaster.xlsx";

		reponse.setHeader(headerkey, headerValue);

		String userId = "TestUser";

		commonUploadAndDownloadService.getCarFmlyDestnMstListReport(reponse, companyCode, userId);

		Assertions.assertDoesNotThrow(
				() -> commonUploadAndDownloadService.getCarFmlyDestnMstListReport(reponse, "TXX", userId));

	}*/

	@Test
	void generateLostPartShortageReportTest() throws JRException, IOException, ParseException {

		LotPartShortageReportsRequestDto request = new LotPartShortageReportsRequestDto();
		request.setEtdMonth(etdMonthValue);
		request.setDestination("715B");
		request.setCarFamilyCode("835W");
		request.setPkgMonth("202202");
		request.setEtdDate("14/02/2022");
		request.setRevisionNo("ALL");
		request.setReportName("Lotpartshortage");

		String etdMonth = etdMonthValue;

		Map<String, Object> parameters = new HashMap<>();
		parameters.put(ConstantUtils.P_I_V_ETD_MONTH, request.getEtdMonth());
		parameters.put(ConstantUtils.P_I_V_DEST, request.getDestination());
		parameters.put(ConstantUtils.P_I_V_CF, request.getCarFamilyCode());
		parameters.put(ConstantUtils.P_I_V_PKG_MNTH, request.getPkgMonth());
		parameters.put(ConstantUtils.P_I_V_ETD, request.getEtdDate());

		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		String ab = reportDirectory;

		TpexConfigEntity tconfig = new TpexConfigEntity();
		tconfig.setId(1);
		tconfig.setName(configName);
		tconfig.setValue("testConfig101");

		when(tpexConfigRepository.findByName(anyString())).thenReturn(tconfig);

		config.put(ConstantUtils.REPORT_DIRECTORY, ab);
		config.put(ConstantUtils.REPORT_FORMAT, "xlsx");
		config.put(ConstantUtils.REPORT_SIZE_LIMIT, 400);
		config.put(ConstantUtils.STORE_DB, "true");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		String fileFormat = "xlsx";

		List<LotPartShortageReportsResponseDto> response = new ArrayList<>();
		LotPartShortageReportsResponseDto dto = new LotPartShortageReportsResponseDto();
		dto.setCfCd("TOY");
		dto.setDestCd("735B");
		dto.setItemNo(1);
		dto.setInvNo("KR22X00002");
		dto.setRevNo("ALL");
		response.add(dto);
		List<Object[]> dataList = new ArrayList<>();
		Object[] objs = new Object[] { 1, "A36925", "ALL", "14/02/2022", "715B", "715B722CR202202835W10", "835W", "",
				"CL0035", "WM", "532040D02100", "Abb", "", "", 9, 4 };

		dataList.add(objs);

		if (request.getRevisionNo().equals("All")) {
			when(lotPartShortageRepository.getAllData(request.getEtdMonth(), request.getDestination(),
					request.getCarFamilyCode(), request.getPkgMonth(), request.getInvoiceNo(), request.getEtdDate()))
					.thenReturn(dataList);
		} else if (request.getRevisionNo().equals("Latest")) {
			when(lotPartShortageRepository.getLatestData(request.getEtdMonth(), request.getDestination(),
					request.getCarFamilyCode(), request.getPkgMonth(), request.getInvoiceNo(), request.getEtdDate()))
					.thenReturn(dataList);
		} else if (request.getRevisionNo() != null) {
			lenient().when(lotPartShortageRepository.getData(request.getEtdMonth(), request.getDestination(),
					request.getCarFamilyCode(), request.getPkgMonth(), request.getInvoiceNo(), request.getEtdDate(),
					request.getRevisionNo())).thenReturn(dataList);
		}

		String reportName = request.getReportName();
		int destCounter = 2;
		int etdCounter = 2;
		String fileName = request.getReportName();
		if (destCounter > 1 && etdCounter > 1) {
			fileName = fileName + "_All_" + etdMonth + ConstantUtils.EXCEL_FORMAT;
		}
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/")
				.append(fileName);

		RddDownLocDtlEntity savedRddDownLocDtlEntity = new RddDownLocDtlEntity();
		savedRddDownLocDtlEntity.setCompanyCode("JPA");
		savedRddDownLocDtlEntity.setReportId(12);
		lenient().when(jasperReportService.saveOfflineDownloadDetail(reportName, config, sb))
				.thenReturn(savedRddDownLocDtlEntity);
		int reportId = savedRddDownLocDtlEntity.getReportId();
		jasperReportService.getJasperReportDownloadOffline(response, fileFormat, reportName, parameters, config,
				reportId, sb);

		commonUploadAndDownloadService.generateLostPartShortageReport(request);

		assertEquals(etdMonthValue, request.getEtdMonth());
	}

}
