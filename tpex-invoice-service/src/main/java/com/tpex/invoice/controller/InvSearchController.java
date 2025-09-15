package com.tpex.invoice.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.SearchInvHaisenDetailResponse;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InvoiceDetailsResponseWrapper;
import com.tpex.invoice.dto.OrderType;
import com.tpex.invoice.dto.SearchByInvNoResponseDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailRequestDto;
import com.tpex.invoice.dto.UpdateInvDetailsRequestDTO;
import com.tpex.invoice.service.InvSearchService;
import com.tpex.repository.InsInvDetailsRepository;
import com.tpex.repository.NoemHaisenDtlsRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.validator.SearchInvHaisenDetailRequestDtoValidation;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class InvSearchController {

	@Autowired
	InvSearchService invSearchService;

	@Autowired
	NoemHaisenDtlsRepository noemHaisenDtlsRepository;

	@Autowired
	InsInvDetailsRepository insInvDetailsRepository;

	@Autowired
	SearchInvHaisenDetailRequestDtoValidation searchInvHaisenDetailRequestDtoValidation;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	/**
	 * This method is used for search data based on ETD and ETA dates and buyer and
	 * regular fields
	 * 
	 * @param searchInvHaisenDetailRequestDto
	 * @return SearchInvHaisenDetailResponse
	 * @throws Exception
	 */

	@PostMapping(value = "/searchInvoice")
	public ResponseEntity<SearchInvHaisenDetailResponse> fetchHaisenDetails(
			@Valid @RequestBody SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto) throws Exception {

		LocalDate etd = null;
		LocalDate eta = null;
		String buyer = null;
		OrderType orderType = null;
		String regular = null;
		String cpo = null;
		String spo = null;

		if (searchInvHaisenDetailRequestDto != null) {
			etd = searchInvHaisenDetailRequestDto.getEtdFrom();
			eta = searchInvHaisenDetailRequestDto.getEtdTo();
			buyer = searchInvHaisenDetailRequestDto.getBuyer();
			orderType = searchInvHaisenDetailRequestDto.getOrderType();
			if (orderType.isRegular() && orderType.isCpoOrspo()) {
				regular = "R";
				cpo = "C";
				spo = "S";
			} else if (orderType.isCpoOrspo()) {
				cpo = "C";
				spo = "S";
			} else if (orderType.isRegular()) {
				regular = "R";
			} else {
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1001);
			}
			if (eta == null) {
				eta = LocalDate.now();
			}
		}
		if (eta != null && etd != null && etd.compareTo(eta) > 0) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3007);
		}
		if (buyer != null && !"".equalsIgnoreCase(buyer)) {
			int count = noemHaisenDtlsRepository.getDetailsByBuyer(buyer);
			if (count == 0)
				throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		SearchInvHaisenDetailResponse response = invSearchService.fetchHaisenDetail(searchInvHaisenDetailRequestDto,
				regular, cpo, spo);
		if (response == null) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * this method used for fetch data based on invoice no
	 * 
	 * @param envNo
	 * @return
	 * @throws Exception
	 * @author Mohd.Javed
	 */
	@GetMapping(value = "/searchByInvoiceNo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchByInvNoResponseDto> getByInvoiceNo(@Valid @RequestParam(required = true) String envNo)
			throws Exception {
		String[] invoiceNoWithImporterCode = envNo.split("-");
		SearchByInvNoResponseDto response = invSearchService.searchByInvNo(invoiceNoWithImporterCode[0]);
		if (response == null) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Method for get Invoice details by haisen no
	 * 
	 * @author Mohd.Javed
	 * @param envNo
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/getInvDtlsByHaisenNo")
	public ResponseEntity<InvoiceDetailsResponseWrapper> getInvDtls(
			@Valid @RequestParam(required = true) String haisenNo, @RequestParam(required = true) String haisenYear)
			throws Exception {
//    update
		InvoiceDetailsResponseWrapper response = invSearchService.getInvoiceDetails(haisenNo, haisenYear);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	/**
	 * @author R.1.Reddy
	 * @param SearchByInvNoResponseDto
	 * @return Success Message
	 * @throws Exception
	 */
	@PutMapping("/updateInvDetailsByInvNo")
	public ResponseEntity<ApiResponseMessage> updateInvoiceSearchResponseAndSave(@Valid @RequestBody UpdateInvDetailsRequestDTO updateRequest) throws Exception{
			
		List<UpdateInvDetailsRequestDTO>  list=new ArrayList<>();
		list.add(updateRequest);
			
		try{
			
			invSearchService.updateInvDetailsByInvNo(updateRequest);
		} catch (Exception e) {
			return new ResponseEntity<ApiResponseMessage>(new ApiResponseMessage(HttpStatus.EXPECTATION_FAILED, ConstantUtils.ERR_IN_1004), HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<ApiResponseMessage>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_IN_1002), HttpStatus.OK);	
	}
	
}
