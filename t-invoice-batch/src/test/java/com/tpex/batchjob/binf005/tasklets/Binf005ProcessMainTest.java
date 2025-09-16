package com.tpex.batchjob.binf005.tasklets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

import com.tpex.repository.Binf005CustomRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

@ExtendWith(MockitoExtension.class)
class Binf005ProcessMainTest {
	
	@InjectMocks private Binf005ProcessMain binf005ProcessMain;
	@Mock private Binf005CustomRepository binf005CustomRepository;
	@Mock private ProcessLogDtlsUtil processLogDtlsUtil;
	
	@Mock private StepExecution stepExecution;
	@Mock private JobExecution jobExecution;
	@Mock private ExecutionContext executionContext;
	@Mock private JobParameters jobParameters;
	@Mock private StepContribution contribution;
	@Mock private ChunkContext chunkContext;
	
	@Test
	void beforeStepTest() {
		assertDoesNotThrow(() -> binf005ProcessMain.beforeStep(stepExecution));
	}
	
	@Test
	void afterStepTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);
		
		ExitStatus afterStep = binf005ProcessMain.afterStep(stepExecution);
		assertEquals(ExitStatus.COMPLETED, afterStep);
	}
	
	@ParameterizedTest
	@MethodSource("executeParamTest")
	void executeTest(boolean isProcErr, Boolean isBusErr) throws Exception {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.get(GlobalConstants.EXECUTE_DATETIME)).thenReturn("2023-07-17 17:17:17");
		when(executionContext.get(GlobalConstants.COMP_CODE)).thenReturn("compCode");
		when(stepExecution.getJobParameters()).thenReturn(jobParameters);
		when(executionContext.getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID)).thenReturn(99);
		when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID)).thenReturn("BINF005");
		when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_NAME)).thenReturn("UploadG");
		when(processLogDtlsUtil.getCurrentSpanId()).thenReturn("A88V");
		when(binf005CustomRepository.callBinf006A(any(), any(), any(), any(), any(), any())).thenReturn(isProcErr);
		when(executionContext.get(GlobalConstants.IS_BUS_ERROR)).thenReturn(isBusErr);
		
		binf005ProcessMain.execute(contribution, chunkContext);
		if(isProcErr || isBusErr) {
			verify(binf005CustomRepository, never()).processContTemp(any(), any(), any());
			verify(binf005CustomRepository, never()).processModuleTemp(any(), any(), any());
			verify(binf005CustomRepository, never()).processPartTemp(any(), any(), any());
			verify(binf005CustomRepository, never()).updInvAico(any(), any());
			verify(binf005CustomRepository, never()).processInvVin(any(), any(), any());
			verify(binf005CustomRepository, never()).recalContModWt(any(), any());
			verify(binf005CustomRepository, never()).updDlyPxPSrsNm(any(), any());
		} else {
			verify(binf005CustomRepository, times(1)).processContTemp(any(), any(), any());
			verify(binf005CustomRepository, times(1)).processModuleTemp(any(), any(), any());
			verify(binf005CustomRepository, times(1)).processPartTemp(any(), any(), any());
			verify(binf005CustomRepository, times(1)).updInvAico(any(), any());
			verify(binf005CustomRepository, times(1)).processInvVin(any(), any(), any());
			verify(binf005CustomRepository, times(1)).recalContModWt(any(), any());
			verify(binf005CustomRepository, times(1)).updDlyPxPSrsNm(any(), any());
		}
	}
	
	private static Stream<Arguments> executeParamTest() {
		return Stream.of(Arguments.of(true, true)
				, Arguments.of(true, null)
				, Arguments.of(false, false)
				, Arguments.of(false, true));
	}
}
