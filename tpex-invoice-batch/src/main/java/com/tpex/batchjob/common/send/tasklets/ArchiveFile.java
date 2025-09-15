package com.tpex.batchjob.common.send.tasklets;

import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.tpex.batchjob.common.configuration.model.SendBatchConfig;
import com.tpex.batchjob.common.configuration.model.SendBatchFileConfig;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;

public class ArchiveFile implements Tasklet {

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Optional<StepExecution> stepExecution = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getStepExecutions().stream().findFirst();
		if (stepExecution.isPresent()) {
			SendBatchConfig sendBatchConfig = (SendBatchConfig) stepExecution.get().getJobExecution().getExecutionContext().get(GlobalConstants.IF_CONFIG);
			if (sendBatchConfig != null) {
				Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames().stream().filter(m -> m.getCompany().equals(stepExecution.get().getJobParameters().getString(ConstantUtils.COMPANY_CODE))).findFirst();
				if (sendBatchFileConfigOptional.isPresent()) {
					SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
					String filePath = stepExecution.get().getJobExecution().getExecutionContext().getString(ConstantUtils.FILEPATH);
					if (FileUtils.getFile(filePath).exists()) {
						FileUtils.moveFile(FileUtils.getFile(filePath), 
								FileUtils.getFile(filePath.replace(sendBatchFileConfig.getOutputFolder(), sendBatchFileConfig.getArchiveFolder())));
					}
				}
			}
		}
		
		return RepeatStatus.FINISHED;
	}

}