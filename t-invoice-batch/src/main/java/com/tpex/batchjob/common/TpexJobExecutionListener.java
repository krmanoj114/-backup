package com.tpex.batchjob.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.batchjob.common.configuration.model.ReceiveBatchConfig;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.InterfaceFileUtils;
import com.tpex.util.ProcessLogDtlsUtil;

public class TpexJobExecutionListener <C> implements JobExecutionListener {
	
	/**
	 * Instance of logger.
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(TpexJobExecutionListener.class);

	@Autowired private OemProcessCtrlRepository processCtrlRepository;
	@Autowired private ProcessLogDtlsRepository processLogDtlsRepository;
	@Autowired private ProcessLogDtlsUtil processLogDtlsUtil;
	
	private TpexBatchContext <C> context;
	
	public TpexJobExecutionListener(TpexBatchContext <C> context) {
		this.context = context;
	}
	
    @Override
	public void beforeJob(JobExecution jobExecution) {
    	jobExecution.getExecutionContext().put(GlobalConstants.IF_CONFIG, context.getConfig());
	}
	
    @Override
	public void afterJob(JobExecution jobExecution) {
		String batchId = jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID);
		Integer processControlId = (Integer) jobExecution.getExecutionContext().get(ConstantUtils.JOB_P_PROCESS_CTRL_ID);
		if(processControlId == null) {
			return;
		}
		Optional<OemProcessCtrlEntity> ctrlLog = processCtrlRepository.findById(new OemProcessCtrlIdEntity(batchId, processControlId));
		if(ctrlLog.isPresent()) {
			OemProcessCtrlEntity ent = ctrlLog.get();
			boolean isComplete = BatchStatus.COMPLETED.equals(jobExecution.getStatus());
			ReceiveBatchConfig config = (ReceiveBatchConfig) jobExecution.getExecutionContext().get(GlobalConstants.IF_CONFIG);
			String filePath = (String) jobExecution.getExecutionContext().get(GlobalConstants.IF_FILE_PATH);
			if (config != null && filePath != null) {
				File curFile = new File(filePath);
				String moveFolder = null;
				if (isComplete || (batchId!=null && (batchId.equalsIgnoreCase(ConstantUtils.BINF009) || (batchId.equalsIgnoreCase(ConstantUtils.BINS104))))) {
					moveFolder = config.getFileInfo().getArchiveFolderName();
				} else {
					moveFolder = config.getFileInfo().getErrorFolderName();
					ent.setErrorFilePath(moveFolder + File.separator + curFile.getName());
				}
				moveTo(curFile, moveFolder);
			}
			ent.setStatus(isComplete ? ConstantUtils.BATCH_STATUS_SUCCESS : ConstantUtils.BATCH_STATUS_ERROR);
			ent.setEndTime(Timestamp.from(Instant.now()));
			processCtrlRepository.save(ent);
		}
		
		insertLogDtlsAfterJob(jobExecution, processControlId);
    }
    
	private void insertLogDtlsAfterJob(JobExecution jobExecution, Integer procCtrlId) {
		
		String compCode = jobExecution.getExecutionContext().getString(GlobalConstants.COMP_CODE);

		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		processLogDtlsEntity.setProcessControlId(procCtrlId);
		processLogDtlsEntity.setProcessId(jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
		processLogDtlsEntity.setProcessName(jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
		processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		processLogDtlsEntity.setCompanyCode(compCode);


		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
			processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_CMP_MSG);
			processLogDtlsEntity.setProcessLogDatetime(Timestamp.from(Instant.now()));
		} else {
			processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_ERROR);
			String errMsg = ConstantUtils.PL_JB_STOP_MSG;
			if(!jobExecution.getAllFailureExceptions().isEmpty()) {
				String message = jobExecution.getAllFailureExceptions().get(0).getMessage();
				errMsg = message.substring(0, message.length() > 500 ? 500 : message.length());
			}
			processLogDtlsEntity.setProcessLogMessage(errMsg);
			processLogDtlsEntity.setProcessLogDatetime(Timestamp.from(Instant.now()));
		}
		processLogDtlsRepository.save(processLogDtlsEntity);
	}

	private void moveTo(File file, String folder) {
		try {
			File fileFolder = new File(folder);
			if (!fileFolder.exists()) {
				Files.createDirectories(Paths.get(folder));
			}
			String fileName = InterfaceFileUtils.getNewFileNameIfExists(folder, file.getName());
			Files.move(Paths.get(file.getAbsolutePath()), Paths.get(folder + File.separator + fileName));
		} catch (IOException e) {
			logger.error("Error while create folder at {}", folder);
		}
	}
}
