package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.invoice.dto.MixPrivilegeDetailsDto;
import com.tpex.invoice.dto.MixPrivilegeDetailsListResponseDto;
import com.tpex.invoice.dto.MixPrivilegeMasterSaveRequestDto;
import com.tpex.invoice.dto.PriorityDto;
import com.tpex.invoice.dto.ReExporterCodeDto;
import com.tpex.invoice.serviceimpl.MixPrivilegeMasterServiceImpl;


@ExtendWith(MockitoExtension.class)
class MixPrivilegeMasterControllerTest {

	@InjectMocks
	private MixPrivilegeMasterController mixPrivilegeMasterController;

	@Mock
	private MixPrivilegeMasterServiceImpl mixPrivilegeMasterServiceImpl;

	@Test
	void fetchMixPrivilegeDetails() {

		MixPrivilegeDetailsListResponseDto response = new MixPrivilegeDetailsListResponseDto();

		List<MixPrivilegeDetailsDto> privList = new ArrayList<>();

		MixPrivilegeDetailsDto dto = new MixPrivilegeDetailsDto();
		dto.setEffFrom("2016-09-01");
		dto.setEffTo("2017-07-31");
		dto.setReExporterCode("1");
		dto.setPriorityOne(Stream.of("ASEAN-FTA-U,NON-1".split(",")).collect(Collectors.toList()));
		dto.setPriorityTwo(Stream.of("FTA-X,NON-1".split(",")).collect(Collectors.toList()));
		dto.setPriorityThree(Stream.of("NON-1,AICO-2".split(",")).collect(Collectors.toList()));
		dto.setPriorityFour(Stream.of("NON-4,ATIGA-3".split(",")).collect(Collectors.toList()));
		dto.setPriorityFive(Stream.of("ATIGA-T,NON-F".split(",")).collect(Collectors.toList()));
		privList.add(dto);
		response.setMixPrivilegeDetails(privList);

		List<ReExporterCodeDto> reExporterCodeDto = new ArrayList<>();
		reExporterCodeDto.add(new ReExporterCodeDto("1","IMV4"));
		reExporterCodeDto.add(new ReExporterCodeDto("4","IMV4"));
		response.setReExporterCode(reExporterCodeDto);

		List<PriorityDto> priority = new ArrayList<>();
		priority.add(new PriorityDto("1","NON"));
		priority.add(new PriorityDto("2","AICO"));
		response.setPriority(priority);

		when(mixPrivilegeMasterServiceImpl.fetchMixPrivilegeDetails(anyString(), anyString())).thenReturn(response);


		ResponseEntity<MixPrivilegeDetailsListResponseDto> result = mixPrivilegeMasterController.fetchMixPrivilegeDetails("578W","709B");

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertEquals(response, result.getBody());
	}

	@Test
	void deleteMixPrivilegeMaster() throws Exception {

		Mockito.lenient().doNothing().when(mixPrivilegeMasterServiceImpl).deleteMixPrivilegeMaster(anyList());

		List<Integer> mixPrivilegeMstIdList = new ArrayList<>();
		mixPrivilegeMstIdList.add(201);

		ResponseEntity<Object> result = mixPrivilegeMasterController.deleteMixPrivilegeMaster(mixPrivilegeMstIdList);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();


	}

	@Test
	void saveMixPrivilegeMaster() throws Exception {

		when(mixPrivilegeMasterServiceImpl.saveMixPrivilegeMaster(anyList(), anyString())).thenReturn(Boolean.TRUE);

		List<MixPrivilegeMasterSaveRequestDto> mixPrivilegeMasterSaveRequestDtoList = new ArrayList<>();
		MixPrivilegeMasterSaveRequestDto mixPrivilegeMasterSaveRequestDto = new MixPrivilegeMasterSaveRequestDto();

		List<String> priorList = new ArrayList<>();
		priorList.add("NON 1");
		priorList.add("ATIGA 3");

		mixPrivilegeMasterSaveRequestDto.setDestCode("715B");
		mixPrivilegeMasterSaveRequestDto.setCrFmlyCode("889W");
		mixPrivilegeMasterSaveRequestDto.setReExpCode("1");
		mixPrivilegeMasterSaveRequestDto.setEffFromDate("12/05/2023");
		mixPrivilegeMasterSaveRequestDto.setEffToDate("31/05/2023");
		mixPrivilegeMasterSaveRequestDto.setPriorityOne(priorList);
		mixPrivilegeMasterSaveRequestDto.setPriorityTwo(new ArrayList<>());
		mixPrivilegeMasterSaveRequestDto.setPriorityThree(new ArrayList<>());
		mixPrivilegeMasterSaveRequestDto.setPriorityFour(new ArrayList<>());
		mixPrivilegeMasterSaveRequestDto.setPriorityFive(new ArrayList<>());
		mixPrivilegeMasterSaveRequestDto.setCompanyCode("TMT");

		mixPrivilegeMasterSaveRequestDtoList.add(mixPrivilegeMasterSaveRequestDto);

		String userId = "TestUser";

		ResponseEntity<ApiResponseMessage> result = mixPrivilegeMasterController.saveMixPrivilegeMaster(mixPrivilegeMasterSaveRequestDtoList, userId);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();

		when(mixPrivilegeMasterServiceImpl.saveMixPrivilegeMaster(anyList(), anyString())).thenReturn(false);
		mixPrivilegeMasterSaveRequestDto.setPrivMstId(11111);
		mixPrivilegeMasterSaveRequestDtoList.add(mixPrivilegeMasterSaveRequestDto);
		result = mixPrivilegeMasterController.saveMixPrivilegeMaster(mixPrivilegeMasterSaveRequestDtoList,userId);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
