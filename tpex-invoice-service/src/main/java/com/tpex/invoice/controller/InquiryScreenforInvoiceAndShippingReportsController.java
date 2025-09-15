package com.tpex.invoice.controller;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.dto.DestinationCodeAndReportTypesDTO;
import com.tpex.dto.InvoiceMaintenanceDTO;
import com.tpex.dto.OemFnlDstMstDto;
import com.tpex.dto.ReportTypeDTO;
import com.tpex.dto.ShippingContResultDTO;
import com.tpex.entity.InsInvDtlsEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.invoice.dto.ShippingContSearchInputDTO;
import com.tpex.invoice.service.CertificateOriginReportService;
import com.tpex.invoice.service.DownloadInvCntnrLvlReportService;
import com.tpex.invoice.service.DownloadInvoiceReportsService;
import com.tpex.invoice.service.InvGenWorkPlanMstService;
import com.tpex.invoice.service.InvMaintenanceSearchService;
import com.tpex.invoice.service.InvPackingListService;
import com.tpex.invoice.service.InvSearchService;
import com.tpex.invoice.service.InvVinListService;
import com.tpex.invoice.service.InvoiceCoverPageService;
import com.tpex.invoice.service.InvoiceHeaderPageService;
import com.tpex.invoice.service.MpmiInvoiceCoverPageService;
import com.tpex.invoice.service.SCInvAttachedSheetService;
import com.tpex.invoice.service.SeparateInvoiceGenerationService;
import com.tpex.invoice.service.WorkInstructionReportService;
import com.tpex.invoice.serviceimpl.DgInvoicePackingListService;
import com.tpex.invoice.serviceimpl.PackingListCustomBrokerServiceImpl;
import com.tpex.repository.InvoiceMaintenanceRepository;
import com.tpex.repository.SeperateInvoiceGenRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.TpexConfigurationUtil;

import net.sf.jasperreports.engine.JRException;


/**
 * The Class InquiryScreenforInvoiceAndShippingReportsController.
 */
