package com.tpex.invoice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.dto.CommonDropdownDto;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.LotPartShortageDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterImporterDTO;
import com.tpex.invoice.service.ReturnablePackingMasterService;
import com.tpex.util.ConstantUtils;
import com.tpex.util.Util;


@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class ReturnablePackingMasterController {
	
	@Autowired
	private ReturnablePackingMasterService returnablePackingMasterService;
	
	
	
	@GetMapping(value = "/packingPlantAndImporterCode")
	public ResponseEntity<ReturnablePackingMasterDTO> onLoadPackingPlantAndImporterCode(@RequestParam(required = true) String cmpCd) {
		ReturnablePackingMasterDTO returnablePackingMasterDTO = new ReturnablePackingMasterDTO();
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		List<PlantMstEntity> plantMstEntityList = returnablePackingMasterService.packingPlantList(cmpCd);
		plantMstEntityList.stream().forEach(c -> returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto(c.getPlantCd(), c.getPlantCd() + ConstantUtils.HYPHEN + c.getPlantName())));
		
		returnablePackingMasterDTO.setPackingPlantList(returnablePackingMasterDtoList.stream().distinct()
				.collect(Collectors.toList()));

		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoListDest = new ArrayList<>();
		List<OemFnlDstMstEntity> finalDestEntityList = returnablePackingMasterService.destinationCodeList();
		finalDestEntityList.stream().forEach(c -> returnablePackingMasterDtoListDest.add(new CommonMultiSelectDropdownDto(c.getFdDstCd(), c.getFdDstCd() + ConstantUtils.HYPHEN + c.getFdDstNm())));
		
		returnablePackingMasterDTO.setImporterCodeList(returnablePackingMasterDtoListDest.stream().distinct()
				.collect(Collectors.toList()));
		

		return new ResponseEntity<>(returnablePackingMasterDTO, HttpStatus.OK);
	}
	
	@GetMapping(value = "/carFamilyAndDestinationMaster")
	public ResponseEntity<LotPartShortageDTO> onLoadCarFamilyAndDestinationMaster() {
		LotPartShortageDTO lotPartShortageDTO = new LotPartShortageDTO();
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		List<CarFamilyMasterEntity> carFamilyMasterEntityList = returnablePackingMasterService.carFamilyList();
		carFamilyMasterEntityList.stream().forEach(c -> returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto(c.getCarFmlyCode(), c.getCarFmlyCode() + ConstantUtils.HYPHEN + c.getCarFmlySrsName())));
		
		lotPartShortageDTO.setCarFamilyList(returnablePackingMasterDtoList.stream().distinct()
				.collect(Collectors.toList()));

		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoListDest = new ArrayList<>();
		List<OemFnlDstMstEntity> finalDestEntityList = returnablePackingMasterService.destinationCodeList();
		finalDestEntityList.stream().forEach(c -> returnablePackingMasterDtoListDest.add(new CommonMultiSelectDropdownDto(c.getFdDstCd(), c.getFdDstCd() + ConstantUtils.HYPHEN + c.getFdDstNm())));
		
		lotPartShortageDTO.setDestinationList(returnablePackingMasterDtoListDest.stream().distinct()
				.collect(Collectors.toList()));
		
		List<CommonDropdownDto> revisionNo = new ArrayList<>();
		revisionNo.add(new CommonDropdownDto(ConstantUtils.LATEST, ConstantUtils.LATEST));
		revisionNo.add(new CommonDropdownDto(ConstantUtils.ALL, ConstantUtils.ALL));
		
		lotPartShortageDTO.setRevisionNo(revisionNo);
		

		return new ResponseEntity<>(lotPartShortageDTO, HttpStatus.OK);
	}
	
	@GetMapping(value="/returnablePackingMasterList",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ReturnablePackingMasterImporterDTO> fetchReturnablePackingMasterDetails(@RequestParam String packingPlant,@RequestParam String importerCode, @RequestParam String moduleType, @RequestParam String cmpCd){

		if(!Util.nullCheck(packingPlant) || !Util.nullCheck(importerCode) || !Util.nullCheck(moduleType))
		{
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}

		ReturnablePackingMasterImporterDTO response = returnablePackingMasterService.fetchReturnablePackingMasterDetails(packingPlant, importerCode, moduleType, cmpCd);

		if (response == null) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
