package com.tpex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.dto.UploadCountryCodeOriginDto;
import com.tpex.service.CountryCodeOriginService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class CountryCodeOriginController {
	
	@Autowired
	CountryCodeOriginService countryCodeOriginService;

	@PostMapping(path = "/countryCodeOriginUpload") // Start batch process path
	public ResponseEntity<String> uplaodCountryCodeOriginMaster(@RequestBody UploadCountryCodeOriginDto countryCodeOriginDto) {
		
		countryCodeOriginService.countryCodeOriginBatchJob(countryCodeOriginDto.getBatchName(), 
				countryCodeOriginDto.getFileName(), countryCodeOriginDto.getOemProcessCtrlEntity()
				, countryCodeOriginDto.getUserId());

		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}

}
