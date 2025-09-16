package com.tpex.month.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.month.model.dto.DropDownVesselBookingMasterResponse;
import com.tpex.month.service.DropdownService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/month/dropdown")

public class DropdownController {

	private final DropdownService dropdownService;

	@GetMapping(path = "/finalDstAndShipComp")
	public ResponseEntity<DropDownVesselBookingMasterResponse> getDropDownForVesselBookingMaster() {
		return ResponseEntity.status(HttpStatus.OK).body(dropdownService.getDropDownForVesselBookingMaster());
	}
}
