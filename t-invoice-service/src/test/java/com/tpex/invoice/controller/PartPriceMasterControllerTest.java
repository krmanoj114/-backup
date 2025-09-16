package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.dto.FinalDestinationAndCarFamilyCodesDTO;
import com.tpex.dto.PartPriceMasterDeleteRequestDto;
import com.tpex.dto.PartPriceMasterDto;
import com.tpex.dto.PartPriceMasterRequestDto;
import com.tpex.dto.PartPriceMasterResponseDto;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.service.PartPriceMasterService;

@ExtendWith(MockitoExtension.class)
class PartPriceMasterControllerTest {

	@InjectMocks
	private PartPriceMasterController partPriceMasterController;
	
	@Mock
	private PartPriceMasterService partPriceMasterService;
	
	private static String id = "481W303D202202174510M02000USD";
	private static String effectiveDate = "2022/02";
	private static String partName = "GASKET, EXHAUST PIPE";
	private static String partNo = "174510M02000";

	
	@Test
	void searchPartPriceDetailsTest() throws Exception {
		
		List<CommonMultiSelectDropdownDto> currencyList = new ArrayList<>();
		currencyList.add(new CommonMultiSelectDropdownDto("USD", "USD - US DOLLARS"));
		currencyList.add(new CommonMultiSelectDropdownDto("BHT", "BHT - THAI BAHT"));
		
		List<PartPriceMasterDto> partPriceMasterList = new ArrayList<>();
		partPriceMasterList.add(new PartPriceMasterDto(id, "481W", "303D", effectiveDate, effectiveDate, partNo, partName, BigDecimal.valueOf(2.55), "USD"));
		
		PartPriceMasterResponseDto partPriceMasterResponseDto = new PartPriceMasterResponseDto();
		partPriceMasterResponseDto.setCurrencyList(currencyList);
		partPriceMasterResponseDto.setPartPriceMasterList(partPriceMasterList);
		
		when(partPriceMasterService.partPriceMasterList(any())).thenReturn(partPriceMasterResponseDto);
		
		ResponseEntity<PartPriceMasterResponseDto> result = partPriceMasterController.searchPartPriceDetails(new PartPriceMasterRequestDto("481W", "303D", partNo, effectiveDate, "TMT"));
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

	}
	
	@Test
	void destinationAndCarfamilyCodesTest() throws Exception {
		
		List<CommonMultiSelectDropdownDto> carFamilyList = new ArrayList<>();
		carFamilyList.add(new CommonMultiSelectDropdownDto("053L", "053L-CAMRY"));
		carFamilyList.add(new CommonMultiSelectDropdownDto("060B", "060B-EFC"));
		
		List<CommonMultiSelectDropdownDto> destinationList = new ArrayList<>();
		destinationList.add(new CommonMultiSelectDropdownDto("0000", "0000-CHINA"));
		destinationList.add(new CommonMultiSelectDropdownDto("0001", "0001-GERMANY"));
		
		when(partPriceMasterService.destinationAndCarfamilyCodes(any())).thenReturn(new FinalDestinationAndCarFamilyCodesDTO(carFamilyList, destinationList));
		
		ResponseEntity<FinalDestinationAndCarFamilyCodesDTO> result = partPriceMasterController.destinationAndCarfamilyCodes("TMT");
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

	}
	
	@Test
	void downloadPartPriceMasterDetailsOnlineTest() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("outStream", new byte[1]);
		map.put("fileName", "PxP Price_481W_303D");
		map.put("status", "online");
		
