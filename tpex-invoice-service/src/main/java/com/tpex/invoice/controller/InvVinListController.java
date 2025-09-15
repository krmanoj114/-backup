package com.tpex.invoice.controller;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.invoice.service.InvVinListService;

//Need to remove this 

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class InvVinListController {
	@Autowired
	InvVinListService invVinListService;

	@GetMapping(value = "/vinList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> downloadInvoiceGenPlan(@RequestParam(defaultValue = "") String cmpCd,
			@RequestParam(defaultValue = "") String invNumber, @RequestParam(defaultValue = "") String userId,
			DownloadInvoiceReportsRequestDTO request) {
		HttpHeaders headers = new HttpHeaders();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(outputStream);
	}
}
