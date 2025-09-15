package com.tpex.batchjob.binf301.tasklets;

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

import com.tpex.entity.EngVinMasterEntity;
import com.tpex.entity.EngVinMasterTempEntity;
import com.tpex.repository.Binf301CustomRepository;
import com.tpex.repository.EngVinMstRepository;
import com.tpex.repository.EngVinMstTempRepository;
import com.tpex.util.GlobalConstants;

public class Binf301ProcessMain implements Tasklet, StepExecutionListener {
	
	@Autowired
	Binf301CustomRepository binf301CustomRepository;
	
	@Autowired
	EngVinMstTempRepository engVinMstTempRepository;
	
	@Autowired
	EngVinMstRepository engVinMstRepository;

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
				
		binf301CustomRepository.deleteEngMstData(compCode);
		
		List<EngVinMasterTempEntity> engVinMasterTempEntityList = engVinMstTempRepository.findAll();		
		List<EngVinMasterEntity> engVinMasterEntityList = new ArrayList<>();
		for(EngVinMasterTempEntity tempEntity : engVinMasterTempEntityList) {

			EngVinMasterEntity engVinMasterEntity = new EngVinMasterEntity();

			BeanUtils.copyProperties(tempEntity, engVinMasterEntity);

			engVinMasterEntityList.add(engVinMasterEntity);
		}
		engVinMstRepository.saveAll(engVinMasterEntityList);
		
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
