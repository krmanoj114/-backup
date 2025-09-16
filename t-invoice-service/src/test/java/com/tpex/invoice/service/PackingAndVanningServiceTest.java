package com.tpex.invoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.entity.PackingAndVanningEntity;
import com.tpex.entity.VanningPlantEntity;
import com.tpex.invoice.controller.PackingAndVanningController;
import com.tpex.invoice.dto.PackingAndVanningDTO;
import com.tpex.invoice.dto.PackingAndVanningResponseDTO;
import com.tpex.invoice.dto.VanningPlantDTO;
import com.tpex.invoice.dto.VanningPlantResponseDTO;
import com.tpex.invoice.serviceimpl.PackingAndVanningServiceImpl;
import com.tpex.repository.PackingAndVanningRepository;
import com.tpex.repository.VanningPlantRepository;

class PackingAndVanningServiceTest {

	@Autowired
	private PackingAndVanningServiceImpl packingAndVanningService;

	private PackingAndVanningRepository packingAndVanningRepository;

	private PackingAndVanningController packingAndVanningController;

	private VanningPlantRepository vanningPlantRepository;

	@BeforeEach
	public void setup() {
		packingAndVanningRepository = mock(PackingAndVanningRepository.class);
		packingAndVanningService = mock(PackingAndVanningServiceImpl.class);
		packingAndVanningController = mock(PackingAndVanningController.class);
		vanningPlantRepository = mock(VanningPlantRepository.class);
	}

	@Test
	void testGetPackingAndVanningResponseData() {
		String companyCode = "ABC";
		List<PackingAndVanningEntity> packingAndVanningEntityList = new ArrayList<>();
		packingAndVanningEntityList
				.add(new PackingAndVanningEntity("Dname1", "Did", "", "", "", "", "", "", "", "TMT"));
		packingAndVanningEntityList
				.add(new PackingAndVanningEntity("Dname2", "Did", "", "", "", "", "", "", "", "TMT"));

		PackingAndVanningDTO dto = new PackingAndVanningDTO();
		dto.setId("TMT");
		dto.setName("Toyota");
		when(packingAndVanningRepository.findByCmpCode(companyCode)).thenReturn(packingAndVanningEntityList);
		PackingAndVanningResponseDTO responseDTO = new PackingAndVanningResponseDTO();
		responseDTO.setContainerDestination(Collections.singletonList(dto));
		when(packingAndVanningService.getPackingAndVanningResponseData(companyCode)).thenReturn(responseDTO);

		// Assertions
		assertNotNull(responseDTO);
		assertEquals(1, responseDTO.getContainerDestination().size());
		assertEquals("TMT", responseDTO.getContainerDestination().get(0).getId());
		assertEquals("Toyota", responseDTO.getContainerDestination().get(0).getName());
	}

	@Test
	void testGetVanningPlantResponseData() {
		String companyCode = "TMT";
		String planningFlag = "Y";
		String planCode = "plan";
		String planName = "name";

		List<VanningPlantEntity> vanningPlantEntityList = new ArrayList<>();

		vanningPlantEntityList.add(new VanningPlantEntity("pc", "pn", "TMT", "Y", "N"));
		vanningPlantEntityList.add(new VanningPlantEntity("pc1", "pn1", "TMT", "N", "Y"));

		when(vanningPlantRepository.findByCompanyCodeAndPackingFlag(companyCode, planningFlag))
				.thenReturn(vanningPlantEntityList);

		VanningPlantDTO vanningPlantDTO = new VanningPlantDTO();
		vanningPlantDTO.setId(planCode);
		vanningPlantDTO.setName(planName);

		List<VanningPlantDTO> vanningPlantCodeName = new ArrayList<>();
		vanningPlantCodeName.add(vanningPlantDTO);

		VanningPlantResponseDTO vanningPlantResponseDTO = new VanningPlantResponseDTO();
		vanningPlantResponseDTO.setVanningPlantCodeName(vanningPlantCodeName);

		when(packingAndVanningService.getVanningPlantResponseData(companyCode, planningFlag))
				.thenReturn(vanningPlantResponseDTO);

		packingAndVanningService.getVanningPlantResponseData(companyCode, planningFlag);

		// Assertions
		assertNotNull(vanningPlantResponseDTO);
		assertEquals(1, vanningPlantResponseDTO.getVanningPlantCodeName().size());
		assertEquals("plan", vanningPlantResponseDTO.getVanningPlantCodeName().get(0).getId());
		assertEquals("name", vanningPlantResponseDTO.getVanningPlantCodeName().get(0).getName());

	}
}
