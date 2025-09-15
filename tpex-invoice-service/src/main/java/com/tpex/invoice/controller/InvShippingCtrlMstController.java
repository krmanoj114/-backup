package com.tpex.invoice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tpex.dto.ConsigneeAndNotifyPartyDto;
import com.tpex.dto.OemShippingCtrlMstDeleteRequestDto;
import com.tpex.dto.OemShippingCtrlMstSaveRequestDto;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.OemShippingCtrlMstDto;
import com.tpex.invoice.service.InvShippingCtrlMstService;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class InvShippingCtrlMstController {
	
	@Autowired
	private InvShippingCtrlMstService invShippingCtrlMstService;
	
	/**
	 * @return
	 * @throws Exception 
	 */
	@GetMapping(value= "/shippingControlMasterList", produces=MediaType.APPLICATION_JSON_VALUE)
	//Fetch Renban codes based on container destination code
	public ResponseEntity<OemShippingCtrlMstDto> fetchInvShippingCtrlMst() throws Exception{
		return new ResponseEntity<>(invShippingCtrlMstService.fetchInvShippingCtrlMst(), HttpStatus.OK);
	}
	
	/**
	 * @param buyer
	 * @return
	 */
	@GetMapping(value= "/consigneeAndNotifyPartyByBuyer", produces=MediaType.APPLICATION_JSON_VALUE)
	//Fetch Renban codes based on container destination code
	public ResponseEntity<ConsigneeAndNotifyPartyDto> fetchConsigneeAndNotifyPartyByBuyer(@RequestParam(required = true) String buyer){
		return new ResponseEntity<>(invShippingCtrlMstService.fetchConsigneeAndNotifyPartyByBuyer(buyer), HttpStatus.OK);
	}
	
	/**
	 * @param oemShippingCtrlMstSaveRequestDtoList
	 * @return
	 */
	@PostMapping(value= "/saveShippingControlMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> saveShippingControlMaster(@RequestBody List<OemShippingCtrlMstSaveRequestDto> oemShippingCtrlMstSaveRequestDtoList) {
		if(invShippingCtrlMstService.saveShippingControlMaster(oemShippingCtrlMstSaveRequestDtoList)) 
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
		else {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
		}
	}
	
	/**
	 * @param oemShippingCtrlMstDeleteRequestDtoList
	 * @return
	 */
	@DeleteMapping(value= "/deleteShippingControlMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> deleteShippingControlMaster(@RequestBody List<OemShippingCtrlMstDeleteRequestDto> oemShippingCtrlMstDeleteRequestDtoList) {
		invShippingCtrlMstService.deleteShippingControlMaster(oemShippingCtrlMstDeleteRequestDtoList); 
		Map<String, Object> errorMessageParams = new HashMap<>();
		errorMessageParams.put("count", oemShippingCtrlMstDeleteRequestDtoList.size());
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3002, errorMessageParams), HttpStatus.OK);
	}

}
