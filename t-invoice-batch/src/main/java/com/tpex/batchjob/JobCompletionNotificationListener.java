package com.tpex.batchjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.ProcessLogDtlsUtil;

/**
 * The listener interface for receiving jobCompletionNotification events.
 * The class that is interested in processing a jobCompletionNotification
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addJobCompletionNotificationListener<code> method. When
 * the jobCompletionNotification event occurs, that object's appropriate
 * method is invoked.
 *
 * @see JobCompletionNotificationEvent
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	
	/** The path. */
	@Value("${upload.path}")
	String path;

	/** The process log dtls repository. */
	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;
	
	/** The emf. */
	@Autowired
	EntityManagerFactory emf;
	
	/** The oem process ctrl repository. */
	@Autowired
	OemProcessCtrlRepository oemProcessCtrlRepository;
	
	/** The process log dtls util. */
	@Autowired
	ProcessLogDtlsUtil processLogDtlsUtil;
	
	/** The entity. */
	OemProcessCtrlEntity entity =  new OemProcessCtrlEntity();
	
	/** The id. */
	OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
	
	@Autowired
	TpexConfigRepository tpexConfigRepository;

	private List<String> mandJobParam = new ArrayList<>(
			Arrays.asList(ConstantUtils.JOB_P_PROCESS_CTRL_ID, ConstantUtils.JOB_P_BATCH_ID,
					ConstantUtils.JOB_P_BATCH_NAME, ConstantUtils.JOB_P_USER_ID, ConstantUtils.JOB_P_START_AT));
	
	/**
	 * After job.
	 *
	 * @param jobExecution the job execution
	 */
	// The callback method from the Spring Batch JobExecutionListenerSupport class that is executed when the batch process is completed
	@Override
	public void afterJob(JobExecution jobExecution) {
		// When the batch process is completed the the users in the database are retrieved and logged on the application logs
			
		String batchId=jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID);
		int processControlId = Integer.parseInt(jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
		List<OemProcessCtrlEntity> oemProcessCtrlEntityList = oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(batchId,processControlId);
		String warningFlag = "";
        for(OemProcessCtrlEntity oemProcessCtrlEntity : oemProcessCtrlEntityList) {
        	oemProcessCtrlEntity.setStatus(getProcessStatus(jobExecution));
        	oemProcessCtrlEntity.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        	if(batchId.equals("BINS027")) {
				warningFlag = (String) jobExecution.getExecutionContext().get("warningFlag");
				oemProcessCtrlEntity.setParameter(warningFlag);
			}
            setErrorFilePathIfFailed(jobExecution, batchId, warningFlag, oemProcessCtrlEntity);
        	oemProcessCtrlRepository.save(oemProcessCtrlEntity);
        }
        
		String fileTobeDeleted = jobExecution.getJobParameters().getString("fileName");
		if (StringUtil.isNotBlank(fileTobeDeleted)) {
			Path file = Paths.get(path).resolve(fileTobeDeleted);
			if (Files.exists(file)) {
				try {
					Files.delete(file);
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
		
		//Create ProcessLogDtlsEntity entry
		createProcessLogDtlsAfterJob(jobExecution);

	}

	private void setErrorFilePathIfFailed(JobExecution jobExecution, String batchId, String warningFlag,
			OemProcessCtrlEntity oemProcessCtrlEntity) {
		if ((jobExecution.getStatus() == BatchStatus.FAILED) || (warningFlag != null && warningFlag.equals("Y") && batchId.equals("BINS027"))) {
			
			String fileName = jobExecution.getJobParameters().getString("fileName");
			if (StringUtils.isNotBlank(fileName)) {
				String errorFilePath = tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue();
				oemProcessCtrlEntity.setErrorFilePath(errorFilePath.concat("/").concat(fileName));
			}
		}
	}
	
	private int getProcessStatus(JobExecution jobExecution) {
		int status = 0;
    	if ("Y".equals(jobExecution.getExecutionContext().get("successWithWarning"))) {
    		status = 2;
    	} else if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
    		status = 1;
    	}
    	return status;
	}
	
	/**
	 * Creates the process log dtls after job.
	 *
	 * @param jobExecution the job execution
	 */
	private void createProcessLogDtlsAfterJob(JobExecution jobExecution) {
		//Create ProcessLogDtlsEntity entry
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		processLogDtlsEntity.setProcessControlId(Integer.valueOf(jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID)));
		processLogDtlsEntity.setProcessId(jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
		processLogDtlsEntity.setProcessName(jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());

		try {
			processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
        
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
        	processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
    		processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_CMP_MSG);
    		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
        } else {
			processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_ERROR);
			processLogDtlsEntity.setProcessLogMessage(jobExecution.getAllFailureExceptions().get(0).getMessage());
			processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
        }
        processLogDtlsRepository.save(processLogDtlsEntity);
	}

	/**
	 * Before job.
	 *
	 * @param jobExecution the job execution
	 */
	@Override
	public void beforeJob(JobExecution jobExecution) {
		super.beforeJob(jobExecution);
		
		String batchId = jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID);
		String userId = jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_USER_ID);
		String batchName = jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME);
		String proCtrlIdStr = jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID);
		Integer procCtrlId = null;
		if(StringUtils.isBlank(proCtrlIdStr)) {
			procCtrlId = processLogDtlsRepository.findTopByOrderByProcessControlIdDesc().getProcessControlId() + 1;
		} else {
			procCtrlId = Integer.valueOf(proCtrlIdStr);
		}

		Timestamp timeNow = Timestamp.valueOf(LocalDateTime.now());
		
		if(ConstantUtils.BINS026.equals(batchId)) {
			OemProcessCtrlIdEntity logMstIdEnt = new OemProcessCtrlIdEntity(batchId, procCtrlId);
			OemProcessCtrlEntity logMstEnt = new OemProcessCtrlEntity();
			logMstEnt.setId(logMstIdEnt);
			logMstEnt.setProgramId(batchId);
			logMstEnt.setStartTime(timeNow);
			logMstEnt.setUserId(userId);
			logMstEnt.setStatus(ConstantUtils.BATCH_STATUS_PROCESS);
			logMstEnt.setSubmitTime(timeNow);
			logMstEnt.setSystemName("TPEX");
			logMstEnt.setProgramName(batchName);
			
            String addsParam = jobExecution.getJobParameters().getParameters().entrySet().stream()
            .filter(e -> !mandJobParam.contains(e.getKey())).map(m -> String.valueOf(m.getValue().getValue()))
            .collect(Collectors.joining(","));
            logMstEnt.setParameter(addsParam.length() > 50 ? addsParam.substring(0, 50) : addsParam);

			oemProcessCtrlRepository.save(logMstEnt);
		}
		
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		try {
			processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		processLogDtlsEntity.setProcessControlId(procCtrlId);
		processLogDtlsEntity.setProcessId(batchId);
		processLogDtlsEntity.setProcessName(batchName);
		processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
		processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_STRT_MSG);
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
		
		processLogDtlsRepository.save(processLogDtlsEntity);
	}
	
}
