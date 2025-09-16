package com.tpex.admin.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.admin.commonfiles.ApiResponseMessage;
import com.tpex.admin.dto.*;
import com.tpex.admin.entity.HaisenNoEntity;
import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.exception.InvalidInputParametersException;
import com.tpex.admin.exception.MyResourceNotFoundException;
import com.tpex.admin.repository.OemProcessCtrlRepository;
import com.tpex.admin.service.CommonProcessControlServiceImpl;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.TpexConfigurationUtil;
import com.tpex.admin.validator.CPCSubmitProcessRequestValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping(path = "/commonProcessControl")
public class CommonProcessControlController {

	private static final String MONTH_YEAR = "haisenYearMonth";

	private static final String INVOICE_NUMBER = "invoiceNo";

	private static final String HAISEN_NUMBER = "haisenNo";

	private List<ProcessBatchDTO> processBatch;

	private final CommonProcessControlServiceImpl commonProcessControlService;
	private final CPCSubmitProcessRequestValidator cpcSubmitProcessRequestValidator;
	private final TpexConfigurationUtil tpexConfigurationUtil;

	@Autowired
	private OemProcessCtrlRepository oemProcessCtrlRepository;

	@PostMapping(path = "/processNames")
	public ResponseEntity<List<CPCProcessNamesResponseDTO>> processNames(
			@Valid @RequestBody CPCProcessNamesRequestDTO request) throws IOException {
		initProcessBatch();
		List<CPCProcessNamesResponseDTO> processNames = processBatch.stream()
				.filter(p -> p.getUserId().equals(request.getUserId()))
				.map(f -> new CPCProcessNamesResponseDTO(f.getProcessId(), f.getProcessName()))
				.collect(Collectors.toList());
		return new ResponseEntity<>(processNames, HttpStatus.OK);
	}

	@PostMapping(path = "/processName")
	public ResponseEntity<CPCProcessNameResponseDTO> processName(@Valid @RequestBody CPCProcessNameRequestDTO request)
			throws IOException {
		initProcessBatch();
		CPCProcessNameResponseDTO processDetailResponseDTO = new CPCProcessNameResponseDTO();
		for (ProcessBatchDTO processBatchDTO : processBatch) {
			if (processBatchDTO.getProcessId().equals(request.getProcessId())
					&& processBatchDTO.getUserId().equals(request.getUserId())) {
				processDetailResponseDTO.setProcessId(processBatchDTO.getProcessId());
				processDetailResponseDTO.setProcessName(processBatchDTO.getProcessName());
				List<ProcessControlParameter> list = processBatchDTO.getParameters();
				processDetailResponseDTO.setParameters(list);

				if (processBatchDTO.getProcessId().equals(ConstantUtils.BINF024)) {
					List<Map<String, Object>> dropDownList = new ArrayList<>();
					Map<String, Object> destMap = new HashMap<>();
					List<CommonDropdownDto> destCodeAndName = commonProcessControlService.fetchDestCodeAndDestName();
					destMap.put(ConstantUtils.DESTINATION_1, destCodeAndName);
					dropDownList.add(destMap);
					processDetailResponseDTO.setDropDownItems(dropDownList);
				}
			}
		}
		return new ResponseEntity<>(processDetailResponseDTO, HttpStatus.OK);
	}

