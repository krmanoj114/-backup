package com.tpex.batchjob.binf009.tasklets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.tpex.batchjob.binf009.configuration.model.Binf009Config;
import com.tpex.batchjob.binf009.configuration.model.Binf009FileStructure;
import com.tpex.batchjob.common.configuration.model.LineConfig;




@ExtendWith(MockitoExtension.class)
class Binf009ConfigurationReaderTest {

	@InjectMocks 
	private Binf009ConfigurationReader binf009ConfigurationReader;
	@Mock 
	private StepExecution stepExecution;
	@Mock 
	private StepContribution stepContribution;
	@Mock 
	private ChunkContext chunkContext;
	@Mock 
	private JobExecution jobExecution;
	@Mock 
	private ExecutionContext executionContext;

	@Test
	void beforeStepTest() {

		LineConfig packSpecDetails = new LineConfig();

		Binf009FileStructure fileStruct = new Binf009FileStructure();
		fileStruct.setPackSpecDetails(packSpecDetails);

		Binf009Config conf = new Binf009Config();
		conf.setFileStructure(fileStruct);

		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.get(anyString())).thenReturn(conf);

		assertDoesNotThrow(() -> binf009ConfigurationReader.beforeStep(stepExecution));
	}

	@Test
	void beforeStepConfigNullTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.get(anyString())).thenReturn(null);

		assertDoesNotThrow(() -> binf009ConfigurationReader.beforeStep(stepExecution));
	}

	@Test
	void executeTest() throws Exception {
		assertDoesNotThrow(() -> binf009ConfigurationReader.execute(stepContribution, chunkContext));
	}

	@Test
	void afterStepTest() {
		assertDoesNotThrow(() -> binf009ConfigurationReader.afterStep(stepExecution));
	}

}
