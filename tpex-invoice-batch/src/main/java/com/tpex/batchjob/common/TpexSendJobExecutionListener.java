package com.tpex.batchjob.common;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.entity.OemProgDtlsEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.OemProgDtlsRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

public class TpexSendJobExecutionListener <C> implements JobExecutionListener {
	
	/**
	 * Instance of logger.
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(TpexSendJobExecutionListener.class);

	@Autowired private OemProcessCtrlRepository processCtrlRepository;
	@Autowired private ProcessLogDtlsRepository processLogDtlsRepository;
	@Autowired private ProcessLogDtlsUtil processLogDtlsUtil;
	@Autowired
	OemProgDtlsRepository oemProgDtlsRepository;
	
	private TpexBatchContext <C> context;
	
	public TpexSendJobExecutionListener(TpexBatchContext <C> context) {
		this.context = context;
	}
	
    @Override
	public void beforeJob(JobExecution jobExecution) {
    	jobExecution.getExecutionContext().put(GlobalConstants.IF_CONFIG, context.getConfig());
    	
    	String batchId = jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID);
		String batchName = jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME);
		String systemName = jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_SYSTEM_NM);
		
		OemProcessCtrlEntity oemProcessCtrlEntity = saveProcessCtrlDetails(batchId, null, systemName);
		
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		try {
			processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		processLogDtlsEntity.setProcessControlId(oemProcessCtrlEntity.getId().getProcessControlId());
		processLogDtlsEntity.setProcessId(batchId);
		processLogDtlsEntity.setProcessName(batchName);
		processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
		processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_STRT_MSG);
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
		processLogDtlsEntity.setCompanyCode(jobExecution.getJobParameters().getString("companyCode"));

		processLogDtlsRepository.save(processLogDtlsEntity);
		
		jobExecution.getExecutionContext().put(ConstantUtils.JOB_P_PROCESS_CTRL_ID, oemProcessCtrlEntity.getId().getProcessControlId());
	}
    
	private OemProcessCtrlEntity saveProcessCtrlDetails(String batchId, String batchParameter, String systemName) {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		OemProcessCtrlEntity entity = new OemProcessCtrlEntity();
		id.setBatchId(batchId);
		id.setProcessControlId(processCtrlRepository.getIdOfProcessControl());
		entity.setId(id);
		entity.setParameter(batchParameter);
		entity.setSubmitTime(new java.sql.Timestamp(System.currentTimeMillis()));
		entity.setProgramId(batchId);
		entity.setStatus(2);
		entity.setDeamon("N");
		entity.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
		entity.setSystemName(systemName);
		entity.setUserId(ConstantUtils.UPD_BY_SCHEDULE);
		entity.setProgramName(getProcessNameById(batchId));
		processCtrlRepository.save(processCtrlRepository.save(entity));
		return entity;
	}
	
	private String getProcessNameById(String programId) {
		OemProgDtlsEntity oemProgDtlsEntity = oemProgDtlsRepository.findTopByIdProgramId(programId);
		return oemProgDtlsEntity == null ? "" : oemProgDtlsEntity.getProgramDesc();
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
			ent.setStatus(isComplete ? ConstantUtils.BATCH_STATUS_SUCCESS : ConstantUtils.BATCH_STATUS_ERROR);
			ent.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
			processCtrlRepository.save(ent);
		}
		
		insertLogDtlsAfterJob(jobExecution, processControlId);
    }
    
	private void insertLogDtlsAfterJob(JobExecution jobExecution, Integer procCtrlId) {
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		processLogDtlsEntity.setProcessControlId(procCtrlId);
		processLogDtlsEntity.setProcessId(jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
		processLogDtlsEntity.setProcessName(jobExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
		processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		processLogDtlsEntity.setCompanyCode(jobExecution.getJobParameters().getString("companyCode"));

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
			processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_CMP_MSG);
			processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		} else {
			processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_ERROR);
			String errMsg = ConstantUtils.PL_JB_STOP_MSG;
			if(!jobExecution.getAllFailureExceptions().isEmpty()) {
				String message = jobExecution.getAllFailureExceptions().get(0).getMessage();
				errMsg = message.substring(0, message.length() > 500 ? 500 : message.length());
			}
			processLogDtlsEntity.setProcessLogMessage(errMsg);
			processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		}
		processLogDtlsRepository.save(processLogDtlsEntity);
	}

}