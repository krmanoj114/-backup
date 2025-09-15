package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.invoice.dto.LotPartShortageDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterDetailsDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterImporterDTO;
import com.tpex.invoice.service.ReturnablePackingMasterService;
import com.tpex.invoice.serviceImpl.ReturnablePackingMasterServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReturnablePackingMasterControllerTest {
	
	@InjectMocks
	ReturnablePackingMasterController returnablePackingMasterController;
	
	@Mock
	ReturnablePackingMasterServiceImpl returnablePackingMasterServiceImpl;
	
	@Mock
	ReturnablePackingMasterService returnablePackingMasterService;
	
	
	@Test
	void fetchReturnablePackingMasterDetailsTest() {
		ReturnablePackingMasterImporterDTO response = new ReturnablePackingMasterImporterDTO();
		List<ReturnablePackingMasterDetailsDTO> list = new ArrayList<>();
		ReturnablePackingMasterDetailsDTO dto = new ReturnablePackingMasterDetailsDTO();
		dto.setImporterCode("301B");
		dto.setPackingPlant("S");
		dto.setModuleType("1B");
		dto.setModuleDesciption("1B");
		dto.setVanningDateFrom("27/05/2013");
		dto.setVanningDateTo("31/12/1999");
		list.add(dto);
		response.setReturnablePackingMasterDetails(list);
		
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("B","B-Bangpakong"));
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("S","S-Samrong"));
		response.setPackingPlantList(returnablePackingMasterDtoList);
		
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("B","B-Bangpakong"));
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("F","F-Banpho"));
		response.setImporterCodeList(returnablePackingMasterDtoList);
		
		when(returnablePackingMasterService.fetchReturnablePackingMasterDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(response);
		ResponseEntity<ReturnablePackingMasterImporterDTO> result = returnablePackingMasterController.fetchReturnablePackingMasterDetails("S-Samrong","301B-ARGENTINA","1B","TMT");
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(response, result.getBody());
	}
	
	@Test
	void testOnLoadPackingPlantAndImporterCode() {
		ReturnablePackingMasterDTO returnablePackingMasterDTO = new ReturnablePackingMasterDTO();
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoListDest = new ArrayList<>();
		
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("B","B-Bangpakong"));
		
		returnablePackingMasterDTO.setPackingPlantList(returnablePackingMasterDtoList);
		
		returnablePackingMasterDtoListDest.add(new CommonMultiSelectDropdownDto("B","B-Bangpakong"));
		returnablePackingMasterDTO.setImporterCodeList(returnablePackingMasterDtoListDest);
		List<OemFnlDstMstEntity> finalDestEntityList = new ArrayList<>();
		OemFnlDstMstEntity oemFnlDstMstEntity = new OemFnlDstMstEntity();
		oemFnlDstMstEntity.setFdDstCd("B");
		oemFnlDstMstEntity.setFdDstNm("Bangpakong");
		finalDestEntityList.add(oemFnlDstMstEntity);
		
		List<PlantMstEntity> plantMstEntityList = new ArrayList<>();
		PlantMstEntity plantMstEntity = new PlantMstEntity();
		plantMstEntity.setPlantCd("B");
		plantMstEntity.setPlantName("Bangpakong");
		plantMstEntityList.add(plantMstEntity);
		
		Mockito.when(returnablePackingMasterService.packingPlantList("TMT")).thenReturn(plantMstEntityList);
		Mockito.when(returnablePackingMasterService.destinationCodeList()).thenReturn(finalDestEntityList);		
		
		ResponseEntity<ReturnablePackingMasterDTO> destCodeAndPackingPlant = returnablePackingMasterController.onLoadPackingPlantAndImporterCode("TMT");
		assertNotNull(destCodeAndPackingPlant.getBody());
		assertEquals("B-Bangpakong", destCodeAndPackingPlant.getBody().getPackingPlantList().get(0).getLabel());
		assertEquals("B-Bangpakong", destCodeAndPackingPlant.getBody().getImporterCodeList().get(0).getLabel());
	}
	
	@Test
	void testonLoadCarFamilyAndDestinationMaster() {
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoListDest = new ArrayList<>();
		LotPartShortageDTO lotPartShortageDTO = new LotPartShortageDTO();
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("0007","0007-bvhjghjgjh"));
		
		lotPartShortageDTO.setCarFamilyList(returnablePackingMasterDtoList);
		
		returnablePackingMasterDtoListDest.add(new CommonMultiSelectDropdownDto("B","B-Bangpakong"));
		lotPartShortageDTO.setDestinationList(returnablePackingMasterDtoListDest);
		List<OemFnlDstMstEntity> finalDestEntityList = new ArrayList<>();
		OemFnlDstMstEntity oemFnlDstMstEntity = new OemFnlDstMstEntity();
		oemFnlDstMstEntity.setFdDstCd("B");
		oemFnlDstMstEntity.setFdDstNm("Bangpakong");
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
		assertEquals("B-Bangpakong", destCodeAndPackingPlant.getBody().getDestinationList().get(0).getLabel());
	}
	

}
