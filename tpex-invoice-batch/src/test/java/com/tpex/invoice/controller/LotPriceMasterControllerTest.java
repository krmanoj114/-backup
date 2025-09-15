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

import com.tpex.controller.LotPriceMasterController;
import com.tpex.dto.UploadLotPartPriceMasterJobDto;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.service.LotPriceMasterService;

@ExtendWith(MockitoExtension.class)
class LotPriceMasterControllerTest {

	@InjectMocks
	LotPriceMasterController lotPriceMasterController;
	
	@Mock
	LotPriceMasterService lotPriceMasterService;
	
	@Test
	void jobLotPartPriceTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("User ID");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));

		Mockito.doNothing().when(lotPriceMasterService).lotPartPriceUploadBatchJob(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		UploadLotPartPriceMasterJobDto uploadLotPartPriceMasterJobDto = new UploadLotPartPriceMasterJobDto();
		uploadLotPartPriceMasterJobDto.setBatchName("BINF106");
		uploadLotPartPriceMasterJobDto.setFileName("lotinputfile");
		uploadLotPartPriceMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadLotPartPriceMasterJobDto.setEffectiveDate("2023/01/01");
		uploadLotPartPriceMasterJobDto.setUserId("User ID");
		
		ResponseEntity<String> result = lotPriceMasterController.uplaodLotPartPriceMaster(uploadLotPartPriceMasterJobDto);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
}
