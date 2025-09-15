package com.tpex.batchjob.binf005.tasklets;

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
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.VprContTempRepository;
import com.tpex.repository.VprDlyVinTempRepository;
import com.tpex.repository.VprModuleTempRepository;
import com.tpex.repository.VprPartTempRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

public class Binf005DeleteStagingData implements Tasklet, StepExecutionListener {

	@Autowired private VprContTempRepository vprContTempRepository;
	@Autowired private VprModuleTempRepository vprModuleTempRepository;
	@Autowired private VprPartTempRepository vprPartTempRepository;
	@Autowired private VprDlyVinTempRepository vprDlyVinTempRepository;
	@Autowired private OemProcessCtrlRepository processCtrlRepository;
	@Autowired private ProcessLogDtlsRepository processLogDtlsRepository;
	@Autowired private ProcessLogDtlsUtil processLogDtlsUtil;
	
	private String compCode;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		deleteStagingData();
		return RepeatStatus.FINISHED;
	}

	private void deleteStagingData() {
		vprContTempRepository.deleteByIntrIdAndCompCode(GlobalConstants.BINF005_INTR_ID, compCode);
		vprModuleTempRepository.deleteByIntrIdAndCompCode(GlobalConstants.BINF005_INTR_ID, compCode);
		vprPartTempRepository.deleteByIntrIdAndCompCode(GlobalConstants.BINF005_INTR_ID, compCode);
		vprDlyVinTempRepository.deleteByIntrIdAndCompCode(GlobalConstants.BINF005_INTR_ID, compCode);
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

		Timestamp timeNow = Timestamp.valueOf(LocalDateTime.now());

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
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
		processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		
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
		return "BINF005DeleteStagingDataAfterError".equals(stepExecution.getStepName());
	}

}
