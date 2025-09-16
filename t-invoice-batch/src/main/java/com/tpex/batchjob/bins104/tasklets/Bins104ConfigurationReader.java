package com.tpex.batchjob.bins104.tasklets;

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

import com.tpex.batchjob.bins104.configuration.model.Bins104Config;
import com.tpex.batchjob.common.configuration.model.LineConfig;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ConstantUtils.Bins104;

public class Bins104ConfigurationReader implements Tasklet, StepExecutionListener {
	
	private final Logger logger = LoggerFactory.getLogger(Bins104ConfigurationReader.class);
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		
		//Get Configuration information.
    	Bins104Config config = (Bins104Config) stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_CONFIG);

    	//Put information of each line in Map
        Map <String, LineConfig> lineInfo = new HashMap <> ();
        
        if(config != null) {
        	
        	lineInfo.put(Bins104.HEAD, config.getFileStructure().getFileHeader());
	    	lineInfo.put(Bins104.FOOT, config.getFileStructure().getFileFooter());
	    	lineInfo.put(Bins104.DATA, config.getFileStructure().getFileRecord());
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
