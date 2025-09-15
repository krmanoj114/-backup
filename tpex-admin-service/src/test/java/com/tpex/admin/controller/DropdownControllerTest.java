package com.tpex.admin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.tpex.admin.service.DropdownService;
import com.tpex.admin.dto.CompanyDropdownDTO;
import com.tpex.admin.dto.CompanyDropdownResponse;
import com.tpex.admin.dto.PlantDropdownDTO;

import static org.mockito.Mockito.anyString;


@ExtendWith(MockitoExtension.class)
class DropdownControllerTest {

	@InjectMocks
	private DropdownController dropdownController;
	@Mock
	private DropdownService dropdownService;
	
	@Test
	void getCompanyAndPlantDropdownByUserIdTest() {
		CompanyDropdownResponse mockRes = new CompanyDropdownResponse();
		List<CompanyDropdownDTO> dropdown = new ArrayList<>();
		List<PlantDropdownDTO> plantDd = new ArrayList<>();
		PlantDropdownDTO plantDto = new PlantDropdownDTO();
		plantDto.setCode("A");
		plantDto.setName("S-PlantA");
		plantDd.add(plantDto);
		plantDto = new PlantDropdownDTO();
		plantDto.setCode("B");
		plantDto.setName("S-PlantB");
		plantDd.add(plantDto);
		CompanyDropdownDTO dto = new CompanyDropdownDTO();
		dto.setCode("stm");
		dto.setName("STM");
		dto.setPlants(plantDd);
		dropdown.add(dto);
		mockRes.setCompanies(dropdown);
		
		when(dropdownService.getCompanyAndPlantDropdownByUserId(anyString())).thenReturn(mockRes);
		
		ResponseEntity<CompanyDropdownResponse> res = dropdownController.getCompanyAndPlantDropdownByUserId("userId");
		
		CompanyDropdownResponse body = res.getBody();
		assertNotNull(body);
		assertEquals(1, body.getCompanies().size());
		assertEquals(2, body.getCompanies().get(0).getPlants().size());
	}

}
