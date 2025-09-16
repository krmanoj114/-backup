package com.tpex.batchjob.binf009.tasklets;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.repository.Binf009CustomRepository;
import com.tpex.repository.NoemPackSpecRepository;
import com.tpex.repository.VprPkgSpecTempRepository;
import com.tpex.util.GlobalConstants;

public class Binf009ProcessMain implements Tasklet, StepExecutionListener {

	@Autowired
	NoemPackSpecRepository noemPackSpecRepository;

	@Autowired
	VprPkgSpecTempRepository vprPkgSpecTempRepository;
	
	@Autowired
	Binf009CustomRepository binf009CustomRepository;

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
		String compCode = (String) this.stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.COMP_CODE);
				
		binf009CustomRepository.deletePkgSpecData(compCode);
		
		binf009CustomRepository.insertPkgSpecData();
		
		Boolean isBusErr = (Boolean) this.stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IS_BUS_ERROR);
		isBusErr = isBusErr != null && isBusErr;

		if (isBusErr) {
			this.stepExecution.getJobExecution().setExitStatus(ExitStatus.FAILED);
			return RepeatStatus.FINISHED;
		}

		this.stepExecution.getJobExecution().setExitStatus(ExitStatus.COMPLETED);
		return RepeatStatus.FINISHED;
	}

}
