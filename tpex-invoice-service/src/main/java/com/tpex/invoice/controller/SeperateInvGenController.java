package com.tpex.invoice.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.SeprateInvGenResponseWrapper;
import com.tpex.invoice.service.SeperateInvGenService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice-generation")
@CrossOrigin
public class SeperateInvGenController {

	@Autowired
	SeperateInvGenService seprateInvGenService;

	@GetMapping(value = "/searchInvoice", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SeprateInvGenResponseWrapper> getByInvoiceNo(@RequestParam String invoiceNo,@RequestParam String carFamily,@RequestParam String partNo) {
		if (StringUtils.isEmpty(invoiceNo)) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}
		SeprateInvGenResponseWrapper response = seprateInvGenService.searchByInvNo(invoiceNo,carFamily,partNo);
		if (response.getSepInvDto().isEmpty() && response.getSepInvGenRespDto().isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
