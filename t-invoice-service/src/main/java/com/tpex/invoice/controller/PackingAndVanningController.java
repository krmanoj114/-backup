package com.tpex.invoice.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.invoice.dto.CustomLabelResponseDTO;
import com.tpex.invoice.dto.PackingAndVanningResponseDTO;
import com.tpex.invoice.dto.PackingVanningReportResponseDTO;
import com.tpex.invoice.dto.SsLineGroupBoxResponseDTO;
import com.tpex.invoice.dto.VanningPlantResponseDTO;
import com.tpex.invoice.service.PackingAndVanningService;

@RestController
@RequestMapping("/invoice")
@CrossOrigin

public class PackingAndVanningController {

	@Autowired
	PackingAndVanningService packingAndVanningService;

	@GetMapping("/destination")
	public ResponseEntity<PackingAndVanningResponseDTO> getPackingAndVanningResponseData(
			@RequestParam(required = true, value = "companyCode") String companyCode) {
		return new ResponseEntity<>(packingAndVanningService.getPackingAndVanningResponseData(companyCode),
				HttpStatus.OK);

	}

	@GetMapping("/vanningPlant")
	public ResponseEntity<VanningPlantResponseDTO> getVanningPlantResponseData(
			@RequestParam(required = true, value = "companyCode") String companyCode,
			@RequestParam(required = true, value = "planningFlag") String planningFlag) {
		return new ResponseEntity<>(packingAndVanningService.getVanningPlantResponseData(companyCode, planningFlag),
				HttpStatus.OK);
	}

	@GetMapping("/customLabel")
	public ResponseEntity<CustomLabelResponseDTO> getCustomLabelResponseData(
			@RequestParam(required = true, value = "companyCode") String companyCode) {
		return new ResponseEntity<>(packingAndVanningService.getCustomLabelResponseData(companyCode), HttpStatus.OK);
	}

	@GetMapping("/ssLineGroupBox")
	public ResponseEntity<SsLineGroupBoxResponseDTO> getSsLineGroupBoxResponseData() {
		return new ResponseEntity<>(packingAndVanningService.getSsLineGroupBoxResponseData(), HttpStatus.OK);
	}

	@GetMapping("/destinationAndShippingReports")
	public ResponseEntity<PackingVanningReportResponseDTO> getPackingVanningReport() throws IOException {
		return new ResponseEntity<>(packingAndVanningService.getPackingVanningReport(), HttpStatus.OK);
	}

}
