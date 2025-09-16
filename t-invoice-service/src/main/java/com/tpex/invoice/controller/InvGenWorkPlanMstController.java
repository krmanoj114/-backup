package com.tpex.invoice.controller;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.BrokerDetailsDTO;
import com.tpex.dto.InvGenWorkPlanMstDTO;
import com.tpex.dto.NoemHaisenDtlsEntityDTO;
import com.tpex.dto.NoemRenbanSetupMstDTO;
import com.tpex.dto.OemFnlDstMstDto;
import com.tpex.dto.PortOfLoadingAndDischargeDetailsDTO;
import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.entity.NoemCbMstEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.OemPortMstEntity;
import com.tpex.invoice.service.InvGenWorkPlanMstService;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantProperties;
import com.tpex.util.ConstantUtils;

import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class InvGenWorkPlanMstController {

	private static final String SAVED_REPORT_ID = "savedReportId";

	private static final String TEST_USER = "TestUser";

	@Autowired
	InvGenWorkPlanMstService invGenWorkPlanMsteService;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Autowired
	ConstantProperties constantProperties;

	@GetMapping(value = "/destCodeAndDestName", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OemFnlDstMstDto>> fetchDestCodeAndDestName() {
		List<OemFnlDstMstEntity> destList = invGenWorkPlanMsteService.destinationCodeList();
		List<OemFnlDstMstDto> destCodeAndName = destList.stream()
				.map(u -> new OemFnlDstMstDto(u.getFdDstNm(), u.getFdDstCd())).collect(Collectors.toList());
		return new ResponseEntity<>(destCodeAndName, HttpStatus.OK);
	}

	@PostMapping(value = "/saveInvoice", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> saveInvoice(
			@RequestBody List<InvGenWorkPlanMstDTO> listInvGenWorkPlanMstDTO)  {
		boolean isSaved = invGenWorkPlanMsteService.saveInvoice(listInvGenWorkPlanMstDTO);
		if (isSaved)
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_IN_1001),
					HttpStatus.OK);
		else {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR,
					ConstantUtils.ERR_CM_3006, errorMessageParams), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/invoiceDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> fetchInvoiceDetails(
			@RequestBody InvGenWorkPlanMstDTO invGenWorkPlanMstDTO, @RequestParam int pageNo,
			@RequestParam int pageSize) throws ParseException {

		pageNo = (pageNo == 0)
				? Integer.parseInt(tpexConfigRepository.findByName("invGenWorkPlanMst.paging.pageNo").getValue())
				: pageNo;
		pageSize = (pageSize == 0)
				? Integer.parseInt(tpexConfigRepository.findByName("invGenWorkPlanMst.paging.pageSize").getValue())
				: pageSize;

		List<InvGenWorkPlanMstEntity> listOfInvGenWorkPlanMstEntity = null;

		Page<InvGenWorkPlanMstEntity> pageinvList = invGenWorkPlanMsteService.fetchInvoiceDetails(pageNo, pageSize,
				invGenWorkPlanMstDTO.getIssueInvoiceFromDate(), invGenWorkPlanMstDTO.getIssueInvoiceToDate(),
				invGenWorkPlanMstDTO.getEtd1fromDate(), invGenWorkPlanMstDTO.getEtd1ToDate(),
				invGenWorkPlanMstDTO.getContDest());

		listOfInvGenWorkPlanMstEntity = pageinvList.getContent();
		List<NoemCbMstEntity> listofNoemCbMstEntity = invGenWorkPlanMsteService.getBrokerDetails();
		Map<String, String> mapOfbrokerDetailsDTO = listofNoemCbMstEntity.stream()
				.collect(Collectors.toMap(x -> x.getCbCd(), x -> x.getCbNm()));
		List<BrokerDetailsDTO> listOfbrokerDetailsDTO = listofNoemCbMstEntity.stream()
				.map(u -> new BrokerDetailsDTO(u.getCbCd(), u.getCbNm())).collect(Collectors.toList());
		List<OemPortMstEntity> listofOemPortMstEntity = invGenWorkPlanMsteService.getPortOfLoadingAndDischargeDetails();
		List<PortOfLoadingAndDischargeDetailsDTO> listOfportLoadingAndDischargeDetailsDTO = listofOemPortMstEntity
				.stream().map(u -> new PortOfLoadingAndDischargeDetailsDTO(u.getCd(), u.getName()))
				.collect(Collectors.toList());
		Map<String, Object> response = new HashMap<>();

		for (InvGenWorkPlanMstEntity entity : listOfInvGenWorkPlanMstEntity) {
			if (mapOfbrokerDetailsDTO.get(entity.getBroker()) != null)
				entity.setBroker(mapOfbrokerDetailsDTO.get(entity.getBroker()));
		}
		response.put("invoiceDetails", listOfInvGenWorkPlanMstEntity);
		response.put("brokerDetails", listOfbrokerDetailsDTO);
		response.put("portDetails", listOfportLoadingAndDischargeDetailsDTO);
		response.put("currentPage", pageinvList.getNumber());
		response.put("totalItems", pageinvList.getTotalElements());
		response.put("totalPages", pageinvList.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@GetMapping(value = "/downloadInvoiceGenPlan", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> downloadInvoiceGenPlan(@RequestParam(defaultValue = "") String issueInvoiceFromDate,
			@RequestParam(defaultValue = "") String issueInvoiceToDate,
			@RequestParam(defaultValue = "") String etd1fromDate, @RequestParam(defaultValue = "") String etd1ToDate,
			@RequestParam(defaultValue = "") List<String> contDest, @RequestParam(defaultValue = "") String createBy)
			throws ParseException, FileNotFoundException, JRException {
		Object obj = null;
		Map<String, Object> response = new HashMap<>();

		int reportSizeLimit = Integer
				.parseInt(tpexConfigRepository.findByName("invoiceGeneration.report.size.limit").getValue());
		Map<String, Integer> map1 = this.invGenWorkPlanMsteService.getCountReportDataToDownload(issueInvoiceFromDate,
				issueInvoiceToDate, etd1fromDate, etd1ToDate, contDest, TEST_USER);

		if (map1.get("statusCode") != null && map1.get("statusCode") == 250) {
			response.put("message", ConstantUtils.ERR_IN_1015);
			response.put("status", "offline");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		// If file size is greater than configured size then store file in directory and
		// return path
		if (map1.get("rowCount") > reportSizeLimit) {
			this.invGenWorkPlanMsteService.downloadReportOffline(issueInvoiceFromDate, issueInvoiceToDate,
					etd1fromDate, etd1ToDate, contDest, "", TEST_USER, map1.get(SAVED_REPORT_ID));
			response.put("message", ConstantUtils.INFO_IN_1004);
			response.put("status", "offline");
			return new ResponseEntity<>(response, HttpStatus.OK);

		} else {
			// If file size is less than configured size then download file directly
			obj = this.invGenWorkPlanMsteService.downloadReportOnline(issueInvoiceFromDate, issueInvoiceToDate,
					etd1fromDate, etd1ToDate, contDest, "", TEST_USER);
			HashMap<String, Object> map = (HashMap<String, Object>) obj;
			String fileName = (String) map.get("fileName");
			HttpHeaders headers = new HttpHeaders();
			headers.add("filename", fileName);
			headers.add("Content-Disposition", "attachment; filename= " + fileName);
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(map.get("outStream"));
		}
	}

	@GetMapping(value = "/renbanCodesByContDstCode", produces = MediaType.APPLICATION_JSON_VALUE)
	// Fetch Renban codes based on container destination code
	public ResponseEntity<List<NoemRenbanSetupMstDTO>> fetchRenbanCodesByContDstCd(
			@RequestParam(required = true) String contDstCode) {
		return new ResponseEntity<>(invGenWorkPlanMsteService.fetchRenbanCodesByContDstCd(contDstCode), HttpStatus.OK);
	}

	@PostMapping(value = "/generateHaisenNo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> generateHaisenNo(@RequestBody NoemHaisenDtlsEntityDTO noemHaisenDtlsEntity) {
		Map<String, Object> haisenNoResponse = invGenWorkPlanMsteService.generateHaisenNo(noemHaisenDtlsEntity);
		if (haisenNoResponse.isEmpty()) {
			return new ResponseEntity<>(ConstantUtils.HAISEN_NO_ALREADY_EXIST, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(ConstantUtils.HAISEN_NO_GENERATED + haisenNoResponse, HttpStatus.OK);
		}
	}

}
