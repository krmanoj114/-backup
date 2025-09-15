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

import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.service.PartMasterService;
import com.tpex.util.ConstantUtils;

@Service
public class PartMasterServiceImpl implements PartMasterService{
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	@Qualifier("partMasterUploadJob")
	private Job partMasterUploadJob;
    
	@Async
	@Override
	public void partMasterUploadJob(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity,
			String companyCode, String userId) {
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong(ConstantUtils.JOB_P_START_AT, System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, batchName)
				.addString(ConstantUtils.JOB_P_USER_ID, userId)
				.addString(ConstantUtils.COMPANY_CODE, companyCode)
				.addString(ConstantUtils.FILENAME, fileName)
				.addString(ConstantUtils.JOB_P_BATCH_ID, oemProcessCtrlEntity != null ? oemProcessCtrlEntity.getId().getBatchId() : "")
				.addString(ConstantUtils.JOB_P_PROCESS_CTRL_ID, oemProcessCtrlEntity != null ? String.valueOf(oemProcessCtrlEntity.getId().getProcessControlId()) : "")
				.toJobParameters();
		try {
			jobLauncher.run(partMasterUploadJob, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			throw new MyResourceNotFoundException();
			
		}
		
	}

}
