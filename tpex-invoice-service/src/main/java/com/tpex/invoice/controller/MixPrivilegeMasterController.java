package com.tpex.invoice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.MixPrivilegeDetailsListResponseDto;
import com.tpex.invoice.dto.MixPrivilegeMasterSaveRequestDto;
import com.tpex.invoice.service.MixPrivilegeMasterService;
import com.tpex.repository.MixPrivilegeMasterRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.Util;


@RestController
@RequestMapping("/invoice")
@CrossOrigin
@Validated
public class MixPrivilegeMasterController {

	@Autowired
	MixPrivilegeMasterService mixPrivilegeMasterService;

	@Autowired 
	MixPrivilegeMasterRepository mixPrivilegeMasterRepository;

	@GetMapping(value="/fetchMixPrivilegeDetails",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MixPrivilegeDetailsListResponseDto> fetchMixPrivilegeDetails(@RequestParam String crFmlyCode,@RequestParam String destCode){

		if(!Util.nullCheck(crFmlyCode) || !Util.nullCheck(destCode))
		{
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}

		MixPrivilegeDetailsListResponseDto response = mixPrivilegeMasterService.fetchMixPrivilegeDetails(crFmlyCode, destCode);

		if (response == null) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@DeleteMapping(value= "/deleteMixPrivilegeMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object>  deleteMixPrivilegeMaster(@RequestBody List<Integer> ids) throws Exception{

		Map<String,Object> errorMessageParams = new HashMap<>();

		try{
			mixPrivilegeMasterService.deleteMixPrivilegeMaster(ids); 
		} 
		catch (InvalidInputParametersException e) {
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.BAD_REQUEST,e.getErrorMessageParamsArray(),e.getMessage()), HttpStatus.EXPECTATION_FAILED);
		}
		catch (Exception e) {
			errorMessageParams.put("errorCode",HttpStatus.EXPECTATION_FAILED.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.EXPECTATION_FAILED, ConstantUtils.ERR_CM_3005,errorMessageParams), HttpStatus.EXPECTATION_FAILED);
		}
		errorMessageParams.put("count", ids.size());
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3002, errorMessageParams), HttpStatus.OK);

	}

	@PostMapping(value= "/saveMixPrivilegeMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> saveMixPrivilegeMaster(@RequestBody List<MixPrivilegeMasterSaveRequestDto> mixPrivilegeMstSaveRequestDtoList,@RequestParam String userId) throws InvalidInputParametersException {

		if(mixPrivilegeMasterService.saveMixPrivilegeMaster(mixPrivilegeMstSaveRequestDtoList,userId)) 
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
		else {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, ConstantUtils.ERR_CM_3006, errorMessageParams), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}