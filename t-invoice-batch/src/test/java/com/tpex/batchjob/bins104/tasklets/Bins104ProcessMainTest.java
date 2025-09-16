package com.tpex.batchjob.bins104.tasklets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
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

import com.tpex.entity.CoeCeptMasterEntity;
import com.tpex.entity.CoeCeptTempEntity;
import com.tpex.repository.Bins104CustomRepository;
import com.tpex.repository.CoeCeptMasterRepository;
import com.tpex.repository.CoeCeptTempRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

@ExtendWith(MockitoExtension.class)
class Bins104ProcessMainTest {

	@InjectMocks private Bins104ProcessMain bins104ProcessMain;
	@Mock private Bins104CustomRepository bins104CustomRepository;
	@Mock private CoeCeptMasterRepository coeCeptMasterRepository;
	@Mock private CoeCeptTempRepository coeCeptTempRepository;
	@Mock private CoeCeptTempEntity coeCeptTempEntity;
	@Mock private CoeCeptMasterEntity coeCeptMasterEntity;
	@Mock private ProcessLogDtlsUtil processLogDtlsUtil;

	@Mock private StepExecution stepExecution;
	@Mock private JobExecution jobExecution;
	@Mock private ExecutionContext executionContext;
	@Mock private JobParameters jobParameters;
	@Mock private StepContribution contribution;
	@Mock private ChunkContext chunkContext;

	@Test
	void beforeStepTest() {
		assertDoesNotThrow(() -> bins104ProcessMain.beforeStep(stepExecution));
	}

	@Test
	void afterStepTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

		ExitStatus afterStep = bins104ProcessMain.afterStep(stepExecution);
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
		lenient().when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID)).thenReturn("BINS104");
		lenient().when(jobParameters.getString(ConstantUtils.JOB_P_BATCH_NAME)).thenReturn("UploadG");
		lenient().when(processLogDtlsUtil.getCurrentSpanId()).thenReturn("A88V");
		lenient().when(executionContext.get(GlobalConstants.IS_BUS_ERROR)).thenReturn(isBusErr);

		bins104ProcessMain.execute(contribution, chunkContext);

		
		if (isBusErr) {
			verify(coeCeptMasterRepository, never()).saveAll(any());
			verify(coeCeptTempRepository, never()).findAll();
		} else {
			verify(bins104CustomRepository, times(1)).deletePkgSpecData(any());
			verify(coeCeptTempRepository, times(1)).findAll();
			verify(coeCeptMasterRepository, times(1)).saveAll(any());
		}
	}

	private static Stream<Arguments> executeParamTest() {
		return Stream.of(Arguments.of(true)
				, Arguments.of(false));
	}}
