package com.tpex.batchjob.workplanmasteruploadfromvessel;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import com.tpex.exception.BatchReadFailedException;
import com.tpex.exception.BatchWriteFailedException;
import com.tpex.util.ConstantUtils;

public class WrkPlanMasterUploadFromVesselBookingStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		//ignore		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (!stepExecution.getFailureExceptions().isEmpty()) {
			Throwable ex = stepExecution.getFailureExceptions().get(0);
			if(!(ex instanceof BatchReadFailedException)) {
				stepExecution.getFailureExceptions().clear();
				stepExecution.getFailureExceptions().add(new BatchWriteFailedException(ConstantUtils.BATCH_UPD_WPM_ERR_MSG));
			}
		}

		return ExitStatus.COMPLETED;
	}

}
