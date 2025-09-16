package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.controller.InvGenWorkPlanMstController;
import com.tpex.dto.UploadWrkPlanMasterFromVesselBookingRequest;
import com.tpex.dto.UploadwrkPlanMasterJobDto;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.service.InvGenWorkPlanMstService;

@ExtendWith(MockitoExtension.class)
class InvGenWorkPlanMstControllerTest {
	
	@InjectMocks
	InvGenWorkPlanMstController invGenWorkPlanMstController;
	
	@Mock
	InvGenWorkPlanMstService invGenWorkPlanMstService;

	@Test
	void jobWorkPlanMasterTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("TestUser");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));

		Mockito.doNothing().when(invGenWorkPlanMstService).wrkPlanMasterUploadBatchJob(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

		UploadwrkPlanMasterJobDto uploadwrkPlanMasterJobDto = new UploadwrkPlanMasterJobDto();
		uploadwrkPlanMasterJobDto.setBatchName("WPMSTUPLOAD");
		uploadwrkPlanMasterJobDto.setFileName("workPlanMasterinputfile");
		uploadwrkPlanMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadwrkPlanMasterJobDto.setInvoiceDateFrom("29/05/2023");
		uploadwrkPlanMasterJobDto.setUserId("TestUser");
		
		ResponseEntity<String> result = invGenWorkPlanMstController.uplaodWrkPlanMaster(uploadwrkPlanMasterJobDto);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
	
	@Test
	void uplaodWrkPlanMasterFromVesselBooking() {
		UploadWrkPlanMasterFromVesselBookingRequest req = new UploadWrkPlanMasterFromVesselBookingRequest();
		req.setUserId("TESTUSER");
		req.setVanningMonth("2022/09");
		
		ResponseEntity<ApiResponseMessage> res = invGenWorkPlanMstController.uplaodWrkPlanMasterFromVesselBooking(req);
		
		assertNotNull(res);
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(res.getBody()).isNotNull();
		verify(invGenWorkPlanMstService, times(1)).wrkPlanMasterUploadFromVesselBookingBatchJob(Mockito.any(UploadWrkPlanMasterFromVesselBookingRequest.class));
	}


}
