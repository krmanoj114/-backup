package com.tpex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.dto.UploadPartMasterJobDto;
import com.tpex.service.PartMasterService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class PartMasterController {
	
	@Autowired
	private PartMasterService partMasterService;
	
	@PostMapping(path = "/uploadPartMaster")
	public ResponseEntity<String> uploadPartMaster(@RequestBody UploadPartMasterJobDto uploadPartMasterJobDto) {
		
		partMasterService.partMasterUploadJob(uploadPartMasterJobDto.getBatchName(), 
				uploadPartMasterJobDto.getFileName(), uploadPartMasterJobDto.getOemProcessCtrlEntity(), 
				uploadPartMasterJobDto.getCompanyCode(), uploadPartMasterJobDto.getUserId());

		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}
}
