package com.tpex.batchjob.bins104.chunks;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;


import com.tpex.batchjob.common.configuration.model.LineData;
import com.tpex.entity.CoeCeptTempEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.CoeCeptTempRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.InterfaceFileUtils;
import com.tpex.util.ProcessLogDtlsUtil;
import com.tpex.util.ConstantUtils.Bins104;

public class Bins104LinesWriter implements ItemWriter<LineData>, ItemWriteListener<LineData>, StepExecutionListener {

	private final Logger logger = LoggerFactory.getLogger(Bins104LinesWriter.class);

	@Autowired
	private CoeCeptTempRepository coeCeptTempRepository;
	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;
	@Autowired
	private ProcessLogDtlsUtil processLogDtlsUtil;
	
	private StepExecution stepExecution;   
    private String compCode;
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("Line Writer initialized.");
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("Line Writer ended.");
        ExitStatus exitStatus = ExitStatus.COMPLETED;
        Boolean isError = (Boolean) getExecutionContext().get(GlobalConstants.IS_ERROR);
        if(isError != null && isError) {
        	exitStatus = new ExitStatus(GlobalConstants.DEL_TEMP);
        }
        
        return exitStatus;
    }
    
	@Override
	public void beforeWrite(List<? extends LineData> items) {
		this.compCode = (String) getExecutionContext().get(GlobalConstants.COMP_CODE);
	}

   	@Override
   	public void afterWrite(List<? extends LineData> items) {
   		// afterWrite
   	}

   	@Override
   	public void onWriteError(Exception exception, List<? extends LineData> items) {
   		// onWriteError
   	}
   	
   	@Override
	public void write(List<? extends LineData> items) throws Exception {
		for (LineData lineData : items) {
			if(!lineData.getFileLevelErrors().isEmpty()) {
				getExecutionContext().put(GlobalConstants.IS_ERROR, true);
				lineData.getFileLevelErrors().entrySet().forEach(e -> 
					e.getValue().forEach(l -> {
						ProcessLogDtlsEntity ent = new ProcessLogDtlsEntity();
						ent.setProcessControlId(getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
						ent.setProcessId(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
						ent.setProcessName(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
						ent.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
						ent.setColumnName(lineData.getLineTokens().get(Integer.parseInt(e.getKey())).getName());
						ent.setLineNo(String.valueOf(lineData.getLineNo()));
						ent.setProcessLogStatus(l.getType());
						ent.setProcessLogMessage(l.getMessage());
						ent.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
						processLogDtlsRepository.save(ent);
					})
				);
				return;
			}
			if(!lineData.getBusinessErrors().isEmpty()) {
				boolean isError = lineData.getBusinessErrors().entrySet().stream()
						.anyMatch(e -> e.getValue().stream().anyMatch(s -> s.getType().equals(ConstantUtils.PL_STATUS_ERROR)));
				
				Boolean isBusErr = (Boolean) this.stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IS_BUS_ERROR);
				isBusErr = isBusErr != null && isBusErr;
				if(Boolean.TRUE.equals(!isBusErr && isError)) {
					getExecutionContext().put(GlobalConstants.IS_BUS_ERROR, isError);
				}
				
				lineData.getBusinessErrors().entrySet().forEach(e -> 
					e.getValue().forEach(l -> {
						ProcessLogDtlsEntity ent = new ProcessLogDtlsEntity();
						ent.setProcessControlId(getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
						ent.setProcessId(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
						ent.setProcessName(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
						ent.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
						ent.setColumnName(lineData.getLineTokens().get(Integer.parseInt(e.getKey())).getName());
						ent.setLineNo(String.valueOf(lineData.getLineNo()));
						ent.setProcessLogStatus(l.getType());
						ent.setProcessLogMessage(l.getMessage());
						ent.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
						processLogDtlsRepository.save(ent);
					})
				);
			}
			insertData(lineData);
		}
	}
   	
   	private void insertData(LineData lineDataInfo) {
		String[] insData = lineDataInfo.getLineTokenText();
		if (!Bins104.DATA.equals(insData[0])) {
			return;
		}
		
		CoeCeptTempEntity cont = new CoeCeptTempEntity();
		cont.setCfCd(insData[1]);
		cont.setSeries(insData[2]);
		cont.setPartNo(insData[3]);			
		cont.setImpCmp(insData[5]);
		cont.setExpCmp(insData[6]);
		cont.setPartFlg(insData[7]);
		cont.setPartEffDt(InterfaceFileUtils.convertStringToLocalDate(insData[8], GlobalConstants.YEAR_MONTH_DATE));
		cont.setPartExpDt(InterfaceFileUtils.convertStringToLocalDate(insData[9], GlobalConstants.YEAR_MONTH_DATE));
		cont.setPartNm(insData[4]);
		cont.setUpdBy(ConstantUtils.UPD_BY_SCHEDULE);
		cont.setUpdDt(LocalDate.now());
		cont.setHsCd(insData[10]);
		cont.setOrgCriteria(insData[11]);
		cont.setCompCode(compCode);
		
		coeCeptTempRepository.save(cont);
   	}
   	
   	private ExecutionContext getExecutionContext() {
		return this.stepExecution.getJobExecution().getExecutionContext();
	}

}