	@PostMapping(path = "/submitProcess")
	public ResponseEntity<ApiResponseMessage> submitProcess(@Valid @RequestBody CPCSubmitProcessRequestDTO request,
			BindingResult result) throws IOException, IllegalArgumentException {

		initProcessBatch();

		request.setHasParameter(processBatch.stream()
				.anyMatch(p -> p.getProcessId().equals(request.getProcessId()) && !p.getParameters().isEmpty()));
		cpcSubmitProcessRequestValidator.validate(request, result);

		if (null != request.getParameters() && null == request.getParameters().get("warnFlag") && !request.getProcessId().equals("BINS011") && !request.getProcessId().equals("BINF023") && !request.getProcessId().equals("BINF016") && !request.getProcessId().equals("PSINS036")) {		
			boolean isInvoiceNumberExist = false;
			boolean isHaisenNumberExist = false;
			String invoiceNumber = null;
			String haisenNo = null;
			if (null != request.getParameters() && null != request.getParameters().get(INVOICE_NUMBER)) {
				invoiceNumber = request.getParameters().get(INVOICE_NUMBER);
				isInvoiceNumberExist = commonProcessControlService.isInvoiceExists(invoiceNumber);
				Map<String, Object> errorMessageParams = new HashMap<>();
				checkValidInvoiceNumber(isInvoiceNumberExist, invoiceNumber, errorMessageParams);
			}
			if (null != request.getParameters() && null != request.getParameters().get(HAISEN_NUMBER)) {
				haisenNo = request.getParameters().get(HAISEN_NUMBER);
				List<HaisenNoEntity> listHaisenNumbers = commonProcessControlService.isHaisenExists(haisenNo);
				isHaisenNumberExist = !listHaisenNumbers.isEmpty();
				Map<String, Object> errorMessageParams = new HashMap<>();
				if (listHaisenNumbers.isEmpty()) {
					errorMessageParams.put(HAISEN_NUMBER, "Haisen No. " + haisenNo + " is Invalid");
					throw new InvalidInputParametersException(ConstantUtils.ERR_AD_4005, errorMessageParams);

				}
			}
			setIxosNumberForInvoiceNo(isInvoiceNumberExist, invoiceNumber);
			setIxosNumberForHaisenNo(isHaisenNumberExist, haisenNo);
		}
		checkError(result);

		if (null != request.getParameters().get("warnFlag") || request.getProcessId().equals("BINS011") || request.getProcessId().equals("BINF023") || request.getProcessId().equals("BINF016") || request.getProcessId().equals("PSINS036")) {
		
			commonProcessControlService.submitProcess(request);
		}
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3011), HttpStatus.OK);

	}

	private void checkValidInvoiceNumber(boolean isInvoiceNumberExist, String invoiceNumber,
			Map<String, Object> errorMessageParams) {
		if (!isInvoiceNumberExist) {
			errorMessageParams.put(INVOICE_NUMBER, "Invoice No. " + invoiceNumber + " is Invalid");
			throw new InvalidInputParametersException(ConstantUtils.ERR_AD_4004, errorMessageParams);
		}
	}

	private void checkError(BindingResult result) {
		if (result.hasErrors()) {
			throw new IllegalArgumentException("Result has errror in submit process : " + result);
		}
	}

	private void setIxosNumberForHaisenNo(boolean isHaisenNumberExist, String haisenNo) {
		if (isHaisenNumberExist) {
			String ixosFlag = commonProcessControlService.findIxosFlagForHaisenNo(haisenNo);
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("ixosFlag", ixosFlag);
			throw new InvalidInputParametersException(ConstantUtils.WARN_AD_3004, errorMessageParams);
		}
	}

	private void setIxosNumberForInvoiceNo(boolean isInvoiceNumberExist, String invoiceNumber) {
		if (isInvoiceNumberExist) {
			String ixosFlag = commonProcessControlService.findIxosFlag(invoiceNumber);
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("ixosFlag", ixosFlag);
			throw new InvalidInputParametersException(ConstantUtils.WARN_AD_3003, errorMessageParams);
		}
	}

	private void initProcessBatch() throws IOException {
		if (this.processBatch == null) {
			String filePath = tpexConfigurationUtil.getFilePath("processBatchJsonPath");
			File file = ResourceUtils.getFile(filePath);
			if (!file.exists()) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				errorMessageParams.put("filePath", filePath);
				throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018, errorMessageParams);
			}
			ObjectMapper objMapper = new ObjectMapper();
			this.processBatch = objMapper.readValue(file, new TypeReference<List<ProcessBatchDTO>>() {
			});
		}
	}

	/**
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/processStatus")
	public ResponseEntity<CPCProcessStatusResponseDto> processStatus(
			@Valid @RequestBody CPCProcessStatusRequestDto request) {
		return new ResponseEntity<>(commonProcessControlService.processStatus(request), HttpStatus.OK);
	}

	/**
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/systemNames")
	public ResponseEntity<List<CPCSystemNamesResponseDto>> systemNames(
			@Valid @RequestBody CPCProcessNamesRequestDTO request) {
		return new ResponseEntity<>(commonProcessControlService.systemNames(request.getUserId()), HttpStatus.OK);
	}

	/**
	 * @param request
	 * @return CPCProcessLogsResponseDto list
	 * @throws Exception
	 */
	@PostMapping(path = "/processLogs")
	public ResponseEntity<List<CPCProcessLogsResponseDto>> processLogs(
			@Valid @RequestBody CPCProcessLogsRequestDTO request) {
		return new ResponseEntity<>(
				commonProcessControlService.processLogs(request.getProcessControlId(), request.getProcessId()),
				HttpStatus.OK);
	}

	/**
	 * @param processControlId
	 * @param processId
	 * @return byte stream
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/downloadProcessLogs", produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public ResponseEntity<Object> downloadProcessLogs(@RequestParam(defaultValue = "") String processControlId,
			@RequestParam(defaultValue = "") String processId) {
		Map<String, Object> resultMap;
		try {
			resultMap = (HashMap<String, Object>) commonProcessControlService.downloadProcessLogs(processControlId,
					processId);
		} catch (Exception e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3020, errorMessageParams);
		}

		if (((byte[]) resultMap.get("outStream")).length == 0) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3020, errorMessageParams);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("file_name", (String) resultMap.get("fileName"));
		headers.add("Content-Disposition", "attachment; filename=" + resultMap.get("fileName"));
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resultMap.get("outStream"));
	}

	@GetMapping(value = "/exportProcessLogs", produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin
	public ResponseEntity<InputStreamResource> downloadProcessErrorLogsToExcel(
			@RequestParam(defaultValue = "") String processControlId, @RequestParam(defaultValue = "") String processId)
					throws IOException {
		OemProcessCtrlEntity entity = oemProcessCtrlRepository.findByIdProcessControlIdAndIdBatchId(Integer.parseInt(processControlId),processId);
		String filePath = entity.getErrorFilePath();
		String filename = getFileNameFromFilePath(filePath);
		if (StringUtils.isEmpty(filePath)) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3018);
		}
		File filePathlocation = new File(filePath);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(filePathlocation));
		HttpHeaders headers = new HttpHeaders();
		headers.add("file_name", filename);
		headers.add("Content-Disposition", "attachment; filename=" + filename);

		return ResponseEntity.ok().headers(headers).contentLength(filePathlocation.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);

	}

	private String getFileNameFromFilePath(String filePath) {
		String filename = "";
		if (StringUtils.isNoneBlank(filePath)) {
			String[] sytringArray = filePath.split("/");
			filename = sytringArray[sytringArray.length - 1];
		}
		return filename;
	}

}
