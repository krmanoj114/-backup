package com.tpex.batchjob.psins036;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.batchjob.bins011.Bins011;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.ProcessLogDtlsUtil;

public class Psins036ProcessMain implements Tasklet, StepExecutionListener {
	
	private Logger logger = LoggerFactory.getLogger(Psins036ProcessMain.class);

	private static final String P_O_V_RESULT = "p_o_v_result";
	private static final String P_O_V_RESULT_MSG = "p_o_v_result_msg";

	@Autowired
	private Psins036Repository psins036Repository;
	
	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;
	
	@Autowired 
	private ProcessLogDtlsUtil processLogDtlsUtil;

	@Autowired
	private Bins011 bins011;

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
	@Transactional
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		Map<String, Object> resultMap = psins036Repository.callPsinsRep(stepExecution.getJobParameters().getString(ConstantUtils.BATCH_INVOICE_NO),
				stepExecution.getJobParameters().getString(ConstantUtils.BATCH_INVOICE_USER_ID),
				stepExecution.getJobParameters().getString(ConstantUtils.COMPANY_CODE));
		String resultStatus = (String) resultMap.get(P_O_V_RESULT);
		String resultMsg = (String) resultMap.get(P_O_V_RESULT_MSG);
		
		String batchInvoiceNo = stepExecution.getJobParameters().getString(ConstantUtils.BATCH_INVOICE_NO);
		String batchUserId= stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_USER_ID);
		String batchCmpCd = stepExecution.getJobParameters().getString(ConstantUtils.COMPANY_CODE);
		
		if ("SUCCESS".equals(resultStatus) && batchInvoiceNo != null && batchUserId!= null && batchCmpCd!= null) {
			JobParameters jobParameters1 = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
					.addString(ConstantUtils.BATCH_INVOICE_NO,
							batchInvoiceNo)
					.addString(ConstantUtils.JOB_P_USER_ID,
							batchUserId)
					.addString(ConstantUtils.COMPANY_CODE,
							batchCmpCd)
					.addString(ConstantUtils.JOB_P_BATCH_NAME, ConstantUtils.BINS011)
					.addString(ConstantUtils.JOB_P_BATCH_ID, ConstantUtils.BINS011)
					.addString(ConstantUtils.JOB_P_SYSTEM_NM, ConstantUtils.IXOS).toJobParameters();
			bins011.bins011JobRunTMT(jobParameters1);

			this.stepExecution.getJobExecution().setExitStatus(ExitStatus.COMPLETED);
		} else {
			this.stepExecution.getJobExecution().setExitStatus(ExitStatus.FAILED);
		
		}
		
		saveProcessLogs(resultMsg, resultStatus);
		return RepeatStatus.FINISHED;

	}

	private void saveProcessLogs(String resultMsg, String status) {
    	ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		try {
			processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		processLogDtlsEntity.setProcessControlId(stepExecution.getJobExecution().getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
		processLogDtlsEntity.setProcessId(stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
		processLogDtlsEntity.setProcessName(stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
		processLogDtlsEntity.setProcessLogStatus("SUCCESS".equals(status) ? ConstantUtils.PL_STATUS_INFO : ConstantUtils.PL_STATUS_ERROR);
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
		processLogDtlsEntity.setProcessLogMessage(resultMsg);
		processLogDtlsRepository.save(processLogDtlsEntity);
    }
	
}
