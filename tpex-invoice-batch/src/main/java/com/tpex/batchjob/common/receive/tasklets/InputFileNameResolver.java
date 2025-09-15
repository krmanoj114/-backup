package com.tpex.batchjob.common.receive.tasklets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.tpex.batchjob.common.configuration.model.InputFileName;
import com.tpex.batchjob.common.configuration.model.ReceiveBatchConfig;
import com.tpex.util.GlobalConstants;

public class InputFileNameResolver implements Tasklet, StepExecutionListener {

	/**
	 * Instance of logger.
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(InputFileNameResolver.class);

	private StepExecution stepExecution;
	/**
	 * Method to grab file to process.
	 * 
	 */
    @Override
    public void beforeStep(StepExecution stepExecution) {
    	this.stepExecution = stepExecution;
    	String filePath = null;
    	
    	//Get Configuration information.
    	ReceiveBatchConfig config = (ReceiveBatchConfig) stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_CONFIG);
    	
    	if(config != null) {
	    	//Get file to process.
	    	File file = getFileToProcess(config.getFileInfo().getInputFileFolder(), config.getFileInfo().getInputFileNames());
	
	    	//Move file to processing folder (if file exists).
	    	if (file != null) {
	    		try {
	    			File tmp = new File(config.getFileInfo().getProcessingFolder());
	    			if (!tmp.exists()) {
	    				Files.createDirectories(Paths.get(config.getFileInfo().getProcessingFolder()));
	    			}
				} catch (IOException e) {
					logger.error("Error while create processing folder at {}", config.getFileInfo().getProcessingFolder());
				}
	        	filePath = moveFileToProcessingFolder(file, config.getFileInfo().getProcessingFolder());
	        	stepExecution.getJobExecution().getExecutionContext().put(GlobalConstants.IF_FILE_NM, file.getName());
	    	}
    	}
    	//set file name in job execution context (To refer in further steps).
    	stepExecution.getJobExecution().getExecutionContext().put(GlobalConstants.IF_FILE_PATH, filePath);
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
    	
    	if (stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_FILE_PATH) == null) {
    		logger.info("No file found to process.");
        	return new ExitStatus(GlobalConstants.FILE_NOT_FOUND);
    	}
    	
		logger.info("Input file found to process :: {}", stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_FILE_PATH));
    	return ExitStatus.COMPLETED;
    }

    /**
     * Method to get first file to process (if exists).
     * 
     * @param inputFolder
     * @return
     */
    private File getFileToProcess(String inputFolder, List <InputFileName> fileNames) {
    	
    	final File folder = new File(inputFolder);

    	// revise logic to check all files and sort on first receiving and get first.
    	for (final File fileEntry : folder.listFiles()) {

    		String currentFileName = fileEntry.getName();
        	
        	for (InputFileName input : fileNames) {
        		if (currentFileName.startsWith(input.getBeginWith())) {
        			this.stepExecution.getJobExecution().getExecutionContext().put(GlobalConstants.COMP_CODE, input.getCompany());
            		return fileEntry;
        		}
        	}
        }
    	
    	return null;
    }
    
    /**
     * Method to get first file to process (if exists).
     * 
     * @param inputFolder
     * @return
     */
    private  String moveFileToProcessingFolder(File file, String processingFolder) {
    	boolean renameTo = file.renameTo(new File(processingFolder + File.separator + file.getName()));
    	if(!renameTo) {
    		return null;
    	}
    	return processingFolder + File.separator + file.getName();
    }
	
}