@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class InquiryScreenforInvoiceAndShippingReportsController {

	@Autowired
	InvVinListService invVinListService;

	@Autowired
	InvGenWorkPlanMstService invGenWorkPlanMsteService;

	@Autowired
	InvMaintenanceSearchService invMaintenanceSearchService;

	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;

	@Autowired
	DownloadInvoiceReportsService downloadInvoiceReportsService;

	@Autowired
	InvoiceMaintenanceRepository invoiceMaintenanceRepository;

	@Autowired
	InvoiceHeaderPageService invoiceHeaderPageService;

	@Autowired
	InvPackingListService invPackingListService;

	@Autowired
	CertificateOriginReportService certificateOriginReportService;

	@Autowired
	WorkInstructionReportService workInstructionReportService;

	@Autowired
	InvSearchService invSearchService;

	@Autowired
	SCInvAttachedSheetService scInvAttachedSheetService;

	@Autowired
	DgInvoicePackingListService dgInvoicePackingListService;

	@Autowired
	PackingListCustomBrokerServiceImpl packingListCustomBrokerServiceImpl;

	@Autowired
	InvoiceCoverPageService invoiceCoverPageService;

	@Autowired
	MpmiInvoiceCoverPageService mpmiInvoiceCoverPageService;
	
	@Autowired
	DownloadInvCntnrLvlReportService downloadInvCntnrLvlReportService;

	@Autowired
	SeparateInvoiceGenerationService separateInvoiceGenerationService;
	
	@Autowired
	SeperateInvoiceGenRepository seperateInvoiceGenRepository;

	private static final String DEST = "destination";
	private static final String BOOKINNO = "bookingNo";
	private static final String REPORTFORMAT = "reportFormat";
	private static final String USERID = "userId";


	/**
	 * Fetch report types.
	 *
	 * @return the response entity
	 * @throws IOException 
	 * @throws DatabindException 
	 * @throws StreamReadException 
	 * @throws Exception the exception
	 */
	@GetMapping(value = "/destinationAndShippingReports", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DestinationCodeAndReportTypesDTO> fetchReportTypes()
			throws  IOException  {
		DestinationCodeAndReportTypesDTO destinationCodeAndReportTypesDTO = new DestinationCodeAndReportTypesDTO();

		List<OemFnlDstMstEntity> destList = invGenWorkPlanMsteService.destinationCodeList();
		List<OemFnlDstMstDto> destCodeAndName = destList.stream()
				.map(u -> new OemFnlDstMstDto(u.getFdDstNm(), u.getFdDstCd())).collect(Collectors.toList());

		ObjectMapper objectMapper = new ObjectMapper();
		String filePath = tpexConfigurationUtil.getFilePath(ConstantUtils.REPORT_TYPES_JSON_FOR_INQUIRYSCREEN);
		File file = ResourceUtils.getFile(filePath);
		if(!file.exists()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.FILEPATH, filePath);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018, errorMessageParams);
		}

		List<ReportTypeDTO> sampleObject = Arrays.asList(objectMapper.readValue(file, ReportTypeDTO[].class));	

		destinationCodeAndReportTypesDTO.setDestinations(destCodeAndName);
		destinationCodeAndReportTypesDTO.setReports(sampleObject);

		return new ResponseEntity<>(destinationCodeAndReportTypesDTO, HttpStatus.OK);

	}

	/**
	 * On load invoice maintenance search.
	 *
	 * @return the response entity
	 */
	@GetMapping(value = "/orderTypeAndInvoiceNo", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<InvoiceMaintenanceDTO>> onLoadInvoiceMaintenanceSearch(){		
		List<InvoiceMaintenanceDTO> list = invMaintenanceSearchService.fetchOrderTypeAndInvoiceNumber();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	/**
	 * Inquiry Screen for Invoice & Shipping Reports- Print
	 *
	 * @return 
	 * @throws IOException 
	 * @throws DatabindException 
	 * @throws StreamReadException 
	 * @throws ParseException 
	 * @throws JRException 
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value="/downloadInvoiceReports")
	@CrossOrigin(exposedHeaders = { "filename" })
	public ResponseEntity<Object> downloadInvoiceReports(@RequestBody DownloadInvoiceReportsRequestDTO request) throws IOException, JRException, ParseException  {

		String[] reportIds = request.getReportTypes();

		List<Map<String, String>> responseList = new ArrayList<>();

		ObjectMapper objectMapper = new ObjectMapper();
		String filePath = tpexConfigurationUtil.getFilePath(ConstantUtils.REPORT_TYPES_JSON_FOR_INQUIRYSCREEN);
		File file = ResourceUtils.getFile(filePath);
		if(!file.exists()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.FILEPATH, filePath);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018, errorMessageParams);
		} 

		List<ReportTypeDTO> sampleObject = Arrays.asList(objectMapper.readValue(file, ReportTypeDTO[].class));	

		Map<String,String> reportNames = new LinkedHashMap<>(); 

		for(ReportTypeDTO r : sampleObject) {
			reportNames.put(r.getReportTypeId(),r.getReportTypeName());
		}

		for(String reportId:reportIds) {

			Object outStream = null;
			if (reportId.equals(ConstantUtils.RINS110)) {
				if(StringUtils.isBlank(request.getEtd()) && (request.getDestinations() != null))
				{
					throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
				}
				byte[] byteResponse = this.downloadInvCntnrLvlReportService.generateContainerLevelReport(reportId,
						request.getReportFormate(), request.getEtd(), request.getDestinations());
				String reportNameAddParameter = this.downloadInvCntnrLvlReportService.getReportfeildItems(request.getEtd(), request.getDestinations());
				StringBuilder fileNameConainerReport = new StringBuilder();
				fileNameConainerReport.append(ConstantUtils.INVOICE_CONTAINER_LEVEL_FILENAME);
				fileNameConainerReport.append(reportNameAddParameter);
				fileNameConainerReport.append(ConstantUtils.INVOICE_CONTAINER_LEVEL_EXTENSION);
				HttpHeaders headers = new HttpHeaders();
				headers.add(ConstantUtils.FILENAME,fileNameConainerReport.toString());
				headers.add(ConstantUtils.CONTENTDISPOSITION,
						ConstantUtils.ATTACHMENT + fileNameConainerReport.toString());
				return ResponseEntity.ok()
						.headers(headers)
						.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(byteResponse);
				}

			Object responseObject = downloadInvoiceReports(reportId, request);

			if(responseObject!=null) {

				HashMap<String, Object> map = (HashMap<String, Object>) responseObject;

				String fileName =(String) map.get(ConstantUtils.FILENAME);

				if(map.get(ConstantUtils.OUTSTREAM) != null){

					outStream = map.get(ConstantUtils.OUTSTREAM);

					HttpHeaders headers = new HttpHeaders();
					headers.add(ConstantUtils.FILENAME, fileName);
					headers.add(ConstantUtils.CONTENTDISPOSITION, ConstantUtils.ATTACHMENT+fileName);

					return ResponseEntity.ok()
							.headers(headers)
							.contentType(MediaType.APPLICATION_OCTET_STREAM)
							.body(outStream);
				}
			}
		}
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	private Object downloadInvoiceReports(String reportId, DownloadInvoiceReportsRequestDTO request) throws FileNotFoundException, JRException, ParseException {
		Object responseObject = null;

		if(reportId.equals(ConstantUtils.RINS104_DG) || reportId.equals("11") || reportId.equals("RINS105")||reportId.equals(ConstantUtils.RINS002DG)) {
			responseObject = downloadDGInvoiceReports(reportId, request);
		} 
		else{
			responseObject= downloadInvoiceReportsForInvoiceNumber(reportId, request);
		}

		return responseObject;
	}

	private Object downloadDGInvoiceReports(String reportId, DownloadInvoiceReportsRequestDTO request) throws FileNotFoundException, ParseException, JRException {
		Object responseObject = new Object();
		if((StringUtils.isBlank(request.getEtd()) || StringUtils.isAllBlank(request.getDestinations())) && !(reportId.equals(ConstantUtils.RINS104_DG) || reportId.equals(ConstantUtils.RINS002DG)))
		{
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}
		if((StringUtils.isBlank(request.getEtd()) || StringUtils.isAllBlank(request.getDestinations())) && reportId.equals(ConstantUtils.RINS104_DG))
		{
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}

		if(reportId.equals(ConstantUtils.RINS105)) {
			String contDestination = request.getDestinations()[0];
			responseObject =  this.workInstructionReportService.getRegularWrkInstructionRptDownload(request.getEtd(),contDestination,request.getBookingNo(),request.getUserId(),request.getReportFormate(), reportId);
		}

		if(reportId.equals(ConstantUtils.RINS104_DG)) {
			responseObject =  dgInvoicePackingListService.downloadDgInvoicePackingListReport(request.getBookingNo(), request.getEtd(), request.getEtdTo(), request.getDestinations()[0], request.getReportFormate(),reportId);
		}
		// tpex-187
		if (reportId.equals(ConstantUtils.RINS002DG)) {
			responseObject =  dgInvoicePackingListService.download2DgInvoicePackingListReport(request.getEtd(),
					request.getDestinations()[0], request.getReportFormate(), request.getInvoiceNumber(),
					reportId, request.getOrderType(), request.getCmpCd(), request.getUserId());
		}
		return responseObject;
	}

	private Object downloadInvoiceReportsForInvoiceNumber(String reportId, DownloadInvoiceReportsRequestDTO request) throws FileNotFoundException, JRException, ParseException {
		Object responseObject = new Object();

		if(StringUtils.isBlank(request.getInvoiceNumber()))
		{
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}

		List<String> dbinvoiceNumberList = invoiceMaintenanceRepository.getAllInvoiceNo();

		if(!dbinvoiceNumberList.contains(request.getInvoiceNumber()))
		{
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1006);
		}
		if(reportId.equals(ConstantUtils.RINS001A)) {
			responseObject =  this.invoiceHeaderPageService.getAttachedInvHeaderPageRptDownload(request.getInvoiceNumber(), reportId, request.getReportFormate());
		}
		if(reportId.equals(ConstantUtils.RINS002)) {
			responseObject =  this.invPackingListService.getInvPackingListRptDownload(request.getInvoiceNumber(), reportId, request.getReportFormate());
		}
		if(reportId.equals(ConstantUtils.RINS003B)) {
			String cmpCd = "TMT";
			responseObject =  this.invSearchService.downloadPINS103(cmpCd, reportId, request.getInvoiceNumber(), request.getUserId(), request.getReportFormate());
		}

		if(reportId.equals(ConstantUtils.RINS003D)) {

			Optional<InsInvDtlsEntity> entity = invoiceMaintenanceRepository.findById(request.getInvoiceNumber());

			if(entity.isPresent() && (StringUtils.isBlank(entity.get().getIndScInvFlag()) || entity.get().getIndScInvFlag().equals("N"))) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1007);
			}

			String cmpCd = "TMT";
			responseObject =  this.scInvAttachedSheetService.downloadPINS103forSc(cmpCd, request.getInvoiceNumber(), request.getUserId(), request.getReportFormate(), reportId);
		}
		if(reportId.equals(ConstantUtils.RINS005)) {
			String cmpCd="TMT";
			responseObject =  this.invVinListService.getInvVinListReportDownload(cmpCd, request.getInvoiceNumber(), request.getUserId(), reportId, request.getReportFormate());
		}
		if(reportId.equals(ConstantUtils.RINS002A)) {
			String pidUserId="NIITTMT";
			responseObject =  this.packingListCustomBrokerServiceImpl.generateReport(request.getInvoiceNumber(),reportId,pidUserId,request.getReportFormate());
		}
		if(reportId.equals(ConstantUtils.RINS106)) {
			String cmpCd="TMT";
			responseObject =  this.certificateOriginReportService.getCertificateOriginReportDownload(cmpCd, request.getInvoiceNumber(), request.getUserId(), reportId, request.getReportFormate());
		}
		if(reportId.equals(ConstantUtils.RINS001)) {
			responseObject =  this.invoiceCoverPageService.getInvoiceCoverPageRptDownload(request.getInvoiceNumber(), reportId, request.getReportFormate());
		}
		if(reportId.equals(ConstantUtils.RINS0011B)) {
			responseObject =  this.mpmiInvoiceCoverPageService.getInvoiceCoverPageRptDownload(request.getInvoiceNumber(), reportId, request.getReportFormate());
		}

		String countryOfOrigin = seperateInvoiceGenRepository.getCountryOfOrigin();
		responseObject = createResponseForSeparateInvoiceGen(reportId, request, responseObject, countryOfOrigin);

		return responseObject;
	}

	private Object createResponseForSeparateInvoiceGen(String reportId, DownloadInvoiceReportsRequestDTO request,
			Object responseObject, String countryOfOrigin) throws FileNotFoundException, JRException, ParseException {
		if ("Y".equals(countryOfOrigin)) {
			if (reportId.equals(ConstantUtils.RINS012)) {
				responseObject = this.separateInvoiceGenerationService.getSeparateInvoiceGeneration(request.getCmpCd(),
						request.getInvoiceNumber(), request.getUserId(), reportId, request.getReportFormate(),
						request.getInvoiceType());
			}
		} else {
			if (reportId.equals(ConstantUtils.RINS007)) {
				responseObject = this.separateInvoiceGenerationService.getSeparateInvoiceGeneration(request.getCmpCd(),
						request.getInvoiceNumber(), request.getUserId(), reportId, request.getReportFormate(),
						request.getInvoiceType());
			}
		}
		return responseObject;
	}

	/**
	 * Inquiry Screen for Invoice & Shipping Reports- Print
	 *
	 * @return 
	 * @throws IOException 
	 * @throws DatabindException 
	 * @throws StreamReadException 
	 * @throws JRException 
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/downloadExportedReports", produces=MediaType.APPLICATION_JSON_VALUE)
	public  ResponseEntity<Object> downloadExportedReports(@RequestParam(defaultValue = "") String userId, 
			@RequestParam(defaultValue = "") String orderType, @RequestParam(defaultValue = "") String invoiceNumber, 
			@RequestParam String reportId, @RequestParam(defaultValue = "") String etd,  @RequestParam(defaultValue = "") String etdTo,
			@RequestParam String destination, @RequestParam(defaultValue = "") String reportFormat, @RequestParam(defaultValue = "") String bookingNo) throws IOException, ParseException, JRException{


		Object responseObject = null;
		ObjectMapper objectMapper = new ObjectMapper();
		String filePath = tpexConfigurationUtil.getFilePath(ConstantUtils.REPORT_TYPES_JSON_FOR_INQUIRYSCREEN);
		File file = ResourceUtils.getFile(filePath);
		if(!file.exists()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.FILEPATH, filePath);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018, errorMessageParams);
		} 

		List<ReportTypeDTO> sampleObject = Arrays.asList(objectMapper.readValue(file, ReportTypeDTO[].class));	

		Map<String,String> reportNames = new LinkedHashMap<>(); 

		for(ReportTypeDTO r : sampleObject) {
			reportNames.put(r.getReportTypeId(),r.getReportTypeName());
		}

		if(reportId.equals(ConstantUtils.RINS104_DG) || reportId.equals(ConstantUtils.RINS002DG)  || reportId.equals(ConstantUtils.RINS105)) {
			Map<String, String> inputMap = new HashMap<>();
			inputMap.put("reportId", reportId);
			inputMap.put("etd", etd);
			inputMap.put("etdTo", etdTo);
			inputMap.put(destination, destination);
			inputMap.put("invoiceNumber", invoiceNumber);
			inputMap.put(bookingNo, bookingNo);
			inputMap.put(reportFormat, reportFormat);
			inputMap.put(userId, userId);

			responseObject = getDGExportedReports(inputMap);
		} 
		else
		{
			responseObject = getExportedReportsForInvoiceNumber(reportId, invoiceNumber, reportFormat, userId);
		}
		HashMap<String, Object> map = (HashMap<String, Object>) responseObject;
		Object outStream = null;
		String fileName = map != null ? (String) map.get(ConstantUtils.FILENAME) : "";
		if(map != null && map.get(ConstantUtils.OUTSTREAM) != null){
			outStream = map.get(ConstantUtils.OUTSTREAM);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(ConstantUtils.FILENAME, fileName);
		headers.add(ConstantUtils.CONTENTDISPOSITION, ConstantUtils.ATTACHMENT+fileName);
		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(outStream);

	}

	private Object getDGExportedReports(Map<String, String> inputMap) throws FileNotFoundException, ParseException, JRException {
		Object responseObject = new Object();
		String reportId = inputMap.get("reportId");
		if((StringUtils.isBlank(inputMap.get("etd")) || StringUtils.isAllBlank(inputMap.get(DEST)) && !(reportId.equals(ConstantUtils.RINS104_DG) || reportId.equals(ConstantUtils.RINS002DG))))
		{
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}
		if(reportId.equals(ConstantUtils.RINS105)) {
			responseObject = this.workInstructionReportService.getRegularWrkInstructionRptDownload(inputMap.get("etd"),inputMap.get(DEST),inputMap.get(BOOKINNO),inputMap.get(USERID),inputMap.get(REPORTFORMAT), reportId);

		}else if(reportId.equals(ConstantUtils.RINS104_DG)) {
			responseObject = dgInvoicePackingListService.downloadDgInvoicePackingListReport(inputMap.get(BOOKINNO), inputMap.get("etd"),inputMap.get("etdTo"),inputMap.get(DEST),inputMap.get(REPORTFORMAT),reportId);

		}else if(reportId.equals(ConstantUtils.RINS002DG)) {
			responseObject = dgInvoicePackingListService.download2DgInvoicePackingListReport(inputMap.get("etd"), inputMap.get(DEST), inputMap.get(REPORTFORMAT), inputMap.get("invoiceNumber"), reportId, inputMap.get("orderType"), null, inputMap.get(USERID));
		}
		return responseObject;
	}

	private Object getExportedReportsForInvoiceNumber(String reportId, String invoiceNumber, String reportFormat, String userId) throws FileNotFoundException, JRException {
		Object responseObject = new Object();

		if(StringUtils.isBlank(invoiceNumber))
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);

		List<String> dbinvoiceNumberList = invoiceMaintenanceRepository.getAllInvoiceNo();

		if(!dbinvoiceNumberList.contains(invoiceNumber))
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1006);

		if(reportId.equals(ConstantUtils.RINS001A)) {
			responseObject = this.invoiceHeaderPageService.getAttachedInvHeaderPageRptDownload(invoiceNumber, reportId, reportFormat);
		}
		else if(reportId.equals(ConstantUtils.RINS002)) {
			responseObject = this.invPackingListService.getInvPackingListRptDownload(invoiceNumber, reportId, reportFormat);
		}
		else if(reportId.equals(ConstantUtils.RINS003B)) {
			String cmpCd = "TMT";
			responseObject = this.invSearchService.downloadPINS103(cmpCd, reportId, invoiceNumber, userId, reportFormat );
		}
		else if(reportId.equals(ConstantUtils.RINS003D)) {
			String cmpCd = "TMT";
			responseObject = this.scInvAttachedSheetService.downloadPINS103forSc(cmpCd,  invoiceNumber, userId, reportFormat,reportId);
		}
		else if(reportId.equals(ConstantUtils.RINS005)) {
			String cmpCd="TMT";
			responseObject = this.invVinListService.getInvVinListReportDownload(cmpCd, invoiceNumber, userId, reportId, reportFormat);
		}
		else if(reportId.equals(ConstantUtils.RINS002A)) {
			String pidUserId="NIITTMT";
			responseObject = this.packingListCustomBrokerServiceImpl.generateReport(invoiceNumber,reportId,pidUserId,reportFormat);
		}
		else if(reportId.equals(ConstantUtils.RINS106)) {
			String cmpCd="TMT";
			responseObject = this.certificateOriginReportService.getCertificateOriginReportDownload(cmpCd, invoiceNumber, userId, reportId, reportFormat);
		}
		else if(reportId.equals(ConstantUtils.RINS001)) {
			responseObject = this.invoiceCoverPageService.getInvoiceCoverPageRptDownload(invoiceNumber, reportId, reportFormat);
		}
		else if(reportId.equals(ConstantUtils.RINS0011B)) {
			responseObject = this.mpmiInvoiceCoverPageService.getInvoiceCoverPageRptDownload(invoiceNumber, reportId, reportFormat);
		}
		return responseObject;
	}



	/**
	 * @param shippingContSearchInputDTO
	 * @return
	 * @throws ParseException
	 */
	@PostMapping(value= "/invShippingContainerResults", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ShippingContResultDTO>> shippingResults(@RequestBody ShippingContSearchInputDTO shippingContSearchInputDTO)  {
		return new ResponseEntity<>(invGenWorkPlanMsteService.shippingResults(shippingContSearchInputDTO), HttpStatus.OK);
	}

}
