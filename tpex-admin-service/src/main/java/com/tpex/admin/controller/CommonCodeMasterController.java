package com.tpex.admin.controller;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.admin.commonfiles.ApiResponseMessage;
import com.tpex.admin.dto.*;
import com.tpex.admin.exception.InvalidInputParametersException;
import com.tpex.admin.exception.MyResourceNotFoundException;
import com.tpex.admin.repository.CountryMasterRepository;
import com.tpex.admin.service.CommonCodeMasterService;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.TpexConfigurationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class CommonCodeMasterController.
 */
@RestController
@CrossOrigin
@RequestMapping("/codeMaster")
public class CommonCodeMasterController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonCodeMasterController.class);

	@Autowired
	CommonCodeMasterService commonCodeMasterService;

	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;

	@Autowired
	CountryMasterRepository countryMasterRepository;

	/**
	 * Code master names.
	 *
	 * @param codeMasterRequestDTO the code master request DTO
	 * @return the response entity
	 * @throws IOException
	 * @throws DatabindException
	 * @throws StreamReadException
	 * @throws Exception           the exception
	 */
	@PostMapping(value = "/codeMasterNames", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CodeMasterDTO>> codeMasterNames(@RequestBody CodeMasterRequestDTO codeMasterRequestDTO)
			throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		String filePath = tpexConfigurationUtil.getFilePath("codeMasterNamesJsonPath");
		File file = ResourceUtils.getFile(filePath);
		if (!file.exists()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("filePath", filePath);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018, errorMessageParams);
		}
		List<CodeMasterDTO> listOfCodeMasterDTO = objectMapper.readValue(file,
				new TypeReference<List<CodeMasterDTO>>() {
				});
		return new ResponseEntity<>(listOfCodeMasterDTO, HttpStatus.OK);
	}

	/**
	 * Code master name.
	 *
	 * @param codeMasterRequestDTO the code master request DTO
	 * @return the response entity
	 * @throws IOException
	 * @throws Exception   the exception
	 */
	@PostMapping(value = "/codeMasterName", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonCodeMasterListDTO> codeMasterName(
			@RequestBody CodeMasterRequestDTO codeMasterRequestDTO) throws IOException {
		ColumnsDTO columnsModel = tpexConfigurationUtil.readDataFromJson(codeMasterRequestDTO.getJsonFileName());

		List<Map<String, String>> listofData;
		if (codeMasterRequestDTO.getCodeMasterName().equals("Final Destination Master")) {
			listofData = commonCodeMasterService.codeMasterDataFdm(columnsModel);
		} else {
			listofData = commonCodeMasterService.codeMasterData(columnsModel, codeMasterRequestDTO.getCmpCd());
		}
		columnsModel.setUserId(codeMasterRequestDTO.getUserId());
		columnsModel.setNoOfRecords(listofData.size());
		CommonCodeMasterListDTO model = new CommonCodeMasterListDTO();
		model.setColumns(columnsModel);
		model.setData(listofData);

		PaymentTerm paymentTerm = new PaymentTerm();
		List<Map<String, String>> list = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put(ConstantUtils.INDAYS, ConstantUtils.INDAY);
		map.put(ConstantUtils.INMONTH, ConstantUtils.INMONTHS);
		list.add(map);

		paymentTerm.setDayMonth(list);
		model.setPaymentTerm(paymentTerm);
		return new ResponseEntity<>(model, HttpStatus.OK);
	}

	/**
	 * Save code master.
	 *
	 * @param codeMasterRequestDTO the code master request DTO
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@PostMapping(value = "/saveCodeMaster", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> saveCodeMaster(@RequestBody CodeMasterRequestDTO codeMasterRequestDTO) {

		LOGGER.info("Calling SaveCodeMaster");
		if (codeMasterRequestDTO.getCodeMasterName() != null
				&& codeMasterRequestDTO.getCodeMasterName().equalsIgnoreCase(ConstantUtils.PAYMENT_TERM_CODE)) {
			Map<String, Object> errorMessageParams = commonCodeMasterService.savePaymentTermMaster(codeMasterRequestDTO,
					true);
			if (errorMessageParams.isEmpty()) {
				return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003),
						HttpStatus.OK);
			}
			return new ResponseEntity<>(
					new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3016, errorMessageParams),
					HttpStatus.OK);
		} else {
			commonCodeMasterService.saveCodeMaster(codeMasterRequestDTO);
		}
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
	}

	/**
	 * Delete code master.
	 *
	 * @param codeMasterRequestDTO the code master request DTO
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@PostMapping(value = "/deleteCodeMaster", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> deleteCodeMaster(@RequestBody CodeMasterRequestDTO codeMasterRequestDTO)
			throws InvalidInputParametersException {
		commonCodeMasterService.deleteCodeMaster(codeMasterRequestDTO);
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3006), HttpStatus.OK);
	}

	/**
	 * Update code master.
	 *
	 * @param codeMasterRequestDTO the code master request DTO
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@PostMapping(value = "/updateCodeMaster", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> updateCodeMaster(@RequestBody CodeMasterRequestDTO codeMasterRequestDTO) {
		if (codeMasterRequestDTO.getCodeMasterName() != null
				&& codeMasterRequestDTO.getCodeMasterName().equalsIgnoreCase(ConstantUtils.PAYMENT_TERM_CODE)) {
			Map<String, Object> errorMessageParams = commonCodeMasterService.savePaymentTermMaster(codeMasterRequestDTO,
					false);
			if (errorMessageParams.isEmpty()) {
				return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3007),
						HttpStatus.OK);
			}
			return new ResponseEntity<>(
					new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3016, errorMessageParams),
					HttpStatus.OK);
		} else {
			commonCodeMasterService.updateCodeMaster(codeMasterRequestDTO);
		}
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3007), HttpStatus.OK);
	}

	/**
	 * Update code master for Final Destionation, Buyer and CurrencyDetails.
	 *
	 * @param
	 * @return the final Destination response entity
	 * @throws Exception the exception
	 */
	@GetMapping(value = "/finalDestinationMaster", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FinalDestinationMasterResponseDto> finalDestination(String cmpCd) {

		return new ResponseEntity<>(commonCodeMasterService.finalDestinationDtls(cmpCd), HttpStatus.OK);
	}
}
