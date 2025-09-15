package com.tpex.invoice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.invoice.dto.ManInvGenResponseDto;
import com.tpex.invoice.dto.ManualInvoiceGenResponseDto;
import com.tpex.invoice.service.ManualInvGenService;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class ManualInvGenController {

	@Autowired
	ManualInvGenService manualInvGenService;

	@GetMapping("/searchManualInvoice")
	public ResponseEntity<ManInvGenResponseDto> searchManualGenInvoiceDetails(@RequestParam Map<String, String> queryParam) {
		
		ManInvGenResponseDto manInvGenResponseDto = new ManInvGenResponseDto();
		List<ManualInvoiceGenResponseDto> response = manualInvGenService.searchManualInvDetails(queryParam);
		manInvGenResponseDto.setListInvoiceResponse(response);
		return new ResponseEntity<>(manInvGenResponseDto, HttpStatus.OK);
	}

}
