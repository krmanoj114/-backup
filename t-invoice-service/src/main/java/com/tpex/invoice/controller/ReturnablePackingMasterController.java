package com.tpex.invoice.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.tpex.dto.CommonDropdownDto;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.LotPartShortageDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterDetailsDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterImporterDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterSaveRequestDTO;
import com.tpex.invoice.service.ReturnablePackingMasterService;
import com.tpex.repository.ReturnablePackingMasterDetailsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.Util;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class ReturnablePackingMasterController {

	@Autowired
	private ReturnablePackingMasterService returnablePackingMasterService;

	
	@SuppressWarnings("unused")
	@Autowired
	private ReturnablePackingMasterDetailsRepository returnablePackingMasterDetailsRepository;
	
	

	@GetMapping(value = "/packingPlantAndImporterCode")
	public ResponseEntity<ReturnablePackingMasterDTO> onLoadPackingPlantAndImporterCode(
			@RequestParam(required = true) String cmpCd) {
		ReturnablePackingMasterDTO returnablePackingMasterDTO = new ReturnablePackingMasterDTO();
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		List<PlantMstEntity> plantMstEntityList = returnablePackingMasterService.packingPlantList(cmpCd);
		plantMstEntityList.stream()
				.forEach(c -> returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto(c.getPlantCd(),
						c.getPlantCd() + ConstantUtils.HYPHEN + c.getPlantName())));

		returnablePackingMasterDTO
				.setPackingPlantList(returnablePackingMasterDtoList.stream().distinct().collect(Collectors.toList()));

		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoListDest = new ArrayList<>();
		List<OemFnlDstMstEntity> finalDestEntityList = returnablePackingMasterService.destinationCodeList();
		finalDestEntityList.stream()
				.forEach(c -> returnablePackingMasterDtoListDest.add(new CommonMultiSelectDropdownDto(c.getFdDstCd(),
						c.getFdDstCd() + ConstantUtils.HYPHEN + c.getFdDstNm())));

		returnablePackingMasterDTO.setImporterCodeList(
				returnablePackingMasterDtoListDest.stream().distinct().collect(Collectors.toList()));

		return new ResponseEntity<>(returnablePackingMasterDTO, HttpStatus.OK);
	}

	@GetMapping(value = "/carFamilyAndDestinationMaster")
	public ResponseEntity<LotPartShortageDTO> onLoadCarFamilyAndDestinationMaster() {
		LotPartShortageDTO lotPartShortageDTO = new LotPartShortageDTO();
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		List<CarFamilyMasterEntity> carFamilyMasterEntityList = returnablePackingMasterService.carFamilyList();
		carFamilyMasterEntityList.stream()
				.forEach(c -> returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto(c.getCarFmlyCode(),
						c.getCarFmlyCode() + ConstantUtils.HYPHEN + c.getCarFmlySrsName())));

		lotPartShortageDTO
				.setCarFamilyList(returnablePackingMasterDtoList.stream().distinct().collect(Collectors.toList()));

		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoListDest = new ArrayList<>();
		List<OemFnlDstMstEntity> finalDestEntityList = returnablePackingMasterService.destinationCodeList();
		finalDestEntityList.stream()
				.forEach(c -> returnablePackingMasterDtoListDest.add(new CommonMultiSelectDropdownDto(c.getFdDstCd(),
						c.getFdDstCd() + ConstantUtils.HYPHEN + c.getFdDstNm())));

		lotPartShortageDTO.setDestinationList(
				returnablePackingMasterDtoListDest.stream().distinct().collect(Collectors.toList()));

		List<CommonDropdownDto> revisionNo = new ArrayList<>();
		revisionNo.add(new CommonDropdownDto(ConstantUtils.LATEST, ConstantUtils.LATEST));
		revisionNo.add(new CommonDropdownDto(ConstantUtils.ALL, ConstantUtils.ALL));

		lotPartShortageDTO.setRevisionNo(revisionNo);

		return new ResponseEntity<>(lotPartShortageDTO, HttpStatus.OK);
	}

	/**
	 * Method for search returnable packing master
	 * @author Mohd.Javed
	 * @param map
	 * @param importerCode
	 * @return
	 * @throws ParseException
	 */
	@GetMapping(value = "/returnablePackingMasterList", produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<ReturnablePackingMasterImporterDTO> fetchReturnablePackingMasterDetails(
			@RequestParam Map<String, String> map, @RequestParam List<String> importerCode) throws ParseException {

		String packingPlant = map.get("packingPlant").isBlank() ? null : map.get("packingPlant");
		String moduleType = map.get("moduleType").isBlank() ? null : map.get("moduleType");
		String cmpCd = map.get("cmpCd").isBlank() ? null : map.get("cmpCd");
		String returnableType = map.get("returnableType").isBlank() ? null : map.get("returnableType");
		String vanDateFrom = map.get("vanDateFrom").isBlank() ? null : map.get("vanDateFrom");
		String vanDateTo = map.get("vanDateTo").isBlank() ? null : map.get("vanDateTo");
        if(importerCode.isEmpty()) {
        	importerCode=null;
        	
        }
		if (!Util.nullCheck(returnableType)) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3001);
		}

		ReturnablePackingMasterImporterDTO response = returnablePackingMasterService
				.fetchReturnablePackingMasterDetails(packingPlant, moduleType, cmpCd, returnableType, vanDateFrom,
						vanDateTo, importerCode);

		if (response == null) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	@PostMapping(value= "/saveReturnablePackingMasterList", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> saveReturnablePackingMaster(@RequestBody ReturnablePackingMasterSaveRequestDTO returnablePackingMasterSaveRequestDTO, @RequestParam String userId) {
		if(returnablePackingMasterService.saveReturnablePackingMaster(returnablePackingMasterSaveRequestDTO, userId)) 
			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);
		else {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3006, errorMessageParams);
		}
	}
	
	@DeleteMapping(value= "/deleteReturnablePackingMasterList", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseMessage> deleteReturnablePackingMaster(@RequestBody List<ReturnablePackingMasterDetailsDTO> returnablePackingMasterDetailsDTOList) {
		try {
		returnablePackingMasterService.deleteReturnablePackingMaster(returnablePackingMasterDetailsDTOList);
		Map<String, Object> errorMessageParams = new HashMap<>();
		errorMessageParams.put("count", returnablePackingMasterDetailsDTOList.size());
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3002, errorMessageParams), HttpStatus.OK);
		} catch (Exception e) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3005, errorMessageParams);
		}
	}

}
