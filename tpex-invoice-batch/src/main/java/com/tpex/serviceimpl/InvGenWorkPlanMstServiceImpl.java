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

import com.tpex.dto.UploadWrkPlanMasterFromVesselBookingRequest;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.service.InvGenWorkPlanMstService;
import com.tpex.util.ConstantUtils;

@Service
public class InvGenWorkPlanMstServiceImpl implements InvGenWorkPlanMstService {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("wrkPlanMasterUploadBatchJob")
	private Job wrkPlanMasterUploadBatchJob;
	
	@Autowired
	@Qualifier("wrkPlanMasterUploadFromVesselBookingBatchJob")
	private Job wrkPlanMasterUploadFromVesselBookingBatchJob;
	
	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;


	@Async
	@Override
	public void wrkPlanMasterUploadBatchJob(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity, String invoiceDate, String userId) {

		JobParameters jobParameters = new JobParametersBuilder()
				.addLong(ConstantUtils.JOB_P_START_AT, System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, batchName)
				.addString(ConstantUtils.JOB_P_USER_ID, userId)
				.addString("fileName", fileName)
				.addString(ConstantUtils.JOB_P_BATCH_ID, oemProcessCtrlEntity != null ? oemProcessCtrlEntity.getId().getBatchId() : "")
				.addString(ConstantUtils.JOB_P_PROCESS_CTRL_ID, oemProcessCtrlEntity != null ? String.valueOf(oemProcessCtrlEntity.getId().getProcessControlId()) : "")
				.toJobParameters();

		try {
			jobLauncher.run(wrkPlanMasterUploadBatchJob, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			throw new MyResourceNotFoundException();
		}
	}
	
	@Async
	@Override
	public void wrkPlanMasterUploadFromVesselBookingBatchJob(UploadWrkPlanMasterFromVesselBookingRequest request) {
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong(ConstantUtils.JOB_P_START_AT, System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, "Update Work Plan Master from Vessel booking")
				.addString(ConstantUtils.JOB_P_USER_ID, request.getUserId())
				.addString(ConstantUtils.JOB_P_BATCH_ID, ConstantUtils.BINS026)
				.addString("vanningMonth", request.getVanningMonth().replace("/", ""))
				.addString("destinationCode", request.getDestinationCode())
				.addString("etdFrom", request.getEtdFrom())
				.addString("etdTo", request.getEtdTo())
				.addString("shippingCompanyCode", request.getShippingCompanyCode())
				.addString(ConstantUtils.JOB_P_PROCESS_CTRL_ID, String.valueOf(processLogDtlsRepository.findTopByOrderByProcessControlIdDesc().getProcessControlId() + 1))
				.toJobParameters();
		
		try {
			jobLauncher.run(wrkPlanMasterUploadFromVesselBookingBatchJob, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			throw new MyResourceNotFoundException();
		}
	}



}
