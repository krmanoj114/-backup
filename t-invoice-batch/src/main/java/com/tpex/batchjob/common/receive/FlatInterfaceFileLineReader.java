package com.tpex.batchjob.common.receive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.batchjob.common.FlatFileReader;
import com.tpex.batchjob.common.configuration.model.LineData;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

public class FlatInterfaceFileLineReader implements ItemReader <LineData>, StepExecutionListener {
	
	@Autowired private ProcessLogDtlsRepository processLogDtlsRepository;
	@Autowired private ProcessLogDtlsUtil processLogDtlsUtil;
    /** 
     * FlatFileReader object to call method for file processing. 
     * 
     * */
    private FlatFileReader fu;

    /**
     * Method to process information before step execution.
     * 1. Open the interface data file to process.
     * 
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
    	//Get File to process.
    	String  fileName = (String) stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_FILE_PATH);

    	//open file for processing.
    	fu = new FlatFileReader(fileName);
    	
    	File file = new File(fileName);
    	
    	try {
    		boolean readable = Files.isReadable(file.toPath());
    		if(!readable) {
    			throw new IOException(ConstantUtils.FILE_CANNOT_OPEN);
    		}
			List<String> allLines = Files.readAllLines(file.toPath());
			boolean hasLine = allLines.stream().anyMatch(StringUtils::isNotBlank);
			if(!hasLine) {
				throw new IOException(ConstantUtils.FILE_IS_EMPTY);
			}
		} catch (IOException e) {
			String batchId = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID);
			String batchName = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME);
			int procCtrlId = stepExecution.getJobExecution().getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID);
			
			ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
			processLogDtlsEntity.setProcessControlId(procCtrlId);
			processLogDtlsEntity.setProcessId(batchId);
			processLogDtlsEntity.setProcessName(batchName);
			processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_ERROR);
			processLogDtlsEntity.setProcessLogMessage(e.getMessage());
			processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
			processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
			processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
			
			processLogDtlsRepository.save(processLogDtlsEntity);
			
			stepExecution.getJobExecution().setExitStatus(ExitStatus.FAILED);
		}
    }

    /**
     * Method to process each line of flat file.
     * Prepare information as object of LineDataInfo.
     * 	1. Current Line No.
     *  2. Current Line Content.
     * 
     */
    @Override
    public LineData read() throws Exception {
    	
    	String line = fu.readLine();

    	if (line == null) {
    		//complete file has been read, so close the reader.
            fu.closeReader();
    		return null;
    	}
    	
    	//Prepare information of line data.
    	LineData lineInfo = new LineData();
    	lineInfo.setLineNo(fu.getCurrentLineNo());
    	lineInfo.setLineContent(line);
    	
    	return lineInfo;
    }

    /**
     * Method called by Spring batch framework, after step processing.
     * 
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getJobExecution().getExitStatus() != ExitStatus.FAILED ? ExitStatus.COMPLETED : ExitStatus.FAILED;
    }
    
}