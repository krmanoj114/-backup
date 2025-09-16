package com.tpex.daily.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.daily.commonfiles.ApiResponseMessage;
import com.tpex.daily.dto.ContainerDestinationPlantCodeDTO;
import com.tpex.daily.dto.IsoContainerFinalResponseDTO;
import com.tpex.daily.dto.IsoContainerMasterDto;
import com.tpex.daily.dto.IsoContainerRequestDTO;
import com.tpex.daily.exception.ResourceNotFoundException;
import com.tpex.daily.service.IsoConatinerNoService;
import com.tpex.daily.util.ConstantUtils;

import io.netty.util.internal.StringUtil;

@RestController
@CrossOrigin
@RequestMapping("/isoContainer")
public class IsoContainerNoController {

	@Autowired
	IsoConatinerNoService isoConatinerNoService;

	@GetMapping(value = "/plantAndDestinationCode", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContainerDestinationPlantCodeDTO> plantAndDestinationCodeDetails() {

		return new ResponseEntity<>(isoConatinerNoService.vanningPlantAndContainerDtls(), HttpStatus.OK);
	}

	@PostMapping(value = "/plantAndDestinationCodeSearch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IsoContainerFinalResponseDTO> plantAndDestinationCodeSearch(
			@RequestBody IsoContainerRequestDTO containerRequestDTO) throws ParseException {

		if (StringUtil.isNullOrEmpty(containerRequestDTO.getVanningMonth())
				|| StringUtil.isNullOrEmpty(containerRequestDTO.getVanningPlant())
				|| StringUtil.isNullOrEmpty(containerRequestDTO.getContainerDestination())) {

			throw new ResourceNotFoundException(ConstantUtils.ERR_CM_3001);

		}

		return new ResponseEntity<>(isoConatinerNoService.vanningPlantAndContainerSearch(containerRequestDTO),
				HttpStatus.OK);
	}

	@PutMapping("/add")
	public ResponseEntity<ApiResponseMessage> add(@RequestBody List<IsoContainerMasterDto> isoContainerMasterDtoList)
			throws ParseException {

		if (Boolean.TRUE.equals(isoConatinerNoService.save(isoContainerMasterDtoList))) {

			Map<String, Object> messageParam = new HashMap<>();
			messageParam.put("No. of Records", isoContainerMasterDtoList.size());

			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003, messageParam),
					HttpStatus.OK);
		}

		else {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("Error_Code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR,
					ConstantUtils.ERR_CM_3006, errorMessageParams), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

}
