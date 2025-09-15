package com.tpex.admin.tpexbatchjob;

import com.tpex.admin.entity.ProcessLogDtlsEntity;
import com.tpex.admin.repository.ProcessLogDtlsRepository;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.ProcessLogDtlsUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DummyTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(DummyTasklet.class);

	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;

	@Autowired
	private ProcessLogDtlsUtil processLogDtlsUtil;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
		JobParameters jobParameters = jobExecution.getJobParameters();

		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		processLogDtlsEntity.setProcessControlId(Integer.valueOf(jobParameters.getString("processControlId")));
		processLogDtlsEntity.setProcessId(jobParameters.getString("processId"));
		processLogDtlsEntity.setProcessName(jobParameters.getString("processName"));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());

		String fileName = jobParameters.getString("fileName");
		String filePath = jobParameters.getString("filePath") + fileName + ".xlsx";
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
			processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
			processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_FILE_FND_MSG + " (" + fileName + ")");
			processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
			processLogDtlsRepository.save(processLogDtlsEntity);
		}

		processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
		processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
		processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_READ_FILE_MSG + " (" + fileName + ")");
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		processLogDtlsRepository.save(processLogDtlsEntity);

		try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file))) {

			XSSFSheet worksheet = workbook.getSheetAt(0);

			int totalCount = 0;
			if (!(worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1)) {
				Iterator<Row> itr = worksheet.iterator();// iterating over excel file
				itr.next();
				while (itr.hasNext()) {
					Row row = itr.next();
					processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
					processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
					processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_PROC_ROW_MSG + (row.getRowNum()));
					processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
					processLogDtlsRepository.save(processLogDtlsEntity);
					totalCount++;
				}
			}


			List<Boolean> myList = Arrays.asList(false, true, false);

			SecureRandom r = new SecureRandom();
			boolean logFlag = myList.get(r.nextInt(myList.size()));
			if (logFlag) {
				processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
				processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
				processLogDtlsEntity.setProcessLogMessage(
						ConstantUtils.PL_JB_SUMMARY_MSG + " (" + totalCount + " / 0 / " + totalCount + ")");
				processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
				processLogDtlsRepository.save(processLogDtlsEntity);
				throw new InterruptedException(ConstantUtils.PL_JB_STOP_MSG);
			}

			processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
			processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
			processLogDtlsEntity.setProcessLogMessage(
					ConstantUtils.PL_JB_SUMMARY_MSG + " (" + totalCount + " / " + totalCount + " / 0)");
			processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));

			processLogDtlsRepository.save(processLogDtlsEntity);

		} catch (Exception e) {

			LOGGER.debug(e.getMessage());
		}
		return RepeatStatus.FINISHED;
	}

}
