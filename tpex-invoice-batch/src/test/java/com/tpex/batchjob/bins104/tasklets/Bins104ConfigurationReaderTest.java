package com.tpex.batchjob.bins104.tasklets;

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

import com.tpex.batchjob.bins104.configuration.model.Bins104Config;
import com.tpex.batchjob.bins104.configuration.model.Bins104FileStructure;
import com.tpex.batchjob.common.configuration.model.LineConfig;

@ExtendWith(MockitoExtension.class)
class Bins104ConfigurationReaderTest {
	
	@InjectMocks private Bins104ConfigurationReader bins104ConfigurationReader;
	@Mock private StepExecution stepExecution;
	@Mock private StepContribution stepContribution;
	@Mock private ChunkContext chunkContext;
	@Mock private JobExecution jobExecution;
	@Mock private ExecutionContext executionContext;

	@Test
	void beforeStepTest() {	
		LineConfig packSpecDetails = new LineConfig();
		Bins104FileStructure fileStruct = new Bins104FileStructure();
		fileStruct.setFileRecord(packSpecDetails);
		Bins104Config conf = new Bins104Config();
				
		conf.setFileStructure(fileStruct);
		
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.get(anyString())).thenReturn(conf);
		
		assertDoesNotThrow(() -> bins104ConfigurationReader.beforeStep(stepExecution));
	}
	
	@Test
	void beforeStepConfigNullTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.get(anyString())).thenReturn(null);
		
		assertDoesNotThrow(() -> bins104ConfigurationReader.beforeStep(stepExecution));
	}
	
	@Test
	void executeTest() throws Exception {
		assertDoesNotThrow(() -> bins104ConfigurationReader.execute(stepContribution, chunkContext));
	}
	
	@Test
	void afterStepTest() {
		assertDoesNotThrow(() -> bins104ConfigurationReader.afterStep(stepExecution));
	}

}
