package com.tpex.batchjob.binf023;

import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.ProcessLogDtlsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Binf023StepExecutionListener implements StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(Binf023StepExecutionListener.class);

    @Autowired
    private ProcessLogDtlsRepository processLogDtlsRepository;

    @Autowired
    private ProcessLogDtlsUtil processLogDtlsUtil;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        //Nothing to do before step
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        if (stepExecution.getWriteCount() == 0) {
            ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
            try {
                processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            processLogDtlsEntity.setProcessControlId(stepExecution.getJobExecution().getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
            processLogDtlsEntity.setProcessId(stepExecution.getJobExecution().getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
            processLogDtlsEntity.setProcessName(stepExecution.getJobExecution().getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
            processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_ERROR);
            processLogDtlsEntity.setProcessLogMessage(ConstantUtils.NO_DATA_TO_BROKER);
            processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
            processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
            processLogDtlsEntity.setCompanyCode(stepExecution.getJobExecution().getJobParameters().getString(ConstantUtils.COMPANY_CODE));

            processLogDtlsRepository.save(processLogDtlsEntity);
            stepExecution.setExitStatus(ExitStatus.FAILED);
            stepExecution.getJobExecution().setStatus(BatchStatus.FAILED);
        }
        if (stepExecution.getJobExecution().getExecutionContext().get(ConstantUtils.IS_ERROR_FLAG) != null
                && (boolean) stepExecution.getJobExecution().getExecutionContext().get(ConstantUtils.IS_ERROR_FLAG)) {
            try {
                Files.deleteIfExists(Paths.get(stepExecution.getJobExecution().getExecutionContext().getString(ConstantUtils.FILEPATH)));
            } catch (IOException e) {
                logger.error(ConstantUtils.FILE_NOT_DELETED);
            }
            stepExecution.setExitStatus(ExitStatus.FAILED);
            stepExecution.getJobExecution().setStatus(BatchStatus.FAILED);
            return ExitStatus.FAILED;
        }
        return null;
    }

}
