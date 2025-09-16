package com.tpex.invoice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.invoice.dto.LotPartShortageDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterDTO;
import com.tpex.invoice.service.ReturnablePackingMasterService;
import com.tpex.invoice.serviceimpl.ReturnablePackingMasterServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReturnablePackingMasterControllerTest {
	
	@InjectMocks
	ReturnablePackingMasterController returnablePackingMasterController;
	
	@Mock
	ReturnablePackingMasterServiceImpl returnablePackingMasterServiceImpl;
	
	@Mock
	ReturnablePackingMasterService returnablePackingMasterService;
	private static final String destNation = "B-Bangpakong";
	private static final String bangpakong = "Bangpakong";
	

	@Test
	void testOnLoadPackingPlantAndImporterCode() {
		ReturnablePackingMasterDTO returnablePackingMasterDTO = new ReturnablePackingMasterDTO();
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoListDest = new ArrayList<>();
		
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("B",destNation));
		
		returnablePackingMasterDTO.setPackingPlantList(returnablePackingMasterDtoList);
		
		returnablePackingMasterDtoListDest.add(new CommonMultiSelectDropdownDto("B",destNation));
		returnablePackingMasterDTO.setImporterCodeList(returnablePackingMasterDtoListDest);
		List<OemFnlDstMstEntity> finalDestEntityList = new ArrayList<>();
		OemFnlDstMstEntity oemFnlDstMstEntity = new OemFnlDstMstEntity();
		oemFnlDstMstEntity.setFdDstCd("B");
		oemFnlDstMstEntity.setFdDstNm(bangpakong);
		finalDestEntityList.add(oemFnlDstMstEntity);
		
		List<PlantMstEntity> plantMstEntityList = new ArrayList<>();
		PlantMstEntity plantMstEntity = new PlantMstEntity();
		plantMstEntity.setPlantCd("B");
		plantMstEntity.setPlantName(bangpakong);
		plantMstEntityList.add(plantMstEntity);
		
		Mockito.when(returnablePackingMasterService.packingPlantList("TMT")).thenReturn(plantMstEntityList);
		Mockito.when(returnablePackingMasterService.destinationCodeList()).thenReturn(finalDestEntityList);		
		
		ResponseEntity<ReturnablePackingMasterDTO> destCodeAndPackingPlant = returnablePackingMasterController.onLoadPackingPlantAndImporterCode("TMT");
		assertNotNull(destCodeAndPackingPlant.getBody());
		assertEquals(destNation, destCodeAndPackingPlant.getBody().getPackingPlantList().get(0).getLabel());
		assertEquals(destNation, destCodeAndPackingPlant.getBody().getImporterCodeList().get(0).getLabel());
	}
	
	@Test
	void testonLoadCarFamilyAndDestinationMaster() {
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoListDest = new ArrayList<>();
		LotPartShortageDTO lotPartShortageDTO = new LotPartShortageDTO();
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("0007","0007-bvhjghjgjh"));
		
		lotPartShortageDTO.setCarFamilyList(returnablePackingMasterDtoList);
		
		returnablePackingMasterDtoListDest.add(new CommonMultiSelectDropdownDto("B",destNation));
		lotPartShortageDTO.setDestinationList(returnablePackingMasterDtoListDest);
		List<OemFnlDstMstEntity> finalDestEntityList = new ArrayList<>();
		OemFnlDstMstEntity oemFnlDstMstEntity = new OemFnlDstMstEntity();
		oemFnlDstMstEntity.setFdDstCd("B");
		oemFnlDstMstEntity.setFdDstNm(bangpakong);
		finalDestEntityList.add(oemFnlDstMstEntity);
		
		List<CarFamilyMasterEntity> carFamilyMasterEntityList = new ArrayList<>();
		CarFamilyMasterEntity carFamilyMasterEntity = new CarFamilyMasterEntity();
		carFamilyMasterEntity.setCarFmlyCode("0007");
		carFamilyMasterEntity.setCarFmlySrsName("bvhjghjgjh");
		carFamilyMasterEntityList.add(carFamilyMasterEntity);
		
		Mockito.when(returnablePackingMasterService.carFamilyList()).thenReturn(carFamilyMasterEntityList);
		Mockito.when(returnablePackingMasterService.destinationCodeList()).thenReturn(finalDestEntityList);		
		
		ResponseEntity<LotPartShortageDTO> destCodeAndPackingPlant = returnablePackingMasterController.onLoadCarFamilyAndDestinationMaster();
		assertNotNull(destCodeAndPackingPlant.getBody());
		assertEquals("0007-bvhjghjgjh", destCodeAndPackingPlant.getBody().getCarFamilyList().get(0).getLabel());
		assertEquals(destNation, destCodeAndPackingPlant.getBody().getDestinationList().get(0).getLabel());
	}
	

}
