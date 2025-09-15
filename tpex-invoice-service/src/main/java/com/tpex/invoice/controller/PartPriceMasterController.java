package com.tpex.invoice.controller;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.FinalDestinationAndCarFamilyCodesDTO;
import com.tpex.dto.PartPriceMasterDeleteRequestDto;
import com.tpex.dto.PartPriceMasterDto;
import com.tpex.dto.PartPriceMasterRequestDto;
import com.tpex.dto.PartPriceMasterResponseDto;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.service.PartPriceMasterService;
import com.tpex.util.ConstantUtils;

import net.sf.jasperreports.engine.JRException;

/**
 * The Class PartPriceMasterController.
 */
@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class PartPriceMasterController {

	private static final String ERROR_CODE = "errorCode";
	
	/** The part price master service. */
	@Autowired
	PartPriceMasterService partPriceMasterService;
	
	/**
	 * Search lot price details.
	 *
	 * @param partPriceMasterRequestDto the part price master request dto
	 * @return the response entity
	 * @throws ParseException 
	 */
	@PostMapping(value = "/partPriceMasterList")
	public ResponseEntity<PartPriceMasterResponseDto> searchPartPriceDetails (
			@Valid @RequestBody PartPriceMasterRequestDto partPriceMasterRequestDto) throws ParseException {

		return new ResponseEntity<>(partPriceMasterService.partPriceMasterList(partPriceMasterRequestDto), HttpStatus.OK);
	}
	
	
	@PostMapping(value= "/deletePxpPartPriceMaster") 
	public ResponseEntity<ApiResponseMessage> deletePartPriceMaster(@RequestBody PartPriceMasterDeleteRequestDto deleteRequest) {
		
		List<PartPriceMasterDto> partPriceMasterDtoList = null;
		try {
			partPriceMasterDtoList = partPriceMasterService.deletePartPriceMasterDetails(deleteRequest.getData());
		} catch (Exception e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3005, errorMessageParams);
		}
		if (partPriceMasterDtoList.isEmpty()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("count", deleteRequest.getData().size());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3002, errorMessageParams), HttpStatus.OK);
		} else {
			
			Integer deletedNumbers = deleteRequest.getData().size() - partPriceMasterDtoList.size();
			
			if(deletedNumbers > 0) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("deletedRecords", deleteRequest.getData().size() - partPriceMasterDtoList.size());
			errorMessageParams.put("notDeletedRecord", partPriceMasterDtoList.size());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3021, errorMessageParams);
			}
			else
			{
				Map<String, Object> errorMessageParams = new HashMap<>();
				List<String> partNumbers = partPriceMasterDtoList.stream().map(PartPriceMasterDto:: getPartNo).collect(Collectors.toList());
				errorMessageParams.put("Part No.", partNumbers);
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_3023, errorMessageParams );
			}


		}
	}
	
	/**
	 * Destination and carfamily codes.
	 *
	 * @param userId the user id
	 * @return the response entity
	 */
	@GetMapping(value = "/destinationAndCarfamilyCodes")
	public ResponseEntity<FinalDestinationAndCarFamilyCodesDTO> destinationAndCarfamilyCodes(@RequestParam String userId) {
		return new ResponseEntity<>(partPriceMasterService.destinationAndCarfamilyCodes(userId), HttpStatus.OK);
	}
	
	/**
	 * Download part price master details.
	 *
	 * @param carFamilyCode the car family code
	 * @param importerCode the importer code
	 * @param partNo the part no
	 * @param effectiveMonth the effective month
	 * @param userId the user id
	 * @return the response entity
	 */
	@GetMapping(value="/downloadPartPriceMasterDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	//Exposed header param to be accessible at UI
	@CrossOrigin(exposedHeaders = { "filename" })
	public ResponseEntity<Object> downloadPartPriceMasterDetails(@RequestParam(defaultValue = "") String carFamilyCode,
			@RequestParam(defaultValue = "") String importerCode, @RequestParam(defaultValue = "") String partNo, 
			@RequestParam(defaultValue = "") String effectiveMonth, @RequestParam(defaultValue = "") String userId) {
		Map<String, Object> resultMap = null;
		try {
			resultMap = partPriceMasterService.downloadPartPriceMasterDetails(carFamilyCode, importerCode, partNo, effectiveMonth, userId);
		} catch (FileNotFoundException | ParseException | JRException e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3020, errorMessageParams);
		}
		
		if (resultMap.isEmpty()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3020, errorMessageParams);
		}
		
		if ("online".equals(resultMap.get("status"))) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("filename", (String) resultMap.get("fileName"));
			headers.add("Content-Disposition", "attachment; filename="+resultMap.get("fileName"));
			return ResponseEntity.ok()
					.headers(headers)
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(resultMap.get("outStream"));
		} else {
			return new ResponseEntity<>(resultMap, HttpStatus.OK);
		}
	}
	
	@PostMapping(value= "/savePxpPartPriceMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> savePxpPartPriceMaster(@RequestBody PartPriceMasterDeleteRequestDto requestDto) {
		try {
			if(partPriceMasterService.savePxpPartPriceMaster(requestDto)) {
				return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
			} else {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
			}
		}catch (InvalidInputParametersException e){
				
				throw new InvalidInputParametersException(e.getMessage(), e.getErrorMessageParams());

		} catch (Exception e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
		}
	}
	
	@PostMapping(value= "/updatePxpPartPriceMaster", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> updatePxpPartPriceMaster(@RequestBody PartPriceMasterDeleteRequestDto requestDto) {
		try {
			if(partPriceMasterService.updatePxpPartPriceMaster(requestDto)) {
				return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
			} else {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
			}
		} catch (Exception e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
		}
	}
	
	@GetMapping(value= "/partName/{partNo}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> partNameByPartNo(@PathVariable String partNo) {
		return new ResponseEntity<>(partPriceMasterService.partNameByPartNo(partNo), HttpStatus.OK);
	}
	
}
