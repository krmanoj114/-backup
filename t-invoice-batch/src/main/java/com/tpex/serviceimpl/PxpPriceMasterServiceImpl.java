package com.tpex.serviceimpl;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tpex.dto.UploadPxpPriceMasterJobDto;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.service.PxpPriceMasterService;
import com.tpex.util.ConstantUtils;

/**
 * The Class PxpPriceMasterServiceImpl.
 */
@Service
public class PxpPriceMasterServiceImpl implements PxpPriceMasterService {

	/** The job launcher. */
	@Autowired
	private JobLauncher jobLauncher;

	/** The pxp price master upload job. */
	@Autowired
	@Qualifier("pxpPriceMasterUploadJob")
	private Job pxpPriceMasterUploadJob;
	
	/**
	 * Pxp price master upload batch job.
	 *
	 * @param uploadPxpPriceMasterJobDto the upload pxp price master job dto
	 */
	@Async
	@Override
	public void pxpPriceMasterUploadBatchJob(UploadPxpPriceMasterJobDto uploadPxpPriceMasterJobDto) {
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong(ConstantUtils.JOB_P_START_AT, System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, uploadPxpPriceMasterJobDto.getBatchName())
				.addString(ConstantUtils.JOB_P_USER_ID, uploadPxpPriceMasterJobDto.getUserId())
				.addString("companyCode", uploadPxpPriceMasterJobDto.getCompanyCode())
				.addString("effectiveFromDate", uploadPxpPriceMasterJobDto.getEffectiveFromDate())
				.addString("effectiveToDate", uploadPxpPriceMasterJobDto.getEffectiveToDate())
				.addString("fileName", uploadPxpPriceMasterJobDto.getFileName())
				.addString(ConstantUtils.JOB_P_BATCH_ID, uploadPxpPriceMasterJobDto.getOemProcessCtrlEntity() != null ? uploadPxpPriceMasterJobDto.getOemProcessCtrlEntity().getId().getBatchId() : "")
				.addString(ConstantUtils.JOB_P_PROCESS_CTRL_ID, uploadPxpPriceMasterJobDto.getOemProcessCtrlEntity() != null ? String.valueOf(uploadPxpPriceMasterJobDto.getOemProcessCtrlEntity().getId().getProcessControlId()) : "")
				.toJobParameters();
		try {
			jobLauncher.run(pxpPriceMasterUploadJob, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			throw new MyResourceNotFoundException();
		}
	}

}
