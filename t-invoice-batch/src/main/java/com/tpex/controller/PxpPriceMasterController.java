package com.tpex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.dto.UploadPxpPriceMasterJobDto;
import com.tpex.service.PxpPriceMasterService;
import com.tpex.util.ConstantUtils;

/**
 * The Class PxpPriceMasterController.
 */
@RestController
@RequestMapping("/invoice")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class PxpPriceMasterController {

	/** The pxp price master service. */
	@Autowired
	private PxpPriceMasterService pxpPriceMasterService;
	
	/**
	 * Upload pxp price master.
	 *
	 * @param uploadPxpPriceMasterJobDto the upload pxp price master job dto
	 * @return the response entity
	 */
	@PostMapping(path = "/uploadPxpPriceMaster")
	public ResponseEntity<String> uploadPxpPriceMaster(@RequestBody UploadPxpPriceMasterJobDto uploadPxpPriceMasterJobDto) {
		pxpPriceMasterService.pxpPriceMasterUploadBatchJob(uploadPxpPriceMasterJobDto);
		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}

}
