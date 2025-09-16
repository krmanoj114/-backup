package com.tpex.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.UploadWrkPlanMasterFromVesselBookingRequest;
import com.tpex.dto.UploadwrkPlanMasterJobDto;
import com.tpex.service.InvGenWorkPlanMstService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class InvGenWorkPlanMstController {
	
	@Autowired
	InvGenWorkPlanMstService invGenWorkPlanMstService;

	@PostMapping(path = "/uplaodWrkPlanMaster") // Start batch process path
	public ResponseEntity<String> uplaodWrkPlanMaster(@RequestBody UploadwrkPlanMasterJobDto uploadwrkPlanMasterJobDto) {
		
		invGenWorkPlanMstService.wrkPlanMasterUploadBatchJob(uploadwrkPlanMasterJobDto.getBatchName(), 
				uploadwrkPlanMasterJobDto.getFileName(), uploadwrkPlanMasterJobDto.getOemProcessCtrlEntity(), 
				uploadwrkPlanMasterJobDto.getInvoiceDateFrom(), uploadwrkPlanMasterJobDto.getUserId());

		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}
	
	@PostMapping(path = "/uplaodWrkPlanMasterFromVesselBooking")
	public ResponseEntity<ApiResponseMessage> uplaodWrkPlanMasterFromVesselBooking(@Valid @RequestBody UploadWrkPlanMasterFromVesselBookingRequest request) {
		
		invGenWorkPlanMstService.wrkPlanMasterUploadFromVesselBookingBatchJob(request);
		
		ApiResponseMessage res = new ApiResponseMessage();
		res.setStatusCode(HttpStatus.OK.value());
		res.setStatusMessage("Work Plan Master update request submitted.");

		return new ResponseEntity<>(res, HttpStatus.OK);
	}

}