		when(partPriceMasterService.downloadPartPriceMasterDetails(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(map);
	
		ResponseEntity<Object> result = partPriceMasterController.downloadPartPriceMasterDetails("481W", "303D", effectiveDate, partNo, "USD");
		
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
	}
	
	@Test
	void downloadPartPriceMasterDetailsOfflineTest() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("message", "INFO_IN_1004");
		map.put("status", "offline");
		
		when(partPriceMasterService.downloadPartPriceMasterDetails(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(map);
	
		ResponseEntity<Object> result = partPriceMasterController.downloadPartPriceMasterDetails("481W", "303D", effectiveDate, partNo, "USD");
		
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	void downloadPartPriceMasterDetailsExceptionsTest() throws Exception {
		assertThrows(MyResourceNotFoundException.class, () -> partPriceMasterController.downloadPartPriceMasterDetails("481W", "303D", "20200002", partNo, "USD"));

		when(partPriceMasterService.downloadPartPriceMasterDetails(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
		assertThrows(MyResourceNotFoundException.class, () -> partPriceMasterController.downloadPartPriceMasterDetails("481W", "303D", effectiveDate, partNo, "USD"));
	}
	
	@Test
	void deletePartPriceMasterTest() throws ParseException {
		List<PartPriceMasterDto> partPriceMasterDeleteRequestDtoList = new ArrayList<>();
		PartPriceMasterDto partPriceMasterDto = new PartPriceMasterDto();
		partPriceMasterDto.setImporterCode("303D");
		partPriceMasterDto.setCarFamilyCode("481W");
		partPriceMasterDto.setEffectiveFromMonth("2023/08");
		partPriceMasterDto.setPartNo("17451-0M020-04");
		partPriceMasterDeleteRequestDtoList.add(partPriceMasterDto);
		
		Mockito.when(partPriceMasterService.deletePartPriceMasterDetails(anyList())).thenReturn(new ArrayList<PartPriceMasterDto>());
		
		PartPriceMasterDeleteRequestDto partPriceMasterDeleteRequestDto = new PartPriceMasterDeleteRequestDto();
		partPriceMasterDeleteRequestDto.setData(partPriceMasterDeleteRequestDtoList);
		
		ResponseEntity<ApiResponseMessage> result = partPriceMasterController.deletePartPriceMaster(partPriceMasterDeleteRequestDto);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

	}
	
	@Test
	void saveShippingControlMasterTest() throws ParseException {
		//Test success scenario
		when(partPriceMasterService.savePxpPartPriceMaster(any())).thenReturn(true);
		
		List<PartPriceMasterDto> partPriceMasterDtos = new ArrayList<>();
		partPriceMasterDtos.add(new PartPriceMasterDto(id, "481W", "303D", effectiveDate, effectiveDate, partNo, partName, BigDecimal.valueOf(2.55), "USD"));
		PartPriceMasterDeleteRequestDto partPriceMasterDeleteRequestDto = new PartPriceMasterDeleteRequestDto("TestUser", partPriceMasterDtos);
		
		ResponseEntity<ApiResponseMessage> result = partPriceMasterController.savePxpPartPriceMaster(partPriceMasterDeleteRequestDto);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		
		when(partPriceMasterService.savePxpPartPriceMaster(any())).thenReturn(false);
		Assertions.assertThrows(MyResourceNotFoundException.class,
				() -> partPriceMasterController.savePxpPartPriceMaster(partPriceMasterDeleteRequestDto));
	}
	
	@Test
	void updateShippingControlMasterTest() throws ParseException {
		//Test success scenario
		when(partPriceMasterService.updatePxpPartPriceMaster(any())).thenReturn(true);
		
		List<PartPriceMasterDto> partPriceMasterDtos = new ArrayList<>();
		partPriceMasterDtos.add(new PartPriceMasterDto(id, "481W", "303D", effectiveDate, effectiveDate, partNo, partName, BigDecimal.valueOf(2.55), "USD"));
		PartPriceMasterDeleteRequestDto partPriceMasterDeleteRequestDto = new PartPriceMasterDeleteRequestDto("TestUser", partPriceMasterDtos);
		
		ResponseEntity<ApiResponseMessage> result = partPriceMasterController.updatePxpPartPriceMaster(partPriceMasterDeleteRequestDto);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		
		when(partPriceMasterService.updatePxpPartPriceMaster(any())).thenReturn(false);
		Assertions.assertThrows(MyResourceNotFoundException.class,
				() -> partPriceMasterController.updatePxpPartPriceMaster(partPriceMasterDeleteRequestDto));
	}
	
	
	@Test
	void partNameByPartNoTest() {
		when(partPriceMasterService.partNameByPartNo(any())).thenReturn(partName);
		ResponseEntity<String> result = partPriceMasterController.partNameByPartNo("17451-0M020-00");
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(partName, result.getBody());
	}
}
