package com.tpex.barcode.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.barcode.entity.TpexBarcodeEntity;
import com.tpex.barcode.service.BarcodeService;

@RestController
@RequestMapping("/barcodeData")
public class TpexBarcodeController {
	final Logger log = LoggerFactory.getLogger(TpexBarcodeController.class);

	@Autowired
	BarcodeService barcodeService;

	@PostMapping("/publishMessage")
	public ResponseEntity<TpexBarcodeEntity> saveBarcodeData(@RequestBody TpexBarcodeEntity barcodeData) {
		log.info("TpexBarcodeController::publishMessage: - Publish Message started");
		TpexBarcodeEntity entity = barcodeService.saveBarcodeData(barcodeData);
		log.info("TpexBarcodeController::publishMessage: - Publish message saved successfully");
		entity.setRespobseBody("OK");
		return ResponseEntity.ok(entity);

	}

}
