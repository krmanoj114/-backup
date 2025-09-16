package com.tpex.batchjob.psins036;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.tpex.util.GlobalConstants;

public class Psins036ConfigurationReader implements Tasklet, StepExecutionListener{


    private final Logger logger = LoggerFactory.getLogger(Psins036ConfigurationReader.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
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
