package com.tpex.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.tpex.admin.service.CommonProcessControlServiceImpl;
import com.tpex.admin.dto.CPCProcessLogsRequestDTO;
import com.tpex.admin.dto.CPCProcessLogsResponseDto;
import com.tpex.admin.dto.CPCProcessNamesRequestDTO;
import com.tpex.admin.dto.CPCProcessStatusRequestDto;
import com.tpex.admin.dto.CPCProcessStatusResponseDto;
import com.tpex.admin.dto.CPCSystemNamesResponseDto;


@ExtendWith(MockitoExtension.class)
class CommonProcessControlControllerTest {

	@Mock
	private CommonProcessControlServiceImpl commonProcessControlServiceImpl;

	@InjectMocks
	private CommonProcessControlController commonProcessControlController;

	private static String processId = "D00001";

	@Test
	void systemNamesTest() throws Exception {
		List<CPCSystemNamesResponseDto> list = new ArrayList<>();

		when(commonProcessControlServiceImpl.systemNames(anyString())).thenReturn(list);

		CPCProcessNamesRequestDTO cpcProcessNamesRequestDTO = new CPCProcessNamesRequestDTO();
		cpcProcessNamesRequestDTO.setUserId("ROEM");
		ResponseEntity<List<CPCSystemNamesResponseDto>> result = commonProcessControlController
				.systemNames(cpcProcessNamesRequestDTO);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

	}

	@Test
	void processStatusTest() throws Exception {
		CPCProcessStatusResponseDto processStatusResponseDto = new CPCProcessStatusResponseDto();

		when(commonProcessControlServiceImpl.processStatus(any())).thenReturn(processStatusResponseDto);

		CPCProcessStatusRequestDto processStatusRequestDto = new CPCProcessStatusRequestDto();
		processStatusRequestDto.setFromDateTime("09/03/2023_00:00");
		processStatusRequestDto.setEndDateTime("10/03/2023_00:00");
		processStatusRequestDto.setUserId("ROEM");
		ResponseEntity<CPCProcessStatusResponseDto> result = commonProcessControlController
				.processStatus(processStatusRequestDto);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

		processStatusRequestDto.setProcessId(processId);
		processStatusRequestDto.setSystemName("TPEX");
		ResponseEntity<CPCProcessStatusResponseDto> result1 = commonProcessControlController
				.processStatus(processStatusRequestDto);

		assertThat(result1.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result1.getBody()).isNotNull();

	}

	@Test
	void processLogsTest() throws Exception {
		List<CPCProcessLogsResponseDto> processLogsResponseDto = new ArrayList<>();

		when(commonProcessControlServiceImpl.processLogs(anyString(), anyString()))
				.thenReturn(processLogsResponseDto);

		CPCProcessLogsRequestDTO processLogsRequestDto = new CPCProcessLogsRequestDTO();
		processLogsRequestDto.setProcessControlId("1");
		processLogsRequestDto.setProcessId(processId);
		ResponseEntity<List<CPCProcessLogsResponseDto>> result = commonProcessControlController
				.processLogs(processLogsRequestDto);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

	}

	@Test
	void downloadProcessLogsTest() throws Exception {

		HashMap<String, Object> map = new HashMap<>();
		map.put("outStream", new byte[1]);
		map.put("fileName", "Process Log Details_15032023_211550");

		when(commonProcessControlServiceImpl.downloadProcessLogs(anyString(), anyString()))
				.thenReturn(map);

		ResponseEntity<?> result = commonProcessControlController.downloadProcessLogs("1", processId);

		if(result!=null) {
			assertEquals(HttpStatus.OK, result.getStatusCode());
			assertTrue(result.getBody() instanceof byte[]);
			assertEquals("Process Log Details_15032023_211550", result.getHeaders().get("file_name").get(0));
			assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
		}
	}
}
