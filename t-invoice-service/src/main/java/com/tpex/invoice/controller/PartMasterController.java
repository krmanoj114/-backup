package com.tpex.invoice.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InhouseDropdownResponseDTO;
import com.tpex.invoice.dto.PartMasterModifyRequestDTO;
import com.tpex.invoice.dto.PartMasterRequestDTO;
import com.tpex.invoice.dto.PartMasterSearchRequestDto;
import com.tpex.invoice.dto.PartMasterSearchResponseDto;
import com.tpex.invoice.service.PartMasterService;
import com.tpex.util.ConstantUtils;
import com.tpex.validator.PartMasterRequestValidatior;

/**
 * The Class PartPriceMasterController.
 */
@RestController
@RequestMapping("/invoice/partmaster")
@CrossOrigin
public class PartMasterController {

	/** The part price master service. */
	@Autowired
	PartMasterService partMasterService;

	@PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PartMasterSearchResponseDto>> search(
			@Valid @RequestBody PartMasterSearchRequestDto partMasterSearchRequestDto)
			throws IllegalAccessException, InvocationTargetException {
		if (PartMasterRequestValidatior.validatePartMasterSearchRequest(partMasterSearchRequestDto)) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1056);
		}

		return new ResponseEntity<>(partMasterService.search(partMasterSearchRequestDto), HttpStatus.OK);
	}

	@PostMapping("/download")
	@CrossOrigin(exposedHeaders = { "filename" })
	public ResponseEntity<Object> download(HttpServletResponse reponse,
			@RequestBody PartMasterSearchRequestDto partMasterSearchRequestDto)
			throws IOException, DecoderException, ParseException, NullPointerException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtils.CARFMLYDESTMST_DOWNLOAD_DATE_FORMAT);
		String currentDateTime = LocalDateTime.now().format(formatter);

		String fileName = ConstantUtils.PART_MASTER + "_" + currentDateTime + ".xlsx";

		byte[] byteResponse = partMasterService.download(reponse, partMasterSearchRequestDto);

		if (byteResponse != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(ConstantUtils.CONTENTDISPOSITION, ConstantUtils.ATTACHMENT + fileName);
			headers.add(ConstantUtils.FILENAME, fileName);
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(byteResponse);
		} else {
			return ResponseEntity.internalServerError().build();

		}
	}

	@DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> deletePartMaster(@RequestBody PartMasterRequestDTO partMasterRequestDTO)
			throws InvalidInputParametersException {
		try {
			int numberOfRowsDeleted = partMasterService.deletePartMaster(partMasterRequestDTO);
			if (numberOfRowsDeleted == 0) {
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3005);
			}
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.COUNT, numberOfRowsDeleted);
			return new ResponseEntity<>(
					new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3006, errorMessageParams),
					HttpStatus.OK);
		} catch (Exception e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3005, errorMessageParams);
		}
	}

	@PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> saveCodeMaster(
			@RequestBody PartMasterModifyRequestDTO partMasterSaveRequestDTO) {
		partMasterService.savePartMaster(partMasterSaveRequestDTO);
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
	}

	@PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> updateCodeMaster(
			@RequestBody PartMasterModifyRequestDTO partMasterModifyRequestDTO) {
		partMasterService.updatePartMaster(partMasterModifyRequestDTO);
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
	}

	@GetMapping("/inhouseshop")
	public ResponseEntity<InhouseDropdownResponseDTO> getInhouseDropdownData(
			@RequestParam(required = true, value = "companyCode") String companyCode) {
		return new ResponseEntity<>(partMasterService.getInhouseDropdownData(companyCode), HttpStatus.OK);
	}

}
