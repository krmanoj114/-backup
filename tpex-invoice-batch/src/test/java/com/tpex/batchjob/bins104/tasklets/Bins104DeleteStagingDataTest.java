package com.tpex.batchjob.bins104.tasklets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.Bins104CustomRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

@ExtendWith(MockitoExtension.class)
class Bins104DeleteStagingDataTest {
	

	
	@InjectMocks 
	private Bins104DeleteStagingData bins104DeleteStagingData;

	@Mock 
	private ProcessLogDtlsRepository processLogDtlsRepository;

	@Mock 
	private OemProcessCtrlRepository processCtrlRepository;

	@Mock 
	private ProcessLogDtlsUtil processLogDtlsUtil;

	@Mock
	Bins104CustomRepository bins104CustomRepository;
	
	@Mock private StepExecution stepExecution;
	@Mock private StepContribution contribution;
	@Mock private ChunkContext chunkContext;
	@Mock private JobExecution jobExecution;
	@Mock private ExecutionContext executionContext;
	@Mock private JobParameters jobParameters;
	
	void executeTest() throws Exception {
		
		ReflectionTestUtils.setField(bins104DeleteStagingData, "compCode", "TMT", String.class);
		
		bins104DeleteStagingData.execute(contribution, chunkContext);
		
		verify(bins104CustomRepository, times(1)).deleteStagingData(any());
		
	}
	
	@Test
	void beforeStepInsertLogTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.getString(GlobalConstants.COMP_CODE)).thenReturn("compCode");
		when(stepExecution.getStepName()).thenReturn("BINS104DeleteStaging");
		when(stepExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID)).thenReturn("999");
		when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID)).thenReturn("BINS104");
		when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_NAME)).thenReturn("UploadG");
		when(jobParameters.getString(ConstantUtils.JOB_P_USER_ID)).thenReturn("MockTest");
		when(executionContext.getString(GlobalConstants.IF_FILE_NM)).thenReturn("fileName");
		
		bins104DeleteStagingData.beforeStep(stepExecution);
		
		verify(processCtrlRepository, times(1)).save(any(OemProcessCtrlEntity.class));
	}
	
	@Test
	void beforeStepInsertLogNewCtrlIdTest() {
		ProcessLogDtlsEntity dtl = new ProcessLogDtlsEntity();
		dtl.setProcessControlId(998);
		
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.getString(GlobalConstants.COMP_CODE)).thenReturn("compCode");
		when(stepExecution.getStepName()).thenReturn("BINF005DeleteStaging");
		when(stepExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID)).thenReturn(null);
		when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID)).thenReturn("BINF005");
		when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_NAME)).thenReturn("UploadG");
		when(jobParameters.getString(ConstantUtils.JOB_P_USER_ID)).thenReturn("MockTest");
		when(processLogDtlsRepository.findTopByOrderByProcessControlIdDesc()).thenReturn(dtl);
		when(executionContext.getString(GlobalConstants.IF_FILE_NM)).thenReturn("fileName");
		
		bins104DeleteStagingData.beforeStep(stepExecution);
		
		verify(processLogDtlsRepository, times(1)).findTopByOrderByProcessControlIdDesc();
		verify(processCtrlRepository, times(1)).save(any(OemProcessCtrlEntity.class));
	}
	
	@Test
	void beforeStepAfterErrTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.getString(GlobalConstants.COMP_CODE)).thenReturn("compCode");
		when(stepExecution.getStepName()).thenReturn("BINS104DeleteStagingDataAfterError");
		
		bins104DeleteStagingData.beforeStep(stepExecution);
		
		verify(processCtrlRepository, never()).save(any(OemProcessCtrlEntity.class));
	}
	
	

	
	@Test
	void afterStepTest() {
		when(stepExecution.getStepName()).thenReturn("BINS104DeleteStagingData");
		
		ExitStatus afterStep = bins104DeleteStagingData.afterStep(stepExecution);
		assertEquals(ExitStatus.COMPLETED, afterStep);
	}
	
	@Test
	void afterStepAfterErrTest() {
		when(stepExecution.getStepName()).thenReturn("BINS104DeleteStagingDataAfterError");
		
		ExitStatus afterStep = bins104DeleteStagingData.afterStep(stepExecution);
		assertEquals(ExitStatus.FAILED, afterStep);
	}

}
