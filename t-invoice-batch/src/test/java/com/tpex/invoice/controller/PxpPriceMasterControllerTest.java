package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.controller.PxpPriceMasterController;
import com.tpex.dto.UploadPxpPriceMasterJobDto;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.service.PxpPriceMasterService;

@ExtendWith(MockitoExtension.class)
class PxpPriceMasterControllerTest {

	@InjectMocks
	PxpPriceMasterController pxpPriceMasterController;
	
	@Mock
	PxpPriceMasterService pxpPriceMasterService;
	
	@Test
	void jobPxpPriceMasterTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("User ID");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));

		Mockito.doNothing().when(pxpPriceMasterService).pxpPriceMasterUploadBatchJob(Mockito.any());

		UploadPxpPriceMasterJobDto uploadPxpPriceMasterJobDto = new UploadPxpPriceMasterJobDto();
		uploadPxpPriceMasterJobDto.setBatchName("BINF107");
		uploadPxpPriceMasterJobDto.setEffectiveFromDate("2022/06");
		uploadPxpPriceMasterJobDto.setEffectiveToDate("2022/07");
		uploadPxpPriceMasterJobDto.setFileName("pxppriceinputfile");
		uploadPxpPriceMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadPxpPriceMasterJobDto.setCompanyCode("TMT");
		uploadPxpPriceMasterJobDto.setUserId("User ID");
		
		ResponseEntity<String> result = pxpPriceMasterController.uploadPxpPriceMaster(uploadPxpPriceMasterJobDto);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
}
