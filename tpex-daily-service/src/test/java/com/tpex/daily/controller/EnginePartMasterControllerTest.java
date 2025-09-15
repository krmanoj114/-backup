package com.tpex.daily.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.tpex.daily.commonfiles.ApiResponseMessage;
import com.tpex.daily.service.EnginePartMasterService;
import com.tpex.daily.serviceimpl.EnginePartMasterServiceImpl;
import com.tpex.daily.dto.CarFamilyNameWithCodeDTO;
import com.tpex.daily.dto.DestinationCodeFinalResponseDTO;
import com.tpex.daily.dto.DestinationNameWithCodeDTO;
import com.tpex.daily.dto.EnginePartMasterResponseDTO;
import com.tpex.daily.dto.EnginePartMasterSaveRequestDto;
import com.tpex.daily.dto.EnginePartMstRequestDTO;
import com.tpex.daily.dto.ExporterImporterDataDTO;

@ExtendWith(MockitoExtension.class)
class EnginePartMasterControllerTest {
	
	private static final String PART_NO_VALUE = "120000E01200";
	private static final String UPDATE_DATE_VALUE = "20/06/2023";
	
	@InjectMocks
	EnginePartMasterController enginePartMasterController;	
	
	@Mock
	private EnginePartMasterService enginePartMasterService;
	
	@Test
	void destinationCodeandImporterControllerTest() throws Exception {
		
		DestinationCodeFinalResponseDTO destinationCodeFinalResponseDTO = new DestinationCodeFinalResponseDTO();
		
		List<DestinationNameWithCodeDTO> destinationNameWithCodeDTO = new ArrayList<>();
		destinationNameWithCodeDTO.add(new DestinationNameWithCodeDTO("001", "HONG KONG"));
		destinationNameWithCodeDTO.add(new DestinationNameWithCodeDTO("AMI", "AUSTRALIA"));
		
		List<CarFamilyNameWithCodeDTO> carFamilyNameWithCodeDTO = new ArrayList<>();
		carFamilyNameWithCodeDTO.add(new CarFamilyNameWithCodeDTO("CFC", "FORTUNNER"));
		carFamilyNameWithCodeDTO.add(new CarFamilyNameWithCodeDTO("CFA", "CORROLA"));

		destinationCodeFinalResponseDTO.setDestinationNameWithCodeDTO(destinationNameWithCodeDTO);
		destinationCodeFinalResponseDTO.setCarFamilyCodeWithCodeDTOs(carFamilyNameWithCodeDTO);
		
		when(enginePartMasterService.destinationCodeandImporterDtls()).thenReturn(destinationCodeFinalResponseDTO);		
		ResponseEntity<DestinationCodeFinalResponseDTO> result = enginePartMasterController.destinationCodeandImporter();		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(destinationCodeFinalResponseDTO, result.getBody());
	}
	
	@Test
	void enginePartMasterSearchTest() throws Exception {		
		
		EnginePartMasterResponseDTO enginePartMasterResponseDTO = new EnginePartMasterResponseDTO();
		 
		List<ExporterImporterDataDTO> exporterImporterDataDTO = new ArrayList<>();
		exporterImporterDataDTO.add(new ExporterImporterDataDTO("722S","301B","578W",PART_NO_VALUE,"**"));
		exporterImporterDataDTO.add(new ExporterImporterDataDTO("722T","578W","812K",PART_NO_VALUE,"**"));
		
		enginePartMasterResponseDTO.setExporterImporterDataDTO(exporterImporterDataDTO);
		
		when(enginePartMasterService.enginePartMasterSearchDtls(any())).thenReturn(enginePartMasterResponseDTO);
		
		EnginePartMstRequestDTO enginePartMstRequestDTO = new EnginePartMstRequestDTO();
		enginePartMstRequestDTO.setExporterCode("722S");
		enginePartMstRequestDTO.setImporterCode("301B");
		enginePartMstRequestDTO.setCarFamilyCode("578W");
		enginePartMstRequestDTO.setPartNo(PART_NO_VALUE);		
				
		ResponseEntity<EnginePartMasterResponseDTO> result = enginePartMasterController.enginePartMasterSearch(enginePartMstRequestDTO);		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(enginePartMasterResponseDTO, result.getBody());
	}
	
	@Test
	void saveEnginePartMasterTest() {

		when(enginePartMasterService.saveEnginePartMaster(any(), anyString())).thenReturn(true);

		String userId = "ROEM";

		List<EnginePartMasterSaveRequestDto> enginePartMasterDtos = new ArrayList<>();
		enginePartMasterDtos.add(new EnginePartMasterSaveRequestDto("812K","301B","722S",PART_NO_VALUE,"**","2","E","ROEM",UPDATE_DATE_VALUE,"TMT"));
		enginePartMasterDtos.add(new EnginePartMasterSaveRequestDto("812K","578W","722T",PART_NO_VALUE,"**","2","E","ROEM",UPDATE_DATE_VALUE,"TMT"));
		enginePartMasterDtos.add(new EnginePartMasterSaveRequestDto("812K","578W","722S",PART_NO_VALUE,"**","2","E","ROEM",UPDATE_DATE_VALUE,"TMT"));

		ResponseEntity<ApiResponseMessage> result = enginePartMasterController.saveEnginePartMaster(enginePartMasterDtos,userId);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

	}

	@Test
	void deleteEnginePartMasterTest() {

		Mockito.doNothing().when(enginePartMasterService).deleteEnginePartMaster(any());

		List<EnginePartMasterSaveRequestDto> enginePartMasterDtos = new ArrayList<>();
		enginePartMasterDtos.add(new EnginePartMasterSaveRequestDto("812K","301B","722S",PART_NO_VALUE,"**","2","E","ROEM",UPDATE_DATE_VALUE,"TMT"));

		ResponseEntity<ApiResponseMessage> result = enginePartMasterController.deleteEnginePartMaster(enginePartMasterDtos);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}


}
