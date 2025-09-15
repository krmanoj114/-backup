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

import com.tpex.controller.PartMasterController;
import com.tpex.dto.UploadPartMasterJobDto;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.service.PartMasterService;

@ExtendWith(MockitoExtension.class)
class PartMasterControllerTest {
	
	@InjectMocks
	PartMasterController partMasterController;
	
	@Mock
	PartMasterService partMasterService;

	@Test
	void jobPartMasterTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("User ID");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));

		Mockito.doNothing().when(partMasterService).partMasterUploadJob(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		UploadPartMasterJobDto uploadPartMasterJobDto = new UploadPartMasterJobDto();
		uploadPartMasterJobDto.setBatchName("BINS123");
		uploadPartMasterJobDto.setFileName("partmasterinputfile");
		uploadPartMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadPartMasterJobDto.setCompanyCode("TMT");
		uploadPartMasterJobDto.setUserId("User ID");
		
		ResponseEntity<String> result = partMasterController.uploadPartMaster(uploadPartMasterJobDto);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
}
