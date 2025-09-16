package com.tpex.batchjob.binf009.tasklets;

import java.sql.Timestamp;
import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.Binf009CustomRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

public class Binf009DeleteStagingData implements Tasklet, StepExecutionListener {

	@Autowired 
	private ProcessLogDtlsRepository processLogDtlsRepository;

	@Autowired 
	private OemProcessCtrlRepository processCtrlRepository;

	@Autowired 
	private ProcessLogDtlsUtil processLogDtlsUtil;

	@Autowired
	Binf009CustomRepository binf009CustomRepository;

	private String compCode;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		deleteStagingData();
		return RepeatStatus.FINISHED;
	}

	private void deleteStagingData() {		

		binf009CustomRepository.deleteStagingData(compCode);
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		compCode = stepExecution.getJobExecution().getExecutionContext().getString(GlobalConstants.COMP_CODE);
		if(!isAfterError(stepExecution)) {
			insertProcCtrlLog(stepExecution);
		}
	}

	private void insertProcCtrlLog(StepExecution stepExecution) {
		String batchId = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID);
		String userId = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_USER_ID);
		String batchName = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME);
		String proCtrlIdStr = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID);
		Integer procCtrlId = null;
		if(StringUtils.isBlank(proCtrlIdStr)) {
			procCtrlId = processLogDtlsRepository.findTopByOrderByProcessControlIdDesc().getProcessControlId() + 1;
		} else {
			procCtrlId = Integer.valueOf(proCtrlIdStr);
		}

		stepExecution.getJobExecution().getExecutionContext().put(ConstantUtils.JOB_P_PROCESS_CTRL_ID, procCtrlId);

		Timestamp timeNow = Timestamp.from(Instant.now());

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
		logMstEnt.setFileName(stepExecution.getJobExecution().getExecutionContext().getString(GlobalConstants.IF_FILE_NM));
		logMstEnt.setCompanyCOde(compCode);

		processCtrlRepository.save(logMstEnt);

		insertLogDtls(batchId, batchName, procCtrlId);
	}

	private void insertLogDtls(String batchId, String batchName, Integer procCtrlId) {
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		processLogDtlsEntity.setProcessControlId(procCtrlId);
		processLogDtlsEntity.setProcessId(batchId);
		processLogDtlsEntity.setProcessName(batchName);
		processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
		processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_STRT_MSG);
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.from(Instant.now()));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
		processLogDtlsEntity.setCompanyCode(compCode);
		processLogDtlsRepository.save(processLogDtlsEntity);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (isAfterError(stepExecution)) {
			return ExitStatus.FAILED;
		}
		return ExitStatus.COMPLETED;
	}

	private boolean isAfterError(StepExecution stepExecution) {
		return "BINF009DeleteStagingDataAfterError".equals(stepExecution.getStepName());
	}

}
