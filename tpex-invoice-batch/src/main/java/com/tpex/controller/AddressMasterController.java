package com.tpex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.dto.UploadAddressMasterJobDto;
import com.tpex.service.AddressMasterService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class AddressMasterController {

	@Autowired
	private AddressMasterService addressMasterService;
	
	@PostMapping(path = "/uplaodAddressMaster")
	public ResponseEntity<String> uplaodAddressMaster(@RequestBody UploadAddressMasterJobDto uploadLotPartPriceMasterJobDto) {
		
		addressMasterService.addressMasterUploadBatchJob(uploadLotPartPriceMasterJobDto.getBatchName(), 
				uploadLotPartPriceMasterJobDto.getFileName(), uploadLotPartPriceMasterJobDto.getOemProcessCtrlEntity(), 
				uploadLotPartPriceMasterJobDto.getCompanyCode(), uploadLotPartPriceMasterJobDto.getUserId());

		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}

}
