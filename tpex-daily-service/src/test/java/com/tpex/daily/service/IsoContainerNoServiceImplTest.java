package com.tpex.daily.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.tpex.daily.dto.ContainerDestinationPlantCodeDTO;
import com.tpex.daily.dto.IsoContainerRequestDTO;
import com.tpex.daily.entity.CarFamilyMasterEntity;
import com.tpex.daily.entity.FinalDestEntity;
import com.tpex.daily.entity.TbMPlant;
import com.tpex.daily.repository.FinalDestRepository;
import com.tpex.daily.repository.PlantMasterRepository;
import com.tpex.daily.serviceimpl.IsoContainerNoServiceImpl;

@ExtendWith(MockitoExtension.class)
class IsoContainerNoServiceImplTest {

	@InjectMocks
	IsoContainerNoServiceImpl isoContainerNoServiceImpl;

	@Mock
	PlantMasterRepository plantMasterRepository;

	@Mock
	FinalDestRepository finalDestRepository;

	@Test
	void vanningPlantAndContainerDtlsTest() throws Exception {

		ContainerDestinationPlantCodeDTO containerDestinationPlantCodeDto = new ContainerDestinationPlantCodeDTO();
		List<TbMPlant> tbMPlantList = new ArrayList<>();
		tbMPlantList.add(new TbMPlant("B", "Bangpakong", "TMT"));
		tbMPlantList.add(new TbMPlant("F", "Banpho", "TMT"));

		when(plantMasterRepository.findAll()).thenReturn(tbMPlantList);

		List<FinalDestEntity> destinationNameWithCodeDTOList = new ArrayList<>();
		destinationNameWithCodeDTOList.add(new FinalDestEntity("0000", "CHINA", "TTM", "USD", "CN", "PCHEENIM",
				Date.valueOf("2001-01-04"), BigInteger.valueOf(1l), "CN", "TMT"));
		destinationNameWithCodeDTOList.add(new FinalDestEntity("0010", "HONGKONG", "TTM", "USD", "CN", "PCHEENIM",
				Date.valueOf("2007-01-05"), BigInteger.valueOf(1l), "CN", "TMT"));
		when(finalDestRepository.findAll()).thenReturn(destinationNameWithCodeDTOList);

		isoContainerNoServiceImpl.vanningPlantAndContainerDtls();
		assertNotNull(destinationNameWithCodeDTOList.get(0).getCmpCd());
		assertThat(tbMPlantList.get(0).getPlantCode()).isEqualTo("B");

	}

	@Test
	void vanningPlantAndContainerSearchTest() throws Exception {

		List<String[]> conatinerData = new ArrayList<>();
		conatinerData.add(new String[] { "2020-02-18", "MOL000000000003", "BBC976", "null", "DRYU4291205", "S",
				"THAG66990", "40", "ONE", "null" });
		conatinerData.add(new String[] { "2020-02-19", "MOL000000000005", "BBC975", "null", "ONEU0102420", "H", "null",
				"40", "ONE", "null" });

		when(plantMasterRepository.getConstainerDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(conatinerData);

		IsoContainerRequestDTO isoContainerRequestDTO = new IsoContainerRequestDTO();
		isoContainerRequestDTO.setContainerDestination("301B");
		isoContainerRequestDTO.setEtd("14/12/2022");
		isoContainerRequestDTO.setVanningMonth("2022/01");
		isoContainerRequestDTO.setVanningPlant("B");
		isoContainerRequestDTO.setContinerRanbanNo("BBC975");

		isoContainerNoServiceImpl.vanningPlantAndContainerSearch(isoContainerRequestDTO);
		assertNotNull(conatinerData.get(0)[0]);
	}
}
