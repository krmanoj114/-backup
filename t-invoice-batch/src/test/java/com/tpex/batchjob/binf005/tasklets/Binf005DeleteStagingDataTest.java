package com.tpex.batchjob.binf005.tasklets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.VprContTempRepository;
import com.tpex.repository.VprDlyVinTempRepository;
import com.tpex.repository.VprModuleTempRepository;
import com.tpex.repository.VprPartTempRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

@ExtendWith(MockitoExtension.class)
class Binf005DeleteStagingDataTest {
	
	@InjectMocks private Binf005DeleteStagingData binf005DeleteStagingData;
	@Mock private VprContTempRepository vprContTempRepository;
	@Mock private VprModuleTempRepository vprModuleTempRepository;
	@Mock private VprPartTempRepository vprPartTempRepository;
	@Mock private VprDlyVinTempRepository vprDlyVinTempRepository;
	@Mock private OemProcessCtrlRepository processCtrlRepository;
	@Mock private ProcessLogDtlsRepository processLogDtlsRepository;
	@Mock private ProcessLogDtlsUtil processLogDtlsUtil;
	
	@Mock private StepExecution stepExecution;
	@Mock private StepContribution contribution;
	@Mock private ChunkContext chunkContext;
	@Mock private JobExecution jobExecution;
	@Mock private ExecutionContext executionContext;
	@Mock private JobParameters jobParameters;

	@Test
	void executeTest() throws Exception {
		ReflectionTestUtils.setField(binf005DeleteStagingData, "compCode", "TMT", String.class);
		
		binf005DeleteStagingData.execute(contribution, chunkContext);
		
		verify(vprContTempRepository, times(1)).deleteByIntrIdAndCompCode(eq(GlobalConstants.BINF005_INTR_ID), anyString());
		verify(vprModuleTempRepository, times(1)).deleteByIntrIdAndCompCode(eq(GlobalConstants.BINF005_INTR_ID), anyString());
		verify(vprPartTempRepository, times(1)).deleteByIntrIdAndCompCode(eq(GlobalConstants.BINF005_INTR_ID), anyString());
		verify(vprDlyVinTempRepository, times(1)).deleteByIntrIdAndCompCode(eq(GlobalConstants.BINF005_INTR_ID), anyString());
	}
	
	@Test
	void beforeStepInsertLogTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.getString(GlobalConstants.COMP_CODE)).thenReturn("compCode");
		when(stepExecution.getStepName()).thenReturn("BINF005DeleteStaging");
		when(stepExecution.getJobParameters()).thenReturn(jobParameters);
		when(jobParameters.getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID)).thenReturn("999");
		when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID)).thenReturn("BINF005");
		when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_NAME)).thenReturn("UploadG");
		when(jobParameters.getString(ConstantUtils.JOB_P_USER_ID)).thenReturn("MockTest");
		when(executionContext.getString(GlobalConstants.IF_FILE_NM)).thenReturn("fileName");
		
		binf005DeleteStagingData.beforeStep(stepExecution);
		
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
		
		binf005DeleteStagingData.beforeStep(stepExecution);
		
		verify(processLogDtlsRepository, times(1)).findTopByOrderByProcessControlIdDesc();
		verify(processCtrlRepository, times(1)).save(any(OemProcessCtrlEntity.class));
	}
	
	@Test
	void beforeStepAfterErrTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.getString(GlobalConstants.COMP_CODE)).thenReturn("compCode");
		when(stepExecution.getStepName()).thenReturn("BINF005DeleteStagingDataAfterError");
		
		binf005DeleteStagingData.beforeStep(stepExecution);
		
		verify(processCtrlRepository, never()).save(any(OemProcessCtrlEntity.class));
	}
	
	@Test
	void afterStepTest() {
		when(stepExecution.getStepName()).thenReturn("BINF005DeleteStagingData");
		
		ExitStatus afterStep = binf005DeleteStagingData.afterStep(stepExecution);
		assertEquals(ExitStatus.COMPLETED, afterStep);
	}
	
	@Test
	void afterStepAfterErrTest() {
		when(stepExecution.getStepName()).thenReturn("BINF005DeleteStagingDataAfterError");
		
		ExitStatus afterStep = binf005DeleteStagingData.afterStep(stepExecution);
		assertEquals(ExitStatus.FAILED, afterStep);
	}

}
