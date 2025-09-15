package com.tpex.invoice.controller;

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

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InvoiceSetupListResponseDto;
import com.tpex.invoice.dto.InvoiceSetupMasterSaveRequestDto;
import com.tpex.invoice.service.InvoiceSetupMasterService;
import com.tpex.util.ConstantUtils;
import com.tpex.util.Util;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class InvoiceSetupMasterController {
	
	@Autowired
	InvoiceSetupMasterService invoiceSetupMasterService;

	@GetMapping(value="/invoiceSetupMaster", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InvoiceSetupListResponseDto> getInvoiceSetupMaster(@RequestParam String setupType, @RequestParam(defaultValue = "") String importerCode){

		if(!Util.nullCheck(setupType) || importerCode == null || importerCode.isEmpty())
		{
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}
		InvoiceSetupListResponseDto response = invoiceSetupMasterService.getInvoiceSetupMstDetails(setupType, importerCode);
		if (response == null) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping(value= "/invoiceSetupMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object>  deleteInvoiceSetupMaster(@RequestBody List<Integer> ids) {

		Map<String,Object> errorMessageParams = new HashMap<>();
		
		try{
			invoiceSetupMasterService.deleteInvoiceSetupMaster(ids); 
		} 
		catch (InvalidInputParametersException e) {
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.BAD_REQUEST,e.getErrorMessageParamsArray(),e.getMessage()), HttpStatus.EXPECTATION_FAILED);
		}
		catch (Exception e) {
			errorMessageParams.put(ConstantUtils.ERROR_CODE,HttpStatus.EXPECTATION_FAILED.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.EXPECTATION_FAILED, ConstantUtils.ERR_CM_3005,errorMessageParams), HttpStatus.EXPECTATION_FAILED);
		}
		errorMessageParams.put(ConstantUtils.COUNT, ids.size());
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3002, errorMessageParams), HttpStatus.OK);

	}
	
	@PostMapping(value= "/invoiceSetupMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> saveInvoiceSetupMaster(@Valid @RequestBody InvoiceSetupMasterSaveRequestDto invoiceSetupMasterSaveRequestDto, @RequestParam String userId) throws InvalidInputParametersException {

		if(invoiceSetupMasterService.saveInvoiceSetupMaster(invoiceSetupMasterSaveRequestDto,userId)) 
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
		else {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, ConstantUtils.ERR_CM_3006, errorMessageParams), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
