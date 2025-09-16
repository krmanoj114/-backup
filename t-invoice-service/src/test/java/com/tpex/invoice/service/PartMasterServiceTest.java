package com.tpex.invoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.entity.InhouseShopMasterEntity;
import com.tpex.entity.PartMasterEntity;
import com.tpex.invoice.dto.PartMasterSearchRequestDto;
import com.tpex.invoice.dto.PartMasterSearchResponseDto;
import com.tpex.invoice.serviceimpl.PartMasterServiceImpl;
import com.tpex.repository.InhouseShopMasterRepo;
import com.tpex.repository.PartMasterDao;

@ExtendWith(MockitoExtension.class)
class PartMasterServiceTest {

	private static String partNo = "091010K43000";
	private static String partName = "TOOL SET, STD L/JACK";
	private static String partType = "1";
	private static String inhouseShop = "1";
	private static String weight = "0.360";
	private static String batchUpdateDate = "2018-02-21";
	private static String cmpCode = "TMT";
	private static String updateBy = "SUWITCHA";
	private static String updateDate = "2008-06-14";

	@Mock
	private PartMasterDao partMasterDao;

	@InjectMocks
	private PartMasterServiceImpl partMasterService;

	@Mock
	InhouseShopMasterRepo inhouseShopRepo;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testSearch() {
		// Prepare test data
		PartMasterSearchRequestDto requestDto = new PartMasterSearchRequestDto(partNo, partName, partType, cmpCode);
		List<PartMasterEntity> partMasterEntities = new ArrayList<>();
		// Add sample PartMasterEntity objects to the list (replace with your actual
		// data)

		PartMasterEntity e1 = new PartMasterEntity(partNo, partName, partType, inhouseShop, weight, updateDate,
				updateBy, batchUpdateDate, cmpCode);

		partMasterEntities.add(e1);

		// Mock the behavior of the partMasterRepositoryDao
		when(partMasterDao.search(requestDto)).thenReturn(partMasterEntities);

		// Call the method to be tested
		List<PartMasterSearchResponseDto> result = partMasterService.search(requestDto);

		// Assertions
		assertEquals(partMasterEntities.size(), result.size());

		assertEquals(partMasterEntities.get(0).getPartNo(), partNo);

		// Add more assertions as needed based on your actual data

		// Verify that the partMasterRepositoryDao.search method was called once with
		// the correct parameter
		verify(partMasterDao, times(1)).search(requestDto);

	}

	@Test
	void downloadServiceTest() throws Exception {
		List<InhouseShopMasterEntity> inhouseShopMaster = new ArrayList<>();
		inhouseShopMaster.add(new InhouseShopMasterEntity("LC", "Local maker", "DATACONV", "2002-03-23", null, "TMT"));

		when(inhouseShopRepo.findAll()).thenReturn(inhouseShopMaster);

		HttpServletResponse response = mock(HttpServletResponse.class);
		String headerkey = "Content-Disposition";
		String headerValue = "attachment;filename=Part_Master.xlxs";
		response.setHeader(headerkey, headerValue);

		PartMasterSearchRequestDto partMasterSearchRequestDto = new PartMasterSearchRequestDto();
		partMasterSearchRequestDto.setCmpCd("TMT");
		partMasterSearchRequestDto.setPartNo("019990K03300");
		partMasterSearchRequestDto.setPartName("");
		partMasterSearchRequestDto.setPartType("");
		partMasterService.download(response, partMasterSearchRequestDto);

		Assertions.assertDoesNotThrow(

				() -> partMasterService.download(response, partMasterSearchRequestDto));
	}

}
