package com.tpex.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.admin.dto.CompanyDropdownResponse;
import com.tpex.admin.dto.CompanyPlantMappingDTO;
import com.tpex.admin.entity.TbMUserEntity;
import com.tpex.admin.repository.CompanyDropdownRepository;
import com.tpex.admin.repository.TbMUserRepository;

import static org.mockito.Mockito.anyString;


@ExtendWith(MockitoExtension.class)
class DropdownServiceTest {

	@InjectMocks
	private DropdownService dropdownService;
	@Mock
	private CompanyDropdownRepository companyDropdownRepository;
	@Mock
	private TbMUserRepository tbMUserRepository;
	
	@Test
	void getCompanyAndPlantDropdownByUserIdTest() {
		
		List<CompanyPlantMappingDTO> fromDb = new ArrayList<>();
		CompanyPlantMappingDTO dto = new CompanyPlantMappingDTO();
		dto.setCompanyCode("stm");
		dto.setCompanyName("STM");
		dto.setPlantCode("A");
		dto.setPlantName("S-PlantA");
		fromDb.add(dto);
		dto = new CompanyPlantMappingDTO();
		dto.setCompanyCode("stm");
		dto.setCompanyName("STM");
		dto.setPlantCode("B");
		dto.setPlantName("S-PlantB");
		fromDb.add(dto);
		TbMUserEntity mockUser = new TbMUserEntity();
		mockUser.setCompanyCode("tmap-em");
		Optional<TbMUserEntity> optUser = Optional.of(mockUser);
		
		when(companyDropdownRepository.findCompanyAndPlantDropdownByUser(anyString())).thenReturn(fromDb);
		when(tbMUserRepository.findById(anyString())).thenReturn(optUser);
		
		CompanyDropdownResponse res = dropdownService.getCompanyAndPlantDropdownByUserId("userId");
		
		assertNotNull(res);
		assertEquals("tmt", res.getCompanyDefault());
		assertEquals(2, res.getCompanies().get(0).getPlants().size());
		
	}

}
