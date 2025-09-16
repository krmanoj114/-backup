package com.tpex.batchjob.binf009.tasklets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
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

import com.tpex.repository.Binf009CustomRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

@ExtendWith(MockitoExtension.class)
class Binf009ProcessMainTest {

	@InjectMocks private Binf009ProcessMain binf009ProcessMain;
	@Mock private Binf009CustomRepository binf009CustomRepository;
	@Mock private ProcessLogDtlsUtil processLogDtlsUtil;

	@Mock private StepExecution stepExecution;
	@Mock private JobExecution jobExecution;
	@Mock private ExecutionContext executionContext;
	@Mock private JobParameters jobParameters;
	@Mock private StepContribution contribution;
	@Mock private ChunkContext chunkContext;

	@Test
	void beforeStepTest() {
		assertDoesNotThrow(() -> binf009ProcessMain.beforeStep(stepExecution));
	}

	@Test
	void afterStepTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

		ExitStatus afterStep = binf009ProcessMain.afterStep(stepExecution);
		assertEquals(ExitStatus.COMPLETED, afterStep);
	}

	@ParameterizedTest
	@MethodSource("executeParamTest")
	void executeTest(Boolean isBusErr) throws Exception {
		lenient().when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		lenient().when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		lenient().when(executionContext.get(GlobalConstants.EXECUTE_DATETIME)).thenReturn("2023-07-17 17:17:17");
		lenient().when(executionContext.get(GlobalConstants.COMP_CODE)).thenReturn("compCode");
		lenient().when(stepExecution.getJobParameters()).thenReturn(jobParameters);
		lenient().when(executionContext.getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID)).thenReturn(99);
		lenient().when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID)).thenReturn("BINF009");
		lenient().when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_NAME)).thenReturn("UploadG");
		lenient().when(processLogDtlsUtil.getCurrentSpanId()).thenReturn("A88V");
		lenient().when(executionContext.get(GlobalConstants.IS_BUS_ERROR)).thenReturn(isBusErr);

		binf009ProcessMain.execute(contribution, chunkContext);

		verify(binf009CustomRepository, times(1)).deletePkgSpecData(anyString());
		verify(binf009CustomRepository, times(1)).insertPkgSpecData();
	}

	private static Stream<Arguments> executeParamTest() {
		return Stream.of(Arguments.of(true)
				, Arguments.of(false));
	}

}
