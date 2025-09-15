package com.tpex.invoice.controller;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

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

	@GetMapping(value="/vinList", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> downloadInvoiceGenPlan(@RequestParam(defaultValue = "") String cmpCd,@RequestParam(defaultValue = "") String invNumber, @RequestParam(defaultValue = "") String userId,DownloadInvoiceReportsRequestDTO request) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//Map<String, Object> response = new HashMap<>();
	/*	HttpHeaders headers = new HttpHeaders();
		ByteArrayOutputStream outputStream = null;
		Object response = null;// invVinListService.getInvVinListReportDownload(cmpCd, invNumber, userId, "InvoiceVinList", request);
		if(response !=null) {
		Map<String, Object> map = (HashMap<String, Object>) response;
		String fileName = map != null ? (String) map.get("fileName") : "";
		if(map !=null)
		outputStream = (ByteArrayOutputStream) map.get("outStream");
		headers.add("filename", fileName);
		headers.add("Content-Disposition", "attachment; filename= "+fileName);
		}*/
		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(outputStream);
	}
}
