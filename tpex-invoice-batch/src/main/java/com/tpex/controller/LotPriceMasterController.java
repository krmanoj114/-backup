package com.tpex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.dto.UploadLotPartPriceMasterJobDto;
import com.tpex.service.LotPriceMasterService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class LotPriceMasterController {

	@Autowired
	LotPriceMasterService lotPriceMasterService;

	@PostMapping(path = "/uplaodLotPartPriceMaster") // Start batch process path
	public ResponseEntity<String> uplaodLotPartPriceMaster(@RequestBody UploadLotPartPriceMasterJobDto uploadLotPartPriceMasterJobDto) {
		
		lotPriceMasterService.lotPartPriceUploadBatchJob(uploadLotPartPriceMasterJobDto.getBatchName(), 
				uploadLotPartPriceMasterJobDto.getFileName(), uploadLotPartPriceMasterJobDto.getOemProcessCtrlEntity(), 
				uploadLotPartPriceMasterJobDto.getEffectiveDate(), uploadLotPartPriceMasterJobDto.getUserId());

		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}
	
}
