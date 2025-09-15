package com.tpex.invoice.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.entity.CarFamilyDestinationMasterIdEntity;
import com.tpex.entity.MixPrivilegeMasterEntity;
import com.tpex.entity.OemShippingCtrlMstIdEntity;
import com.tpex.entity.PrivilegeMasterEntity;
import com.tpex.entity.PrivilegeMasterIdEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.MixPrivilegeDetailsListResponseDto;
import com.tpex.invoice.dto.MixPrivilegeMasterSaveRequestDto;
import com.tpex.repository.CarFamilyDestinationMasterRepository;
import com.tpex.repository.MixPrivilegeMasterRepository;
import com.tpex.repository.PrivilegeMasterRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class MixPrivilegeMasterServiceImplTest {

	@InjectMocks
	private MixPrivilegeMasterServiceImpl mixPrivilegeMasterServiceImpl;

	@Mock
	CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;

	@Mock
	PrivilegeMasterRepository privilegeMasterRepository;

	@Mock
	MixPrivilegeMasterRepository mixPrivilegeMasterRepository;


	@Test
	void fetchMixPrivilegeDetails(){

		List<MixPrivilegeMasterEntity> mixPrivilegeMasterList = new ArrayList<>();

		MixPrivilegeMasterEntity mixPrivilegeMasterEntity = new MixPrivilegeMasterEntity();

		String str1="2016-09-01";  
		Date date1=Date.valueOf(str1);

		String str2="2017-07-31";  
		Date date2=Date.valueOf(str2);

		mixPrivilegeMasterEntity.setDestinationCode("715B");
		mixPrivilegeMasterEntity.setCarFmlyCode("889W");
		mixPrivilegeMasterEntity.setPrivMstId(101);
		mixPrivilegeMasterEntity.setEffFrom(date1);
		mixPrivilegeMasterEntity.setEffTo(date2);
		mixPrivilegeMasterEntity.setExporterCode("1");
		mixPrivilegeMasterEntity.setPriorityOne("NON-1");
		mixPrivilegeMasterEntity.setPriorityTwo("NON-4");
		mixPrivilegeMasterEntity.setPriorityThree("ATIGA-Q");
		mixPrivilegeMasterEntity.setPriorityFour("FTA-X");
		mixPrivilegeMasterEntity.setPriorityFive("AICO-H");

		mixPrivilegeMasterList.add(mixPrivilegeMasterEntity);

		Mockito.lenient().when(mixPrivilegeMasterRepository.findByCarFmlyCodeAndDestinationCode(Mockito.anyString(),Mockito.anyString())).thenReturn(mixPrivilegeMasterList);


		List<CarFamilyDestinationMasterEntity> carFamilyDestinationMasterEntityList = new ArrayList<>();

		CarFamilyDestinationMasterEntity carFamilyDestinationMasterEntity1 = new CarFamilyDestinationMasterEntity();
		CarFamilyDestinationMasterIdEntity CarFamilyDestinationMasterIdEntity1 = new CarFamilyDestinationMasterIdEntity();
		CarFamilyDestinationMasterIdEntity1.setReExporterCode("1");
		carFamilyDestinationMasterEntity1.setId(CarFamilyDestinationMasterIdEntity1);
		carFamilyDestinationMasterEntity1.setSrsName("VIOS");
		carFamilyDestinationMasterEntityList.add(carFamilyDestinationMasterEntity1);

		CarFamilyDestinationMasterEntity carFamilyDestinationMasterEntity2 = new CarFamilyDestinationMasterEntity();
		CarFamilyDestinationMasterIdEntity CarFamilyDestinationMasterIdEntity2 = new CarFamilyDestinationMasterIdEntity();
		CarFamilyDestinationMasterIdEntity2.setReExporterCode("6");
		carFamilyDestinationMasterEntity2.setId(CarFamilyDestinationMasterIdEntity1);
		carFamilyDestinationMasterEntity2.setSrsName("VIOS");
		carFamilyDestinationMasterEntityList.add(carFamilyDestinationMasterEntity2);

		Mockito.lenient().when(carFamilyDestinationMasterRepository.findByIdCarFmlyCodeAndIdDestinationCode(Mockito.anyString(),Mockito.anyString())).thenReturn(carFamilyDestinationMasterEntityList);

		List<PrivilegeMasterEntity> privilegeList = new ArrayList<>();

		PrivilegeMasterEntity privilegeMasterEntity1= new PrivilegeMasterEntity();
		PrivilegeMasterIdEntity privilegeMasterIdEntity1 = new PrivilegeMasterIdEntity();
		privilegeMasterIdEntity1.setPrivilegeCode("1");
		privilegeMasterIdEntity1.setPrivilegeName("NON");
		privilegeMasterEntity1.setId(privilegeMasterIdEntity1);
		privilegeList.add(privilegeMasterEntity1);

		PrivilegeMasterEntity privilegeMasterEntity2= new PrivilegeMasterEntity();
		PrivilegeMasterIdEntity privilegeMasterIdEntity2 = new PrivilegeMasterIdEntity();
		privilegeMasterIdEntity2.setPrivilegeCode("2");
		privilegeMasterIdEntity2.setPrivilegeName("AICO");
		privilegeMasterEntity2.setId(privilegeMasterIdEntity2);
		privilegeList.add(privilegeMasterEntity2);

		Mockito.lenient().when(privilegeMasterRepository.findPriorities()).thenReturn(privilegeList);

		String reExporterName = "TOYOTA";

		Mockito.lenient().when(carFamilyDestinationMasterRepository.findReExporterName(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(reExporterName);


		MixPrivilegeDetailsListResponseDto mixPrivilegeDetailsListResponseDto = mixPrivilegeMasterServiceImpl.fetchMixPrivilegeDetails("889W","715B");
		assertThat(mixPrivilegeDetailsListResponseDto).isNotNull();
	}

	@Test
	void deleteMixPrivilegeMaster(){

		Optional<MixPrivilegeMasterEntity> entity = Optional.ofNullable(new MixPrivilegeMasterEntity());

		Mockito.lenient().when(mixPrivilegeMasterRepository.findById(Mockito.anyInt())).thenReturn(entity);

		doNothing().when(mixPrivilegeMasterRepository).deleteAllById(Mockito.anyIterable());

		List<Integer> mixPrivilegeMstIdList = new ArrayList<>();
		mixPrivilegeMstIdList.add(201);
		mixPrivilegeMstIdList.add(202);


		Assertions.assertDoesNotThrow(() -> mixPrivilegeMasterServiceImpl.deleteMixPrivilegeMaster(mixPrivilegeMstIdList));
	}
	
}