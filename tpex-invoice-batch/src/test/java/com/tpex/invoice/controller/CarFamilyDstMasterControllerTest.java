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

import com.tpex.controller.CarFamilyDstMasterController;
import com.tpex.dto.UploadAddressMasterJobDto;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.service.CarFamilyDstMasterService;

@ExtendWith(MockitoExtension.class)
class CarFamilyDstMasterControllerTest {

	@InjectMocks
	CarFamilyDstMasterController carFamilyDstMasterController;
	
	@Mock
	CarFamilyDstMasterService carFamilyDstMasterService;
	
	@Test
	void jobAddressMasterTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("User ID");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));

		Mockito.doNothing().when(carFamilyDstMasterService).carFamilyDstMasterUploadBatchJob(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		UploadAddressMasterJobDto uploadAddressMasterJobDto = new UploadAddressMasterJobDto();
		uploadAddressMasterJobDto.setBatchName("BINF126");
		uploadAddressMasterJobDto.setFileName("carfamilydstinputfile");
		uploadAddressMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadAddressMasterJobDto.setCompanyCode("TMT");
		uploadAddressMasterJobDto.setUserId("User ID");
		
		ResponseEntity<String> result = carFamilyDstMasterController.uplaodCarFamilyDstMaster(uploadAddressMasterJobDto);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
}
