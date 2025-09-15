package com.tpex.batchjob.binf009.tasklets;

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

import com.tpex.batchjob.binf009.configuration.model.Binf009Config;
import com.tpex.batchjob.common.configuration.model.LineConfig;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ConstantUtils.Binf009;

public class Binf009ConfigurationReader implements Tasklet, StepExecutionListener {

	private final Logger logger = LoggerFactory.getLogger(Binf009ConfigurationReader.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {

		//Get Configuration information.
		Binf009Config config = (Binf009Config) stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_CONFIG);

		//Put information of each line in Map
		Map <String, LineConfig> lineInfo = new HashMap <> ();

		if(config != null) {

			lineInfo.put(Binf009.GATEWAY_TAG, config.getFileStructure().getGatewayHeader());
			lineInfo.put(Binf009.HEAD, config.getFileStructure().getFileHeader());
			lineInfo.put(Binf009.FOOT, config.getFileStructure().getFileFooter());
			lineInfo.put(Binf009.DATA, config.getFileStructure().getPackSpecDetails());

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
