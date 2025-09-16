package com.tpex.batchjob.binf301.tasklets;

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

import com.tpex.batchjob.binf301.configuration.model.Binf301Config;
import com.tpex.batchjob.common.configuration.model.LineConfig;
import com.tpex.util.ConstantUtils.Binf301;
import com.tpex.util.GlobalConstants;

public class Binf301ConfigurationReader implements Tasklet, StepExecutionListener {

	private final Logger logger = LoggerFactory.getLogger(Binf301ConfigurationReader.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {

		//Get Configuration information.
		Binf301Config config = (Binf301Config) stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_CONFIG);

		//Put information of each line in Map
		Map <String, LineConfig> lineInfo = new HashMap <> ();

		if(config != null) {

			lineInfo.put(Binf301.HEAD, config.getFileStructure().getFileHeader());
			lineInfo.put(Binf301.FOOT, config.getFileStructure().getFileFooter());
			lineInfo.put(Binf301.DATA, config.getFileStructure().getEngVinMstDetails());

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
