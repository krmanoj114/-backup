package com.tpex.batchjob.binf016;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tpex.batchjob.common.configuration.model.SendBatchConfig;
import com.tpex.batchjob.common.configuration.model.SendBatchFileConfig;
import com.tpex.dto.InvoiceErrorInfo;
import com.tpex.entity.InfErrorLogEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.Binf016CustomRepository;
import com.tpex.repository.InfErrorLogRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

@Component
public class ValidateRecordsTasklet implements Tasklet {
	
	private Logger logger = LoggerFactory.getLogger(ValidateRecordsTasklet.class);
	
	private SendBatchConfig getSendBatchConfig(ExecutionContext executionContext) {
    	return (SendBatchConfig) executionContext.get(GlobalConstants.IF_CONFIG);
    }
	
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	
	SimpleDateFormat dbDf = new SimpleDateFormat(GlobalConstants.DATETIME_FORMAT);
	
	@Autowired private Binf016CustomRepository binf016CustomRepository;
	@Autowired
	private InfErrorLogRepository infErrorLogRepository;
	
	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;
	
	@Autowired 
	private ProcessLogDtlsUtil processLogDtlsUtil;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
		
		List<String> errorMsgList = new ArrayList<>();
		List<InvoiceErrorInfo> invoiceErrorInfoList = binf016CustomRepository.getInvoiceError(jobExecution.getJobParameters().getString("invNum"),
				jobExecution.getJobParameters().getString("haisenNo"), jobExecution.getJobParameters().getString("haisenYearMonth"));
		
		for (InvoiceErrorInfo invoiceErrorInfo : invoiceErrorInfoList) {
		if ("1".equals(invoiceErrorInfo.getErrorCode())) {
			errorMsgList.add(String.format(ConstantUtils.INVALID_INVOICE_ATIGA, invoiceErrorInfo.getInvNo()));
		} else if ("2".equals(invoiceErrorInfo.getErrorCode())) {
			errorMsgList.add(String.format(ConstantUtils.INVALID_INVOICE_HSCODE, invoiceErrorInfo.getInvNo()));
		} else if ("3".equals(invoiceErrorInfo.getErrorCode())) {
			errorMsgList.add(String.format(ConstantUtils.INVALID_INVOICE_ORIGIN_CRITERIA, invoiceErrorInfo.getInvNo()));
		}
		}
		

		List<InvoiceErrorInfo> invErrorInfoList = binf016CustomRepository.getPartNetWeight(jobExecution.getJobParameters().getString("invNum"),
				jobExecution.getJobParameters().getString("haisenNo"), jobExecution.getJobParameters().getString("haisenYearMonth"));

		for (InvoiceErrorInfo invErrorInfo : invErrorInfoList) {
		if(StringUtils.isNotBlank(invErrorInfo.getInvNo())){
			errorMsgList.add(String.format(ConstantUtils.INVALID_INVOICE_NET_WEIGHT, invErrorInfo.getInvNo()));
		}
		}
		
		SendBatchConfig sendBatchConfig = getSendBatchConfig(jobExecution.getExecutionContext());
    	if (!errorMsgList.isEmpty() && sendBatchConfig != null) {
    		saveErrorLogs(chunkContext.getStepContext().getStepExecution(), sendBatchConfig, errorMsgList);
    		chunkContext.getStepContext().getStepExecution().setExitStatus(ExitStatus.FAILED);
    		jobExecution.setExitStatus(ExitStatus.FAILED);
    		jobExecution.setStatus(BatchStatus.FAILED);
		}
    	
		return RepeatStatus.FINISHED;
		
	}
	
	private void saveErrorLogs(StepExecution context, SendBatchConfig sendBatchConfig, List<String> errorMsgList) {
    	InfErrorLogEntity infErrorLogEntity = new InfErrorLogEntity();
    	infErrorLogEntity.setLogId(getNextInfErrorLogId());
    	infErrorLogEntity.setProgramId(sendBatchConfig.getBatchId());
		String fileName = getFileName(sendBatchConfig, context.getJobParameters().getString(ConstantUtils.COMPANY_CODE), context.getJobExecution().getStartTime());
    	infErrorLogEntity.setFileName(fileName);
    	infErrorLogEntity.setErrorType("ERROR");
    	infErrorLogEntity.setErrorDate(Timestamp.valueOf(LocalDateTime.now()));
    	infErrorLogEntity.setExecuteDate(Timestamp.valueOf(dbDf.format(context.getJobExecution().getStartTime())));
    	infErrorLogEntity.setCmpCd(context.getJobParameters().getString(ConstantUtils.COMPANY_CODE));
    	
    	ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		try {
			processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		processLogDtlsEntity.setProcessControlId(context.getJobExecution().getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
		processLogDtlsEntity.setProcessId(context.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
		processLogDtlsEntity.setProcessName(context.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
		processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_ERROR);
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
		
    	for (String errorMsg : errorMsgList) {
    		infErrorLogEntity.setMessage(errorMsg);
    		infErrorLogRepository.save(infErrorLogEntity);
    		
    		processLogDtlsEntity.setProcessLogMessage(errorMsg);
    		processLogDtlsRepository.save(processLogDtlsEntity);
		}
    }
	
	private String getFileName(SendBatchConfig sendBatchConfig, String companyCode, Date startTime) {
    	String fileName = "";
    	Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames().stream().filter(m -> m.getCompany().equals(companyCode)).findFirst();
		if (sendBatchFileConfigOptional.isPresent()) {
			SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
			fileName = sendBatchFileConfig.getFileName().replace("YYYYMMDDHHMISS", df.format(startTime));
		}
		return fileName;
    }
	
	private int getNextInfErrorLogId() {
		InfErrorLogEntity infErrorLogEntity = infErrorLogRepository.findTopByOrderByLogIdDesc();
		return (infErrorLogEntity == null) ? 1 : infErrorLogEntity.getLogId() + 1;
	}

}
