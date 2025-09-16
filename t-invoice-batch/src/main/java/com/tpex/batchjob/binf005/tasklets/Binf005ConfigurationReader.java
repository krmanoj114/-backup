package com.tpex.batchjob.binf005.tasklets;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.tpex.batchjob.binf005.configuration.model.Binf005Config;
import com.tpex.batchjob.common.configuration.model.LineConfig;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ConstantUtils.Binf005;

public class Binf005ConfigurationReader implements Tasklet, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(Binf005ConfigurationReader.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
    	
    	//Get Configuration information.
    	Binf005Config config = (Binf005Config) stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_CONFIG);

    	//Put information of each line in Map
        Map <String, LineConfig> lineInfo = new HashMap <> ();

        if(config != null) {
        
	        lineInfo.put(Binf005.GATEWAY_TAG, config.getFileStructure().getGatewayHeader());
	
	    	lineInfo.put(Binf005.HEAD, config.getFileStructure().getFileHeader());
	    	lineInfo.put(Binf005.FOOT, config.getFileStructure().getFileFooter());
	    	
	    	lineInfo.put(Binf005.H1, config.getFileStructure().getContainers().get(0).getContainerHeader());
	    	lineInfo.put(Binf005.D1, config.getFileStructure().getContainers().get(0).getContainerDetail());
	    	lineInfo.put(Binf005.T1, config.getFileStructure().getContainers().get(0).getContainerFooter());
	    	
	    	lineInfo.put(Binf005.H2, config.getFileStructure().getContainers().get(0).getContainerModules().get(0).getModuleHeader());
	    	lineInfo.put(Binf005.D2, config.getFileStructure().getContainers().get(0).getContainerModules().get(0).getModuleDetail());
	    	lineInfo.put(Binf005.T2, config.getFileStructure().getContainers().get(0).getContainerModules().get(0).getModuleFooter());
	
	    	lineInfo.put(Binf005.H3, config.getFileStructure().getContainers().get(0).getContainerModules().get(0).getModuleParts().get(0).getPartHeader());
	    	lineInfo.put(Binf005.D3, config.getFileStructure().getContainers().get(0).getContainerModules().get(0).getModuleParts().get(0).getPartDetail());
	    	lineInfo.put(Binf005.T3, config.getFileStructure().getContainers().get(0).getContainerModules().get(0).getModuleParts().get(0).getPartFooter());
	
	    	lineInfo.put(Binf005.H4, config.getFileStructure().getLot().getLotHeader());
	    	lineInfo.put(Binf005.D4, config.getFileStructure().getLot().getLotDetails().get(0));
	    	lineInfo.put(Binf005.T4, config.getFileStructure().getLot().getLotFooter());
    	
        }
    	//Set Configuration information in context.
    	stepExecution.getJobExecution().getExecutionContext().put(GlobalConstants.IF_LINES_INFO, lineInfo);

        logger.debug("Lines configuration prepared as :: {}", stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_LINES_INFO));
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
	
	
}
