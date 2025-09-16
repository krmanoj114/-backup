package com.tpex.batchjob.binf005.tasklets;

import java.time.LocalDateTime;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.repository.Binf005CustomRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.InterfaceFileUtils;
import com.tpex.util.ProcessLogDtlsUtil;

public class Binf005ProcessMain implements Tasklet, StepExecutionListener {
	
	@Autowired private Binf005CustomRepository binf005CustomRepository;
	@Autowired private ProcessLogDtlsUtil processLogDtlsUtil;

	private StepExecution stepExecution;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return this.stepExecution.getJobExecution().getExitStatus();
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String dt = (String) this.stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.EXECUTE_DATETIME);
		LocalDateTime executeDate = InterfaceFileUtils.convertStringToLocalDateTime(dt, GlobalConstants.DATETIME_FORMAT);
		String compCode = (String) this.stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.COMP_CODE);
		String batchId = this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID);
		String batchName = this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME);
		Integer procCtrlId = this.stepExecution.getJobExecution().getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID);
		
		boolean isProcBusErr = binf005CustomRepository.callBinf006A(executeDate, batchId, processLogDtlsUtil.getCurrentSpanId(), compCode, procCtrlId, batchName);
		Boolean isBusErr = (Boolean) this.stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IS_BUS_ERROR);
		isBusErr = isBusErr != null && isBusErr;
		
		if (isProcBusErr || isBusErr) {
			this.stepExecution.getJobExecution().setExitStatus(ExitStatus.FAILED);
			return RepeatStatus.FINISHED;
		}
		
		binf005CustomRepository.processContTemp(executeDate, compCode, ConstantUtils.UPD_BY_SCHEDULE);
		binf005CustomRepository.processModuleTemp(executeDate, compCode, ConstantUtils.UPD_BY_SCHEDULE);
		binf005CustomRepository.processPartTemp(executeDate, compCode, ConstantUtils.UPD_BY_SCHEDULE);
		binf005CustomRepository.updInvAico(executeDate, compCode);
		binf005CustomRepository.processInvVin(executeDate, compCode, ConstantUtils.UPD_BY_SCHEDULE);
		isBusErr = binf005CustomRepository.updVltActDtTm(executeDate, batchId, processLogDtlsUtil.getCurrentSpanId(), compCode, procCtrlId, batchName);
		if (isBusErr) {
			this.stepExecution.getJobExecution().setExitStatus(ExitStatus.FAILED);
			return RepeatStatus.FINISHED;
		}
		binf005CustomRepository.recalContModWt(executeDate, compCode);
		binf005CustomRepository.updDlyPxPSrsNm(executeDate, compCode);
		binf005CustomRepository.genLotPartShortageInfo(executeDate, batchId, processLogDtlsUtil.getCurrentSpanId(), compCode, procCtrlId, batchName);
		
		this.stepExecution.getJobExecution().setExitStatus(ExitStatus.COMPLETED);
		return RepeatStatus.FINISHED;
	}

}
