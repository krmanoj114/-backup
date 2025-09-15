package com.tpex.admin.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.admin.dto.CompanyDropdownResponse;
import com.tpex.admin.dto.ContainerDestinationPlantCodeDTO;
import com.tpex.admin.service.DropdownService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/dropdown")
public class DropdownController {

	private static final Logger log = LoggerFactory.getLogger(DropdownController.class);

	private final DropdownService dropdownService;

	@GetMapping("/companyAndPlant")
	public ResponseEntity<CompanyDropdownResponse> getCompanyAndPlantDropdownByUserId(
			@RequestParam(required = true, value = "userId") String userId) {
		return new ResponseEntity<>(dropdownService.getCompanyAndPlantDropdownByUserId(userId), HttpStatus.OK);
	}
	
	@GetMapping(value = "/plantAndDestinationCode", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContainerDestinationPlantCodeDTO> plantAndDestinationCodeDetails(@RequestParam(value = "companyCode",required = true) String companyCode) {
		log.info("DropdownController::plantAndDestinationCodeDetails: Started ");

		ContainerDestinationPlantCodeDTO vanningPlantAndContainerDtls = dropdownService.vanningPlantAndContainerDtls(companyCode);
		log.info("DropdownController::plantAndDestinationCodeDetails: Ended ");
		return new ResponseEntity<>(vanningPlantAndContainerDtls, HttpStatus.OK);
	}
}
