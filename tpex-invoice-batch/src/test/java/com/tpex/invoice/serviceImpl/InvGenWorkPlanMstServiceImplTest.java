package com.tpex.invoice.serviceImpl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;

import com.tpex.dto.UploadWrkPlanMasterFromVesselBookingRequest;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.serviceimpl.InvGenWorkPlanMstServiceImpl;

@ExtendWith(MockitoExtension.class)
class InvGenWorkPlanMstServiceImplTest {

	@InjectMocks
	InvGenWorkPlanMstServiceImpl invGenWorkPlanMstServiceImpl;

	@Mock
	JobLauncher jobLauncher;
	
	@Mock
	ProcessLogDtlsRepository processLogDtlsRepository;
	
	@Mock
	@Qualifier("wrkPlanMasterUploadFromVesselBookingBatchJob")
	private Job wrkPlanMasterUploadFromVesselBookingBatchJob;
	
	@Test
	void jobWorkPlanMasterTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("TestUser");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));
		jobLauncher.run(Mockito.any(), Mockito.any());
        Assertions.assertDoesNotThrow(() -> invGenWorkPlanMstServiceImpl.wrkPlanMasterUploadBatchJob("WPMSTUPLOAD", "workPlanMasterinputfile", oemProcessCtrlEntity, "29/05/2023", "TestUser"));

	}
	
	@Test
	void wrkPlanMasterUploadFromVesselBookingBatchJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		UploadWrkPlanMasterFromVesselBookingRequest req = new UploadWrkPlanMasterFromVesselBookingRequest();
		req.setVanningMonth("2023/04");
		req.setUserId("USERTEST");
		ProcessLogDtlsEntity logDtl = new ProcessLogDtlsEntity();
		logDtl.setProcessControlId(999);
		
		when(processLogDtlsRepository.findTopByOrderByProcessControlIdDesc()).thenReturn(logDtl);
		
		invGenWorkPlanMstServiceImpl.wrkPlanMasterUploadFromVesselBookingBatchJob(req);
		verify(jobLauncher, times(1)).run(Mockito.any(Job.class), Mockito.any(JobParameters.class));
	}


}
