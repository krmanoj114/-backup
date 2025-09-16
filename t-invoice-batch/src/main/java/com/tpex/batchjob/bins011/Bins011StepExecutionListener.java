package com.tpex.batchjob.bins011;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class Bins011StepExecutionListener implements StepExecutionListener {

	private Logger logger = LoggerFactory.getLogger(Bins011StepExecutionListener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// Nothing to do before step
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (stepExecution.getJobExecution().getExecutionContext().get("isError") != null
				&& (boolean) stepExecution.getJobExecution().getExecutionContext().get("isError")) {
			try {
				Files.deleteIfExists(
						Paths.get(stepExecution.getJobExecution().getExecutionContext().getString("filePath")));
			} catch (IOException e) {
				logger.error("File not deleted.");
			}
			stepExecution.setExitStatus(ExitStatus.FAILED);
			return ExitStatus.FAILED;
		}
		return null;
	}

}
