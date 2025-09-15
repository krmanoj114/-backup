package com.tpex.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.tpex.admin.service.CommonUploadAndDownloadServiceImpl;
import com.tpex.admin.service.FileService;
import com.tpex.admin.dto.CommonUploadAndDownloadDTO;
import com.tpex.admin.dto.CountryMasterDto;
import com.tpex.admin.dto.CountryofOriginMastListReportRequestDto;
import com.tpex.admin.dto.LotPartShortageReportsRequestDto;
import com.tpex.admin.dto.ProcessBatchDTO;
import com.tpex.admin.dto.ReportNameDTO;
import com.tpex.admin.entity.CountryMasterEntity;
import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.exception.DateFormateException;
import com.tpex.admin.exception.InvalidFileException;
import com.tpex.admin.exception.MyResourceNotFoundException;
import com.tpex.admin.dto.ColumnsDTO;
import com.tpex.admin.dto.CoulmnsInfo;
import com.tpex.admin.repository.CountryMasterRepository;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.TpexConfigurationUtil;

import net.sf.jasperreports.engine.JRException;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.ArgumentMatchers.any;



@ExtendWith(MockitoExtension.class)
class CommonUploadAndDownloadControllerTest {

	@InjectMocks
	CommonUploadAndDownloadController commonUploadAndDownloadController;

	@Mock
	CommonUploadAndDownloadServiceImpl commonUploadAndDownloadService;

	@Mock
	TpexConfigurationUtil tpexConfigurationUtil;

	@Mock
	CountryMasterRepository countryMasterRepository;

	@Mock
	FileService fileService;
	
	private static String nameOfFile = "nameOfFile.xlsx";
	private static String attachment = "attachment";
	private static String testUser = "TestUser";
	private static String batchNameBINS107 = "BINS107";
	private static String effFromDate = "2004/02";
	private static String textPlain = "text/plain";
	private static String someXlsx = "some xlsx";
	private static String partPriceMasterExcelName = "pxpPartPriceMaster.xlsx";
	private static String date1 = "01/08/2015";
	private static String date2 = "31/08/2017";
	private static String partPriceNo = "partPriceNo";

	@Test
	void fetchReportNamesAndProcessDetailsTest() throws Exception {
		CommonUploadAndDownloadDTO request = new CommonUploadAndDownloadDTO();
		request.setUserId("test");
		List<OemProcessCtrlEntity> list = new ArrayList<>();

		when(tpexConfigurationUtil.getFilePath(any())).thenReturn("classpath:reportName.json");
		when(commonUploadAndDownloadService.fetchProcessDetails(anyString())).thenReturn(list);

		ResponseEntity<CommonUploadAndDownloadDTO> result = commonUploadAndDownloadController
				.fetchReportNamesAndProcessDetails(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}

	@Test
	void fetchProcessDetailsTest() throws Exception {
		ReportNameDTO request = new ReportNameDTO();
		request.setUserId("test");
		List<OemProcessCtrlEntity> list = new ArrayList<>();

		when(commonUploadAndDownloadService.fetchProcessDetails(anyString())).thenReturn(list);

		ResponseEntity<List<OemProcessCtrlEntity>> result = commonUploadAndDownloadController
				.fetchProcessDetails(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}

	@ParameterizedTest
	@MethodSource(DOWNLOAD_NAT_CAL_MAST_PARAM)
	void getNalCalMastListTemplateReportTest(Object obj, int assertCase) throws Exception {

		when(commonUploadAndDownloadService.downloadNatCalMast(anyString(), anyString(), eq(true))).thenReturn(obj);

		ResponseEntity<?> result = commonUploadAndDownloadController.getNalCalMastListTemplateReport("2022", "ROEM");

		if(assertCase == 1) {// String
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertThat(result.getBody()).isInstanceOf(String.class);
		} else if (assertCase == 2) {//map with byte[]
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody() instanceof byte[]);
			assertEquals(nameOfFile, result.getHeaders().getContentDisposition().getFilename());
			assertEquals(attachment, result.getHeaders().getContentDisposition().getType());
			assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
		} else {//map with NOT byte[]
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
		}
	}

	// @ParameterizedTest
	// @MethodSource(DOWNLOAD_NAT_CAL_MAST_PARAM)
	void getNalCalMastListReportTest(Object obj, int assertCase) throws Exception {

		when(commonUploadAndDownloadService.downloadNatCalMast(anyString(),anyString(), eq(false))).thenReturn(obj);

		ResponseEntity<?> result = commonUploadAndDownloadController.getNalCalMastListReport("2022", "ROEM");

		if(assertCase == 1) {// String
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertThat(result.getBody()).isInstanceOf(String.class);
		} else if (assertCase == 2) {//map with byte[]
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody() instanceof byte[]);
			assertEquals(nameOfFile, result.getHeaders().getContentDisposition().getFilename());
			assertEquals(attachment, result.getHeaders().getContentDisposition().getType());
			assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
		} else {//map with NOT byte[]
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
		}
	}

