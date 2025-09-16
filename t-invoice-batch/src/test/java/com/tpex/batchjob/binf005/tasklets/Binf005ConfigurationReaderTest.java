package com.tpex.batchjob.binf005.tasklets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import com.tpex.batchjob.binf005.configuration.model.Binf005Config;
import com.tpex.batchjob.binf005.configuration.model.Binf005FileStructure;
import com.tpex.batchjob.binf005.configuration.model.Binf005FileStructureContainerBlock;
import com.tpex.batchjob.binf005.configuration.model.Binf005FileStructureContainerModuleBlock;
import com.tpex.batchjob.binf005.configuration.model.Binf005FileStructureLot;
import com.tpex.batchjob.binf005.configuration.model.Binf005FileStructureModulePartBlock;
import com.tpex.batchjob.common.configuration.model.LineConfig;

@ExtendWith(MockitoExtension.class)
class Binf005ConfigurationReaderTest {
	
	@InjectMocks private Binf005ConfigurationReader binf005ConfigurationReader;
	@Mock private StepExecution stepExecution;
	@Mock private StepContribution stepContribution;
	@Mock private ChunkContext chunkContext;
	@Mock private JobExecution jobExecution;
	@Mock private ExecutionContext executionContext;

	@Test
	void beforeStepTest() {
		List<Binf005FileStructureContainerBlock> listBlockD1 = new ArrayList<>();
		Binf005FileStructureContainerBlock blockD1 = new Binf005FileStructureContainerBlock();
		List<Binf005FileStructureContainerModuleBlock> listBlockD2 = new ArrayList<>();
		Binf005FileStructureContainerModuleBlock blockD2 = new Binf005FileStructureContainerModuleBlock();
		List<Binf005FileStructureModulePartBlock> listBlockD3 = new ArrayList<>();
		Binf005FileStructureModulePartBlock blockD3 = new Binf005FileStructureModulePartBlock();
		listBlockD3.add(blockD3);
		blockD2.setModuleParts(listBlockD3);
		listBlockD2.add(blockD2);
		blockD1.setContainerModules(listBlockD2);
		listBlockD1.add(blockD1);
		List<LineConfig> listConfigLot = new ArrayList<>();
		listConfigLot.add(new LineConfig());
		Binf005FileStructureLot lot = new Binf005FileStructureLot();
		lot.setLotDetails(listConfigLot);
		Binf005Config conf = new Binf005Config();
		Binf005FileStructure fileStruct = new Binf005FileStructure();
		fileStruct.setContainers(listBlockD1);
		fileStruct.setLot(lot);
		conf.setFileStructure(fileStruct);
		
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.get(anyString())).thenReturn(conf);
		
		assertDoesNotThrow(() -> binf005ConfigurationReader.beforeStep(stepExecution));
	}
	
	@Test
	void beforeStepConfigNullTest() {
		when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		when(jobExecution.getExecutionContext()).thenReturn(executionContext);
		when(executionContext.get(anyString())).thenReturn(null);
		
		assertDoesNotThrow(() -> binf005ConfigurationReader.beforeStep(stepExecution));
	}
	
	@Test
	void executeTest() throws Exception {
		assertDoesNotThrow(() -> binf005ConfigurationReader.execute(stepContribution, chunkContext));
	}
	
	@Test
	void afterStepTest() {
		assertDoesNotThrow(() -> binf005ConfigurationReader.afterStep(stepExecution));
	}

}
