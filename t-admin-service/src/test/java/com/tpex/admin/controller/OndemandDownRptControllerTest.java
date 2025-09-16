package com.tpex.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.admin.service.OndemandDownRptServiceImpl;
import com.tpex.admin.dto.RddDownLocDtlDto;
import com.tpex.admin.dto.ReportStatusInformation;
import com.tpex.admin.entity.RddDownLocDtlEntity;
import com.tpex.admin.module.OndemandDownRptModule;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class OndemandDownRptControllerTest {

	@InjectMocks
	OndemandDownRptController ondemandDownRptController;

	@Mock
	OndemandDownRptServiceImpl ondemandDownRptService;
	
	@Test
	void searchOnDemandReportsTest() throws Exception {
		OndemandDownRptModule request = new OndemandDownRptModule();
		List<RddDownLocDtlEntity> list = new ArrayList<>();
		
		when(ondemandDownRptService.searchOnDemandReports(any(OndemandDownRptModule.class))).thenReturn(list);
		
		ResponseEntity<List<RddDownLocDtlDto>> result = ondemandDownRptController.searchOnDemandReports(request);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
	
	@Test
	void fetchReportAndStatusTest() throws Exception {
		ReportStatusInformation list = new ReportStatusInformation();
		
		when(ondemandDownRptService.fetchReportAndStatus(any(ReportStatusInformation.class))).thenReturn(list);
		
		ResponseEntity<ReportStatusInformation> result = ondemandDownRptController.fetchReportAndStatus();
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
	
	//@Test
	void downloadTest() throws Exception {
		ResponseEntity<InputStreamResource> result = ondemandDownRptController.download("C:/TPEXConfig/InvoiceGeneration/reportName.json");
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getHeaders().getContentDisposition().getFilename()).isEqualTo("reportName.json");
		assertThat(result.getHeaders().getContentDisposition().getType()).isEqualTo("attachment");
	}


}
