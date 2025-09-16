package com.tpex.invoice.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InvoiceNumberResponseDTO;
import com.tpex.invoice.dto.InvoiceReCalculateRequestDto;
import com.tpex.invoice.dto.InvoiceReCalculationRequestDTO;
import com.tpex.invoice.dto.InvoiceReCalculationResponseDTO;
import com.tpex.invoice.service.InvoiceRecalculateService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class InvoiceRecalculateController {
	
	@Autowired
	InvoiceRecalculateService invoiceRecalculateService;
	
	@GetMapping(value ="/invoiceDetailsByInv")
	public ResponseEntity<InvoiceNumberResponseDTO> getInvDetailsByInvNumber(@RequestParam(required = true) String invoiceNumber, @RequestParam(required = true) String companyCode) throws ParseException {
		
		InvoiceNumberResponseDTO response = invoiceRecalculateService.getDetailsByInvNo(invoiceNumber, companyCode);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/invrecalculatedetails")
	public ResponseEntity<List<InvoiceReCalculationResponseDTO>> searchInvRecalulateDetails(@RequestBody InvoiceReCalculationRequestDTO request) throws ParseException{
		
		List<InvoiceReCalculationResponseDTO> response = invoiceRecalculateService.getInvRecalculateDetails(request.getInvoiceNumber(), request.getPartNumber(), request.getPrivilege(), request.getCompanyCode());
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping(value = "/recalculateInvoice")
	public ResponseEntity<ApiResponseMessage> recalculateInvoice(@RequestBody InvoiceReCalculateRequestDto request) {
		try {
			invoiceRecalculateService.recalculateInvoice(request);
			if ("PR".equals(request.getPrivilege())) {
				return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_IN_1007), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_IN_1006), HttpStatus.OK);
			}
		} catch (MyResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
		}
	}
}
