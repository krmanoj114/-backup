package com.tpex.admin.tpexbatchjob;

import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.entity.OemProcessCtrlIdEntity;
import com.tpex.admin.entity.ProcessLogDtlsEntity;
import com.tpex.admin.repository.OemProcessCtrlRepository;
import com.tpex.admin.repository.ProcessLogDtlsRepository;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.ProcessLogDtlsUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The listener interface for receiving jobCompletionNotification events. The
 * class that is interested in processing a jobCompletionNotification event
 * implements this interface, and the object created with that class is
 * registered with a component using the component's
 * <code>addJobCompletionNotificationListener<code> method. When the
 * jobCompletionNotification event occurs, that object's appropriate method is
 * invoked.
 *
 * @see JobCompletionNotificationEvent
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	@Value("${upload.path}")
	String path;

	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;

	@Autowired
	EntityManagerFactory emf;

	@Autowired
	OemProcessCtrlRepository oemProcessCtrlRepository;

	@Autowired
	ProcessLogDtlsUtil processLogDtlsUtil;

	OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();

	/**
	 * After job.
	 *
	 * @param jobExecution the job execution
	 */
	// The callback method from the Spring Batch JobExecutionListenerSupport class
	// that is executed when the batch process is completed
	@Override
	public void afterJob(JobExecution jobExecution) {
		// When the batch process is completed the the users in the database are
		// retrieved and logged on the application logs
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

			String batchId = jobExecution.getJobParameters().getString("batchId");
			int processControlId = Integer
					.parseInt(jobExecution.getJobParameters().getString(ConstantUtils.PROCESS_CONTROL_ID));
			List<OemProcessCtrlEntity> entity = oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(batchId,
					processControlId);

			for (OemProcessCtrlEntity l : entity) {
				l.setStatus(1);
				l.setEndTime(new Timestamp(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime()));

				oemProcessCtrlRepository.save(l);
			}

			String fileTobeDeleted = jobExecution.getJobParameters().getString("fileName");

			deleteFile(fileTobeDeleted);
		} else {

			String batchId = jobExecution.getJobParameters().getString("batchId");
			int processControlId = Integer
					.parseInt(jobExecution.getJobParameters().getString(ConstantUtils.PROCESS_CONTROL_ID));
			List<OemProcessCtrlEntity> entity = oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(batchId,
					processControlId);

			for (OemProcessCtrlEntity l : entity) {
				l.setStatus(0);
				l.setEndTime(new Timestamp(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime()));

				oemProcessCtrlRepository.save(l);
			}

			String fileTobeDeleted = jobExecution.getJobParameters().getString("fileName");

			deleteFile(fileTobeDeleted);

		}

		// Create ProcessLogDtlsEntity entry
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		processLogDtlsEntity
		.setProcessControlId(Integer.valueOf(jobExecution.getJobParameters().getString("processControlId")));
		processLogDtlsEntity.setProcessId(jobExecution.getJobParameters().getString("processId"));
		processLogDtlsEntity.setProcessName(jobExecution.getJobParameters().getString("processName"));
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

	private void deleteFile(String fileTobeDeleted) {

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
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		super.beforeJob(jobExecution);

		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();

		processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());

		processLogDtlsEntity
		.setProcessControlId(Integer.valueOf(jobExecution.getJobParameters().getString("processControlId")));
		processLogDtlsEntity.setProcessId(jobExecution.getJobParameters().getString("processId"));
		processLogDtlsEntity.setProcessName(jobExecution.getJobParameters().getString("processName"));
		processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_INFO);
		processLogDtlsEntity.setProcessLogMessage(ConstantUtils.PL_JB_STRT_MSG);
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
		processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());

		processLogDtlsRepository.save(processLogDtlsEntity);
	}

}