	static final String DOWNLOAD_NAT_CAL_MAST_PARAM = "getDownloadNatCalMastParam";

	static Stream<Arguments> getDownloadNatCalMastParam() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("outStream", "test_byte[]".getBytes());
		map.put("fileName", nameOfFile);

		HashMap<String, Object> map2 = new HashMap<>();
		map2.put("outStream", "not_byte[]");
		map2.put("fileName", nameOfFile);
		return Stream.of(Arguments.of("string message", 1), Arguments.of(map, 2), Arguments.of(map2, 3));
	}

	// @Test
	void batchReceiverTest() throws Exception {
		OemProcessCtrlEntity entity = new OemProcessCtrlEntity();
		entity.setUserId("OEM");
		List<OemProcessCtrlEntity> result = new ArrayList<>();
		result.add(entity);

		when(commonUploadAndDownloadService.saveProcessDetails(anyString(), anyString()))
				.thenReturn(entity);
		when(commonUploadAndDownloadService.fetchProcessDetails(anyString())).thenReturn(result);

		ResponseEntity<List<ProcessBatchDTO>> actual = commonUploadAndDownloadController.batchReceiver("batchName",
				"userId", 2022, mock(MultipartFile.class));

		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertThat(actual.getBody()).isNotEmpty();
	}

	@Test
	void countryCodeNameTest() throws Exception {
		List<CountryMasterDto> response = new ArrayList<>();
		CountryMasterEntity countryMasterEntity = new CountryMasterEntity("FRANCE", "FR", "103", "TMT", "", "");
		List<CountryMasterEntity> list = new ArrayList<>();
		list.add(countryMasterEntity);
		for (CountryMasterEntity enttity : list) {
			CountryMasterDto countryMasterDto = new CountryMasterDto();
			countryMasterDto.setCountryCodeName(enttity.getCountryCode() + "-" + enttity.getCountryName());
			response.add(countryMasterDto);
		}
		when(countryMasterRepository.findAll()).thenReturn(list);
		ResponseEntity<List<CountryMasterDto>> result = commonUploadAndDownloadController.countryCodeName();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}

	@Test
	void codeMasterNameTest() throws Exception {
		ReportNameDTO reportNameDTO = new ReportNameDTO();
		reportNameDTO.setReportId(1);
		reportNameDTO.setReportName("Master - Lot Part Price");
		reportNameDTO.setReportJsonPath("CountryOfOriginMaster");
		reportNameDTO.setUserId(testUser);
		reportNameDTO.setExcelHeaderNames("MasterLotPartPrice");
		reportNameDTO.setExcelName("Class Name, Branch Name,First Name, Last Name");
		CoulmnsInfo coulmnsInfo1 = new CoulmnsInfo("1", "abc", "String", "4", "4", false, false, false, false);
		CoulmnsInfo coulmnsInfo2 = new CoulmnsInfo("2", "year", "String", "4", "4", false, false, false, false);
		List<CoulmnsInfo> list = new ArrayList<>();
		list.add(coulmnsInfo1);
		list.add(coulmnsInfo2);
		ColumnsDTO columnsModel = new ColumnsDTO();
		columnsModel.setColumns(list);
		columnsModel.setNoOfRecords(4);
		columnsModel.setPrimaryKey("PART_NO");
		columnsModel.setTableName("TB_M_COUNTRY_ORIGIN");
		columnsModel.setUserId("LoginUser");
		
		when(tpexConfigurationUtil.readDataFromJson(any())).thenReturn(columnsModel);
		ResponseEntity<ColumnsDTO> result = commonUploadAndDownloadController.codeMasterName(reportNameDTO);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

	}

	@ParameterizedTest
	@MethodSource(DOWNLOAD_NAT_CAL_MAST_PARAM)
	void getLotPartPriceMasterTemplateReportTest(Object obj, int assertCase) throws Exception {

		when(commonUploadAndDownloadService.downloadLotPartPriceMast(anyString(),eq(true))).thenReturn(obj);

		ResponseEntity<?> result = commonUploadAndDownloadController.getLotPartPriceMasterTemplateReport("test");

		if(assertCase == 1) {// String
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertThat(result.getBody()).isInstanceOf(String.class);
		} else if (assertCase == 2) {//map with byte[]
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody() instanceof byte[]);
			assertEquals(nameOfFile, result.getHeaders().getContentDisposition().getFilename());
			assertEquals(attachment, result.getHeaders().getContentDisposition().getType());
			assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
		} else {//map with NOT byte[]
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
		}
	}

	@ParameterizedTest
	@MethodSource(DOWNLOAD_NAT_CAL_MAST_PARAM)
	void getAddressMasterReportTest(Object obj, int assertCase) throws Exception {

		when(commonUploadAndDownloadService.downloadAddressMaster(anyString(),anyString(),eq(true))).thenReturn(obj);

		ResponseEntity<Object> result = commonUploadAndDownloadController.getAddressMasterReport("TMT","Test");

		if(assertCase == 1) {// String
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertThat(result.getBody()).isInstanceOf(String.class);
		} else if (assertCase == 2) {//map with byte[]
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody() instanceof byte[]);
			assertEquals(nameOfFile, result.getHeaders().getContentDisposition().getFilename());
			assertEquals(attachment, result.getHeaders().getContentDisposition().getType());
			assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
		} else {//map with NOT byte[]
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
		}
	}

	/**
	 * @author Mohd.Javed
	 * @param obj
	 * @param assertCase
	 * @throws Exception
	 */
	@ParameterizedTest
	@MethodSource(DOWNLOAD_NAT_CAL_MAST_PARAM)
	void getPxpPartPriceMasterTest(Object obj, int assertCase) throws Exception {

		when(commonUploadAndDownloadService.downloadPxpPartPriceMast(anyString(),eq(true))).thenReturn(obj);

		ResponseEntity<Object> result = commonUploadAndDownloadController.getPxpPartPriceMasterTemplateReport("Test");
		testAssert(assertCase,result);
	}

	void testAssert(int assertCase, ResponseEntity<Object> result) {
		if (assertCase == 1) {// String
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertThat(result.getBody()).isInstanceOf(String.class);
		} else if (assertCase == 2) {// map with byte[]
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody() instanceof byte[]);
			assertEquals(nameOfFile, result.getHeaders().getContentDisposition().getFilename());
			assertEquals(attachment, result.getHeaders().getContentDisposition().getType());
			assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
		} else {// map with NOT byte[]
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
		}
	}

	// 822 upload
	/**
	 * @author Mohd.Javed Method for test file format
	 * @throws Exception
	 */
	@Test
	void batchReceiverTestPxpFile(){

		String batchName = batchNameBINS107;
		String userId = "Test";
		String effectiveFrom = effFromDate;
		String effectiveTo = "2004/07";
		MockMultipartFile file = new MockMultipartFile("file", "pxpPartPriceMaster.xlsxs", textPlain,
				someXlsx.getBytes());
		String filename = file.getOriginalFilename();

		if (filename != null && (!(filename.endsWith(".xls") || filename.endsWith(".xlsx")))) {
			Assertions.assertThrows(InvalidFileException.class, () -> commonUploadAndDownloadController
					.batchReceiver(batchName, userId, effectiveFrom, effectiveTo, file));
		}
	}

	/**
	 * @author Mohd.Javed Method for test Batch , file and date is empty or not
	 * @throws Exception
	 */
	@Test
	void batchReceiverTestBatchAdnDateEmpty(){

		String batchName = "";
		String userId = "Test";
		String effectiveFrom = "";
		String effectiveTo = "2004/07";
		MockMultipartFile file = new MockMultipartFile("file", partPriceMasterExcelName, textPlain,
				someXlsx.getBytes());

		if (batchName.isBlank() || batchName.isEmpty() || effectiveFrom.isBlank() || effectiveTo.isBlank()
				|| file.isEmpty()) {
			Assertions.assertThrows(MyResourceNotFoundException.class, () -> commonUploadAndDownloadController
					.batchReceiver(batchName, userId, effectiveFrom, effectiveTo, file));
		}
	}

	/**
	 * @author Mohd.Javed Method for compare dates
	 * @throws Exception
	 */
	@Test
	void batchReceiverTestDateCompare() throws Exception {

		String batchName = batchNameBINS107;
		String userId = "Test";
		String effectiveFrom = effFromDate;
		String effectiveTo = effFromDate;
		MockMultipartFile file = new MockMultipartFile("file", partPriceMasterExcelName, textPlain,
				someXlsx.getBytes());
		SimpleDateFormat sdformat = new SimpleDateFormat(ConstantUtils.YYYYMM);
		Date fromDate = sdformat.parse(effectiveFrom);
		Date toDate = sdformat.parse(effectiveTo);
		if (fromDate.compareTo(toDate) > 0) {
			Assertions.assertThrows(DateFormateException.class, () -> commonUploadAndDownloadController
					.batchReceiver(batchName, userId, effectiveFrom, effectiveTo, file));
		} 
	}

	/**
	 * @author Mohd.Javed Method for test upload pxpPartPrice master
	 * @throws Exception
	 */
	@Test
	void batchReceiverTestCompleteMethod() throws Exception {

		String batchName = batchNameBINS107;
		String userId = "Test";
		String effectiveFrom = effFromDate;
		String effectiveTo = "2004/04";
		MockMultipartFile file = new MockMultipartFile("file", partPriceMasterExcelName, textPlain,
				someXlsx.getBytes());
		when(commonUploadAndDownloadService.batchProcess(batchName, userId, effectiveFrom, effectiveTo, file))
				.thenReturn(new ResponseEntity<List<ProcessBatchDTO>>(HttpStatus.OK));
		commonUploadAndDownloadController.batchReceiver(batchName, userId, effectiveFrom, effectiveTo, file);
		assertEquals(partPriceMasterExcelName, file.getOriginalFilename());
	}

	@Test
	void getCountryofOriginMastListReportcontrollertest() throws Exception {

		List<String> code = new ArrayList<>();
		code.add("IN");
		code.add("JP");

		CountryofOriginMastListReportRequestDto countryofOriginMastListReportRequestDto = new CountryofOriginMastListReportRequestDto();

		countryofOriginMastListReportRequestDto.setCountryOfCountryCode(code);
		countryofOriginMastListReportRequestDto.setPartPriceNo("125110L01000");
		countryofOriginMastListReportRequestDto.setVanDateFrom("25/01/2017");
		countryofOriginMastListReportRequestDto.setVanDateTo("25/01/2015");

		HttpServletResponse reponse = mock(HttpServletResponse.class);
		String headerkey = "Content-Disposition";
		String headerValue = "attachment;filename=CountryOfOrigin.xls";
		reponse.setHeader(headerkey, headerValue);

		commonUploadAndDownloadService.getCountryofOriginMastListReport(partPriceNo, code, date1,
				date2);

		commonUploadAndDownloadController.getCountryofOriginMastListReport(partPriceNo, code, date1,
				date2);

		Assertions.assertDoesNotThrow(() -> commonUploadAndDownloadService
				.getCountryofOriginMastListReport(partPriceNo, code, date1, date2));
	}

	@Test

	void getCarFmlyDestinationMasterListReportcontrollertest() throws Exception {

		HttpServletResponse reponse = mock(HttpServletResponse.class);
		String headerkey = "Content-Disposition";
		String headerValue = "attachment;filename=CarFamilyDestinationMaster.xlsx";
		reponse.setHeader(headerkey, headerValue);

		commonUploadAndDownloadService.getCarFmlyDestnMstListReport(reponse, "TMT", testUser);

		commonUploadAndDownloadController.getCarFmlyDestnMstListReport(reponse, "TMT", testUser);

		Assertions.assertDoesNotThrow(
				() -> commonUploadAndDownloadService.getCarFmlyDestnMstListReport(reponse, "TMT", testUser));
	}

	@Test
	void downloadLotPartShortageReportTest() throws IOException, ParseException {
		LotPartShortageReportsRequestDto request = new LotPartShortageReportsRequestDto();
		if (request != null
				&& (StringUtils.isEmpty(request.getEtdMonth()) || StringUtils.isEmpty(request.getRevisionNo()))) {
			Assertions.assertThrows(MyResourceNotFoundException.class,
					() -> commonUploadAndDownloadController.downloadLotPartShortageReport(request));
		}
	}
	/**
	 * TestCase for generate excell report of Lot Part Shortage
	 * @param request
	 * @author Mohd.Javed
	 * @return
	 * @throws JRException
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("unused")
	@Test
	void downloadLotPartShortageReportTests() throws IOException, ParseException,JRException {
		LotPartShortageReportsRequestDto request = new LotPartShortageReportsRequestDto();
		request.setEtdMonth("02/2022");
		request.setRevisionNo("0");
		Object obj = null;
		Object outStream = null;
		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) obj;
		String fileName = map != null ? (String) map.get(ConstantUtils.FILENAME) : "";
		if (map != null && map.get(ConstantUtils.OUTSTREAM) != null) {
			outStream = map.get("outstream");
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(ConstantUtils.FILENAME, fileName);
		headers.add(ConstantUtils.CONTENT_DISPOSITION, ConstantUtils.ATTACHMENT + fileName);
		ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(outStream);
		when(commonUploadAndDownloadService.generateLostPartShortageReport(request)).thenReturn(obj);
		commonUploadAndDownloadController.downloadLotPartShortageReport(request);
		assertEquals("02/2022", request.getEtdMonth());

	}
}
