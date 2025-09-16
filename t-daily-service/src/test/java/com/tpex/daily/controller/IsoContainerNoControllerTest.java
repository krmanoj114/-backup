package com.tpex.daily.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

import com.tpex.daily.dto.ContainerDestinationPlantCodeDTO;
import com.tpex.daily.dto.ContainerDestinationWithCodeDTO;
import com.tpex.daily.dto.EnginePartMasterResponseDTO;
import com.tpex.daily.dto.EnginePartMstRequestDTO;
import com.tpex.daily.dto.ExporterImporterDataDTO;
import com.tpex.daily.dto.IsoContainerDataDto;
import com.tpex.daily.dto.IsoContainerFinalResponseDTO;
import com.tpex.daily.dto.IsoContainerRequestDTO;
import com.tpex.daily.dto.IsoContainerFinalResponseDTO;
import com.tpex.daily.dto.PlantMasterDTO;
import com.tpex.daily.service.IsoConatinerNoService;

@ExtendWith(MockitoExtension.class)
class IsoContainerNoControllerTest {

	@InjectMocks
	IsoContainerNoController containerNoController;

	@Mock
	private IsoConatinerNoService isoConatinerNoService;

	@Test
	void IsoContainerNoControllerDropDownTest() throws Exception {

		ContainerDestinationPlantCodeDTO containerDestinationPlantCodeDTO = new ContainerDestinationPlantCodeDTO();

		List<ContainerDestinationWithCodeDTO> containerDestinationWithCodeDTO = new ArrayList<>();
		containerDestinationWithCodeDTO.add(new ContainerDestinationWithCodeDTO("0005-CAMBODIA", "0005"));
		containerDestinationWithCodeDTO.add(new ContainerDestinationWithCodeDTO("666X-INDIA", "666X"));

		List<PlantMasterDTO> plantMasterDTO = new ArrayList<>();
		plantMasterDTO.add(new PlantMasterDTO("F", "Banpho"));
		plantMasterDTO.add(new PlantMasterDTO("B", "Bangpakong"));

		containerDestinationPlantCodeDTO.setContainerDestinationWithCodeDTO(containerDestinationWithCodeDTO);
		containerDestinationPlantCodeDTO.setPlantMasterDTO(plantMasterDTO);

		when(isoConatinerNoService.vanningPlantAndContainerDtls()).thenReturn(containerDestinationPlantCodeDTO);
		ResponseEntity<ContainerDestinationPlantCodeDTO> result = containerNoController
				.plantAndDestinationCodeDetails();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(containerDestinationPlantCodeDTO, result.getBody());
	}

	@Test
	void plantAndDestinationCodeSearchTest() throws Exception {

		IsoContainerFinalResponseDTO isoContainerFinalResponseDTO = new IsoContainerFinalResponseDTO();

		List<IsoContainerDataDto> isoContainerDataDto = new ArrayList<>();
		isoContainerDataDto.add(new IsoContainerDataDto("2020-02-18", "MOL000000000003", "BBC976", "Null",
				"DRYU4291205", "S", "THAG66990", "40", "ONE", "Null"));
		isoContainerDataDto.add(new IsoContainerDataDto("2020-02-18", "MOL000000000002", "BBC975", "Null",
				"ONEU0102420", "H", "THAG66990", "40", "ONE", "Null"));

		isoContainerFinalResponseDTO.setIsoContainerDataDto(isoContainerDataDto);

		IsoContainerRequestDTO isoContainerRequestDTO = new IsoContainerRequestDTO();
		isoContainerRequestDTO.setContainerDestination("301B");
		isoContainerRequestDTO.setEtd("2022-12-14");
		isoContainerRequestDTO.setVanningMonth("2022/01");
		isoContainerRequestDTO.setVanningPlant("B");
		isoContainerRequestDTO.setContinerRanbanNo("BBC975");

		when(isoConatinerNoService.vanningPlantAndContainerSearch(any())).thenReturn(isoContainerFinalResponseDTO);

		ResponseEntity<IsoContainerFinalResponseDTO> result = containerNoController
				.plantAndDestinationCodeSearch(isoContainerRequestDTO);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(isoContainerFinalResponseDTO, result.getBody());
	}

}
