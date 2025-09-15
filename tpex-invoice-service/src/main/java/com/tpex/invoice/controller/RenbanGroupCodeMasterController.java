package com.tpex.invoice.controller;


import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.RenbanGroupCodeMasterRequestDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterResponseFinalDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterSaveRequestDto;
import com.tpex.invoice.dto.RenbancodeMasterDeleteDto;
import com.tpex.invoice.service.RenbanGroupCodeMasterService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class RenbanGroupCodeMasterController {
	
	
	@Autowired
	private RenbanGroupCodeMasterService renbanGroupCodeMasterService;
	
	@PostMapping(value= "/renbanGroupCodeByDestination")
	public ResponseEntity<RenbanGroupCodeMasterResponseFinalDto> searchRenbanGroupCodeMasterDetails(@Valid @RequestBody RenbanGroupCodeMasterRequestDto request) throws ParseException{
		
		 RenbanGroupCodeMasterResponseFinalDto data=renbanGroupCodeMasterService.getRenbanGroupCodeMasterDetails(request);
		
		 if(data.getRenbanData().isEmpty() && data.getRenbanGroupCodeList().isEmpty()) {
			 
			 throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		 }
		 
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
	@DeleteMapping(value= "/renbanGroupCode")
	public ResponseEntity<ApiResponseMessage> renbanGroupCodeMasterDetails(@Valid @RequestBody List<RenbancodeMasterDeleteDto> deleteRequest ) throws ParseException{

		try {
			renbanGroupCodeMasterService.deleteRenbanGroupCodeMasterDetails(deleteRequest);
			
		}catch (InvalidInputParametersException | MyResourceNotFoundException e) {

			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_IN_1005), HttpStatus.OK);
	}
	
	@PostMapping(value = "/saveRenbanCodeMaster")
	public ResponseEntity<ApiResponseMessage> saveRenbanCodeMaster(
			@RequestBody List<RenbanGroupCodeMasterSaveRequestDto> renbanGroupCodeMasterSaveRequestDtoList)
			throws ParseException {

		if (renbanGroupCodeMasterService.saveRenbanCodeMaster(renbanGroupCodeMasterSaveRequestDtoList))
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003),
					HttpStatus.OK);
		else {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
		}

	}

	@PutMapping(value = "/updateRenbanCodeMaster")
	public ResponseEntity<ApiResponseMessage> updateRenbanCodeMaster(
			@RequestBody List<RenbanGroupCodeMasterSaveRequestDto> renbanGroupCodeMasterSaveRequestDtoList)
			throws Exception {

		renbanGroupCodeMasterService.updateRenbanCodeMaster(renbanGroupCodeMasterSaveRequestDtoList);
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
	}


}
