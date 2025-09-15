package com.tpex.invoice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.PartMasterSearchRequestDto;
import com.tpex.invoice.dto.PartMasterSearchResponseDto;
import com.tpex.invoice.service.PartMasterService;
import com.tpex.util.ConstantUtils;

@ExtendWith(MockitoExtension.class)
class PartMasterControllerTest {

	private static String partNo = "091010K43000";
	private static String partName = "TOOL SET, STD L/JACK";
	private static String partType = "1";
	private static String inhouseShop = "1";
	private static String weight = "0.360";
	private static String cmpCode = "TMT";

	@InjectMocks
	private PartMasterController partMasterController;

	@Mock
	private PartMasterService partMasterService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSearchWithValidInput() throws IllegalAccessException, InvocationTargetException {
		// Prepare test data
		PartMasterSearchRequestDto requestDto = new PartMasterSearchRequestDto();
		requestDto.setPartNo(partNo);
		requestDto.setPartName(partName);
		requestDto.setCmpCd(cmpCode);
		requestDto.setPartType(partType);
		List<PartMasterSearchResponseDto> expectedResult = new ArrayList<>();

		expectedResult.add(new PartMasterSearchResponseDto(partNo, partName, partType, inhouseShop, weight));

		// Mock the service method
		when(partMasterService.search(requestDto)).thenReturn(expectedResult);

		// Perform the test
		ResponseEntity<List<PartMasterSearchResponseDto>> responseEntity = partMasterController.search(requestDto);

		// Assertions
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(expectedResult, responseEntity.getBody());
		// Verify that the service method was called with the correct arguments
		verify(partMasterService).search(requestDto);
	}

	@Test
	void testSearchWithFewInput() throws IllegalAccessException, InvocationTargetException {
		// Prepare test data
		PartMasterSearchRequestDto requestDto = new PartMasterSearchRequestDto();
		requestDto.setPartNo(partNo);
		requestDto.setCmpCd(cmpCode);
		requestDto.setPartType("");
		List<PartMasterSearchResponseDto> expectedResult = new ArrayList<>();

		expectedResult.add(new PartMasterSearchResponseDto(partNo, partName, partType, inhouseShop, weight));

		// Mock the service method
		when(partMasterService.search(requestDto)).thenReturn(expectedResult);

		// Perform the test
		ResponseEntity<List<PartMasterSearchResponseDto>> responseEntity = partMasterController.search(requestDto);

		// Assertions
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(expectedResult, responseEntity.getBody());
		// Verify that the service method was called with the correct arguments
		verify(partMasterService).search(requestDto);
	}

	@Test
	void testSearchWithInvalidInput() throws IllegalAccessException, InvocationTargetException {
		// Prepare test data
		PartMasterSearchRequestDto requestDto = new PartMasterSearchRequestDto();
		requestDto.setPartName(null);
		requestDto.setPartNo(null);
		requestDto.setPartType(null);

		// Perform the test and check for the expected exception
		InvalidInputParametersException exception = org.junit.jupiter.api.Assertions
				.assertThrows(InvalidInputParametersException.class, () -> partMasterController.search(requestDto));

		// Assertions
		assertEquals(ConstantUtils.ERR_IN_1056, exception.getMessage());

	}

	@Test
	void downloadControllerTest() throws Exception {

		HttpServletResponse reponse = mock(HttpServletResponse.class);
		String headerkey = "Content-Disposition";
		String headerValue = "attachment;filename=Part_Master.xlsx";
		reponse.setHeader(headerkey, headerValue);

		PartMasterSearchRequestDto partMasterSearchRequestDto = new PartMasterSearchRequestDto();

		partMasterService.download(reponse, partMasterSearchRequestDto);

		partMasterController.download(reponse, partMasterSearchRequestDto);

		Assertions.assertDoesNotThrow(() -> partMasterService.download(reponse, partMasterSearchRequestDto));
		Assertions.assertDoesNotThrow(() -> partMasterController.download(reponse, partMasterSearchRequestDto));
	}

}
