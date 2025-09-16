package com.tpex.batchjob.bins104.tasklets;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
import com.tpex.repository.Bins104CustomRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;

public class Bins104DeleteStagingData implements Tasklet, StepExecutionListener {

	@Autowired
	private OemProcessCtrlRepository processCtrlRepository;
	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;	
	@Autowired
	Bins104CustomRepository bins104CustomRepository;

	private String compCode;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		deleteStagingData();
		return RepeatStatus.FINISHED;
	}
	
	private void deleteStagingData() {
		bins104CustomRepository.deleteStagingData(compCode);
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		compCode = stepExecution.getJobExecution().getExecutionContext().getString(GlobalConstants.COMP_CODE);
		if (!isAfterError(stepExecution)) {
			insertProcCtrlLog(stepExecution);
		}
	}

	private void insertProcCtrlLog(StepExecution stepExecution) {
		String batchId = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID);
		String userId = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_USER_ID);
		String batchName = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME);
		String proCtrlIdStr = stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID);
		Integer procCtrlId = null;
		if (StringUtils.isBlank(proCtrlIdStr)) {
			procCtrlId = processLogDtlsRepository.findTopByOrderByProcessControlIdDesc().getProcessControlId() + 1;
		} else {
			procCtrlId = Integer.valueOf(proCtrlIdStr);
		}

		stepExecution.getJobExecution().getExecutionContext().put(ConstantUtils.JOB_P_PROCESS_CTRL_ID, procCtrlId);

		Timestamp timeNow = Timestamp.valueOf(LocalDateTime.now());

		OemProcessCtrlIdEntity logMstIdEnt = new OemProcessCtrlIdEntity(batchId, procCtrlId);
		OemProcessCtrlEntity logMstEnt = new OemProcessCtrlEntity();
		logMstEnt.setId(logMstIdEnt);
		logMstEnt.setProgramId(batchId);
		logMstEnt.setStartTime(timeNow);
		logMstEnt.setUserId(userId);
		logMstEnt.setStatus(ConstantUtils.BATCH_STATUS_PROCESS);
		logMstEnt.setSubmitTime(timeNow);
		logMstEnt.setSystemName(ConstantUtils.UPD_BY_SCHEDULE);
		logMstEnt.setProgramName(batchName);
		logMstEnt.setFileName(
				stepExecution.getJobExecution().getExecutionContext().getString(GlobalConstants.IF_FILE_NM));

		processCtrlRepository.save(logMstEnt);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (isAfterError(stepExecution)) {
			return ExitStatus.FAILED;
		}
		return ExitStatus.COMPLETED;
	}

	private boolean isAfterError(StepExecution stepExecution) {
		return "BINS104DeleteStagingDataAfterError".equals(stepExecution.getStepName());
	}

}
