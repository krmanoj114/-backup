package com.tpex.invoice.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.entity.ReturnablePackingMasterEntity;
import com.tpex.entity.ReturnablePackingMasterIdEntity;
import com.tpex.invoice.dto.ReturnablePackingMasterDetailsDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterImporterDTO;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.PlantMstRepository;
import com.tpex.repository.ReturnablePackingMasterDetailsRepository;

@ExtendWith(MockitoExtension.class)
 class ReturnablePackingMasterServiceImplTest {
	
	@InjectMocks
	private ReturnablePackingMasterServiceImpl returnablePackingMasterServiceImpl;
	
	@Mock
	private PlantMstRepository plantMstRepository;
	
	@Mock
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	@Mock
	private ReturnablePackingMasterDetailsRepository returnablePackingMasterDetailsRepository;
	
	@Test
	void fetchReturnablePackingMasterDetailsTest(){
		
		String str1="2016-09-01";  
		Date date1=Date.valueOf(str1);

		String str2="2017-07-31";  
		Date date2=Date.valueOf(str2);
		
		ReturnablePackingMasterImporterDTO response = new ReturnablePackingMasterImporterDTO();
		List<ReturnablePackingMasterDetailsDTO> list = new ArrayList<>();
		List<ReturnablePackingMasterEntity> returnablePackingMasterList = new ArrayList<>();

		ReturnablePackingMasterEntity returnablePackingMasterEntity = new ReturnablePackingMasterEntity();
		List<ReturnablePackingMasterIdEntity> returnablePackingMasterIdEntityList = new ArrayList<>();
		ReturnablePackingMasterIdEntity returnablePackingMasterIdEntity = new ReturnablePackingMasterIdEntity();
		returnablePackingMasterIdEntity.setImpCd("301B");
		returnablePackingMasterIdEntity.setModType("1B");
		returnablePackingMasterIdEntity.setPlantCd("S");
		returnablePackingMasterIdEntity.setVanFrom(date1);
		returnablePackingMasterIdEntityList.add(returnablePackingMasterIdEntity);
		

		returnablePackingMasterEntity.setId(returnablePackingMasterIdEntity);
		returnablePackingMasterEntity.setModDescription("1B");
		returnablePackingMasterEntity.setVanTo(date2);
		returnablePackingMasterList.add(returnablePackingMasterEntity);

		Mockito.lenient().when(returnablePackingMasterDetailsRepository.findByIdPlantCdAndIdImpCdAndIdModType(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(returnablePackingMasterList);
		ReturnablePackingMasterDetailsDTO dto = new ReturnablePackingMasterDetailsDTO();
		dto.setImporterCode("301B");
		dto.setPackingPlant("S");
		dto.setModuleType("1B");
		dto.setModuleDesciption("1B");
		dto.setVanningDateFrom("27/05/2013");
		dto.setVanningDateTo("31/12/1999");
		list.add(dto);
		response.setReturnablePackingMasterDetails(list);
		
		
		List<PlantMstEntity> plantMstEntityList = new ArrayList<>();
		PlantMstEntity plantMstEntity = new PlantMstEntity();
		plantMstEntity.setPlantCd("B");
		plantMstEntity.setPlantName("Bangpakong");
		plantMstEntityList.add(plantMstEntity);
		
		Mockito.when(plantMstRepository.findByCmpCdOrderByPlantCdAsc("TMT")).thenReturn(plantMstEntityList);
		List<CommonMultiSelectDropdownDto> returnablePackingMasterDtoList = new ArrayList<>();
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("B","B-Bangpakong"));
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("S","S-Samrong"));
		response.setPackingPlantList(returnablePackingMasterDtoList);
		
		
		List<OemFnlDstMstEntity> finalDestEntityList = new ArrayList<>();
		OemFnlDstMstEntity oemFnlDstMstEntity = new OemFnlDstMstEntity();
		oemFnlDstMstEntity.setFdDstCd("B");
		oemFnlDstMstEntity.setFdDstNm("Bangpakong");
		finalDestEntityList.add(oemFnlDstMstEntity);
		
		Mockito.when(oemFnlDstMstRepository.findAllByOrderByFdDstCdAsc()).thenReturn(finalDestEntityList);
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("B","B-Bangpakong"));
		returnablePackingMasterDtoList.add(new CommonMultiSelectDropdownDto("F","F-Banpho"));
		response.setImporterCodeList(returnablePackingMasterDtoList);

		ReturnablePackingMasterImporterDTO returnablePackingMasterImporterDTO = returnablePackingMasterServiceImpl.fetchReturnablePackingMasterDetails("S-Samrong","301B-ARGENTINA","1B","TMT");
		assertThat(returnablePackingMasterImporterDTO).isNotNull();
	}

}
