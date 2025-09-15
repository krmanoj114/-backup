package com.tpex.batchjob.bins104.tasklets;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.entity.CoeCeptMasterEntity;
import com.tpex.entity.CoeCeptTempEntity;
import com.tpex.repository.Bins104CustomRepository;
import com.tpex.repository.CoeCeptMasterRepository;
import com.tpex.repository.CoeCeptTempRepository;
import com.tpex.util.GlobalConstants;

public class Bins104ProcessMain implements Tasklet, StepExecutionListener {
	
	
	@Autowired private CoeCeptMasterRepository coeCeptMasterRepository;
	@Autowired private CoeCeptTempRepository coeCeptTempRepository;
	@Autowired
	Bins104CustomRepository bins104CustomRepository;
	
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
		Boolean isBusErr = (Boolean) this.stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IS_BUS_ERROR);
		isBusErr = isBusErr != null && isBusErr;		
		
		if (isBusErr) {
			this.stepExecution.getJobExecution().setExitStatus(ExitStatus.FAILED);
			return RepeatStatus.FINISHED;
		}		
		bins104CustomRepository.deletePkgSpecData(compCode);
		List<CoeCeptTempEntity> coeCeptTempEntityList = coeCeptTempRepository.findAll();		
		List<CoeCeptMasterEntity> coeCeptMasterEntityList = new ArrayList<>();
		for(CoeCeptTempEntity tempEntity : coeCeptTempEntityList) {

			CoeCeptMasterEntity coeCeptMasterEntity = new CoeCeptMasterEntity();

			BeanUtils.copyProperties(tempEntity, coeCeptMasterEntity);

			coeCeptMasterEntityList.add(coeCeptMasterEntity);
		}
		coeCeptMasterRepository.saveAll(coeCeptMasterEntityList);		
		
		this.stepExecution.getJobExecution().setExitStatus(ExitStatus.COMPLETED);
		return RepeatStatus.FINISHED;
	}

}
