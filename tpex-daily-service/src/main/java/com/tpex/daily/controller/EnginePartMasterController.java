package com.tpex.daily.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.daily.commonfiles.ApiResponseMessage;
import com.tpex.daily.service.EnginePartMasterService;
import com.tpex.daily.dto.DestinationCodeFinalResponseDTO;
import com.tpex.daily.dto.EnginePartMasterResponseDTO;
import com.tpex.daily.dto.EnginePartMasterSaveRequestDto;
import com.tpex.daily.dto.EnginePartMstRequestDTO;
import com.tpex.daily.exception.InvalidInputParametersException;
import com.tpex.daily.util.ConstantUtils;

@RestController
@CrossOrigin
@RequestMapping("/enginePartMaster")
public class EnginePartMasterController {
	
	private static final String ERROR_CODE = "errorCode";

	@Autowired
	EnginePartMasterService enginePartMasterService;
	
	@GetMapping(value = "/destinationCode", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DestinationCodeFinalResponseDTO> destinationCodeandImporter() {

		return new ResponseEntity<>(enginePartMasterService.destinationCodeandImporterDtls(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/enginePartMasterSearch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EnginePartMasterResponseDTO> enginePartMasterSearch(@RequestBody EnginePartMstRequestDTO enginePartMstRequestDTO) {

		return new ResponseEntity<>(enginePartMasterService.enginePartMasterSearchDtls(enginePartMstRequestDTO), HttpStatus.OK);
	}
	
	@DeleteMapping(value= "/deleteEnginePartMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> deleteEnginePartMaster(@RequestBody List<EnginePartMasterSaveRequestDto> deleteRequest) {

		Map<String, Object> errorMessageParams = new HashMap<>();

		try {
			enginePartMasterService.deleteEnginePartMaster(deleteRequest);

		} catch (Exception e) {
			errorMessageParams.put(ERROR_CODE,HttpStatus.EXPECTATION_FAILED.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.EXPECTATION_FAILED, ConstantUtils.ERR_CM_3005,errorMessageParams), HttpStatus.EXPECTATION_FAILED);
		}

		errorMessageParams.put("count", deleteRequest.size());
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3002, errorMessageParams), HttpStatus.OK);

	}
	
	@PostMapping(value= "/saveEnginePartMaster")
	public ResponseEntity<ApiResponseMessage> saveEnginePartMaster(@Valid @RequestBody List<EnginePartMasterSaveRequestDto> saveRequestList,@RequestParam String userId) throws InvalidInputParametersException {

		if(enginePartMasterService.saveEnginePartMaster(saveRequestList,userId)) 
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
		else {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, ConstantUtils.ERR_CM_3006, errorMessageParams), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
