package com.tpex.daily.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.tpex.daily.serviceimpl.EnginePartMasterServiceImpl;
import com.tpex.daily.dto.EnginePartMasterSaveRequestDto;
import com.tpex.daily.entity.CarFamilyMasterEntity;
import com.tpex.daily.entity.EnginePartMasterEntity;
import com.tpex.daily.entity.EnginePartMasterIdEntity;
import com.tpex.daily.entity.FinalDestEntity;
import com.tpex.daily.entity.PartMasterEntity;
import com.tpex.daily.repository.CarFamilyMastRepository;
import com.tpex.daily.repository.EnginePartMasterRepository;
import com.tpex.daily.repository.FinalDestRepository;
import com.tpex.daily.repository.PartMasterRespository;

@ExtendWith(MockitoExtension.class)
class EnginePartMasterServiceImplTest {

	private static final String PART_NO_VALUE = "120000E01200";
	private static final String UPDATE_DATE_VALUE = "20/06/2023";
	
	@InjectMocks
	EnginePartMasterServiceImpl enginePartMasterServiceImpl;

	@Mock
	EntityManager entityManager;

	@Mock
	Query query;

	@Mock
	FinalDestRepository finalDestRepository;

	@Mock
	CarFamilyMastRepository carFamilyMastRepository;
	
	@Mock
	Root<EnginePartMasterEntity> entityRoot;
	
	@Mock
	EnginePartMasterRepository enginePartMasterRepository;
	
	@Mock
	PartMasterRespository partMasterRespository;

	@Test
	void destinationCodeandImporterDtlsTest() throws Exception {

		List<FinalDestEntity> destinationNameWithCodeDTOList = new ArrayList<>();
		destinationNameWithCodeDTOList.add(new FinalDestEntity("0000", "CHINA", "TTM", "USD", "CN", "PCHEENIM",
				Date.valueOf("2001-01-04"), BigInteger.valueOf(1l), "CN", "TMT"));
		destinationNameWithCodeDTOList.add(new FinalDestEntity("0010", "HONGKONG", "TTM", "USD", "CN", "PCHEENIM",
				Date.valueOf("2007-01-05"), BigInteger.valueOf(1l), "CN", "TMT"));
		when(finalDestRepository.findAll(Sort.by("destinationCd").ascending()))
				.thenReturn(destinationNameWithCodeDTOList);

		List<CarFamilyMasterEntity> carFamilyNameWithCodeDTOList = new ArrayList<>();
		carFamilyNameWithCodeDTOList
				.add(new CarFamilyMasterEntity("1111", "FORTUNER", "X", "Test User", null, "Test", "AS", "TMT"));
		carFamilyNameWithCodeDTOList
				.add(new CarFamilyMasterEntity("1112", "GLANZA", "N", "Test User", null, "Test", "AN", "TMT"));
		when(carFamilyMastRepository.findAll(Sort.by("carFmlyCode").ascending()))
				.thenReturn(carFamilyNameWithCodeDTOList);

		enginePartMasterServiceImpl.destinationCodeandImporterDtls();
		assertNotNull(destinationNameWithCodeDTOList.get(0).getCmpCd());
		assertThat(carFamilyNameWithCodeDTOList.get(0).getCarFmlyCode()).isEqualTo("1111");

	}
	
	@Test
	void saveEnginePartMasterTest(){

		String userId = "ROEM";

		Mockito.lenient().when(enginePartMasterRepository.countByIdCrFmlyCodeAndIdImporterCodeAndIdExporterCodeAndIdLotModuleCodeAndIdPartNo(any(), any(), any(), any(), any())).thenReturn(0L);

		Optional<EnginePartMasterEntity> entity = Optional.ofNullable(new EnginePartMasterEntity(new EnginePartMasterIdEntity("301B", "578W", "**", PART_NO_VALUE, "722S"),"2","V","TestUser",null,"TMT"));

		Mockito.lenient().when(enginePartMasterRepository.findById(any())).thenReturn(entity);

		List<EnginePartMasterEntity> enginePartMasterEntities = new ArrayList<>();
		EnginePartMasterIdEntity enginePartMasterIdEntity = new EnginePartMasterIdEntity("301B", "578W", "**", PART_NO_VALUE, "722S");
		EnginePartMasterEntity enginePartMasterEntity = new EnginePartMasterEntity();
		enginePartMasterEntity.setId(enginePartMasterIdEntity);
		enginePartMasterEntity.setQuantity("1");
		enginePartMasterEntity.setEngineFlag("E");
		enginePartMasterEntity.setUpdateBy(userId);
		enginePartMasterEntity.setUpdateDate(null);
		enginePartMasterEntities.add(enginePartMasterEntity);

		Mockito.when(enginePartMasterRepository.saveAll(anyIterable())).thenReturn(enginePartMasterEntities);

		PartMasterEntity partMasterEntity = new PartMasterEntity();
		partMasterEntity.setPartNo(PART_NO_VALUE);
		partMasterEntity.setPartName("GASKET, EXHAUST PIPE");
		when(partMasterRespository.findById(any())).thenReturn(Optional.of(partMasterEntity));
		
		List<EnginePartMasterSaveRequestDto> enginePartMasterDtos = new ArrayList<>();
		enginePartMasterDtos.add(new EnginePartMasterSaveRequestDto("812K","301B","722S",PART_NO_VALUE,"**","2","E","ROEM",UPDATE_DATE_VALUE,"TMT"));
		enginePartMasterDtos.add(new EnginePartMasterSaveRequestDto("812K","578W","722T",PART_NO_VALUE,"**","2","E","ROEM",UPDATE_DATE_VALUE,"TMT"));
		boolean isSaved = enginePartMasterServiceImpl.saveEnginePartMaster(enginePartMasterDtos, userId);
		assertEquals(Boolean.TRUE, isSaved);
	}

	@Test
	void deleteEnginePartMasterTest() {

		doNothing().when(enginePartMasterRepository).deleteById(any());

		List<EnginePartMasterSaveRequestDto> enginePartMasterDtos = new ArrayList<>();
		enginePartMasterDtos.add(new EnginePartMasterSaveRequestDto("812K","301B","722S",PART_NO_VALUE,"**","2","E","ROEM",UPDATE_DATE_VALUE,"TMT"));

		Assertions.assertDoesNotThrow(() -> enginePartMasterServiceImpl.deleteEnginePartMaster(enginePartMasterDtos));
	}

	

}
