package com.tpex.batchjob.binf301.chunks;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import com.tpex.entity.EngVinMasterIdEntity;
import com.tpex.entity.EngVinMasterTempEntity;
import com.tpex.entity.OemParameterEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.repository.EngVinMstTempRepository;
import com.tpex.repository.OemParameterRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.ConstantUtils.Binf301;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

public class Binf301LinesWriter implements ItemWriter<LineData>, ItemWriteListener<LineData>, StepExecutionListener {

	private final Logger logger = LoggerFactory.getLogger(Binf301LinesWriter.class);

	@Autowired
	private EngVinMstTempRepository engVinMstTempRepository;

	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;

	@Autowired
	private ProcessLogDtlsUtil processLogDtlsUtil;

	@Autowired
	private OemParameterRepository oemParameterRepository;

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
		if (isError != null && isError) {
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
			if (!lineData.getFileLevelErrors().isEmpty()) {
				getExecutionContext().put(GlobalConstants.IS_ERROR, true);
				saveFileLevelError(lineData);
				return;
			}
			if (!lineData.getBusinessErrors().isEmpty()) {
				putBusErrorIfHas(lineData);
				saveBusLevelError(lineData);
			}

			insertData(lineData);

		}
	}

	private void putBusErrorIfHas(LineData lineData) {
		boolean isError = lineData.getBusinessErrors().entrySet().stream()
				.anyMatch(e -> e.getValue().stream().anyMatch(s -> s.getType().equals(ConstantUtils.PL_STATUS_ERROR)));

		Boolean isBusErr = (Boolean) this.stepExecution.getJobExecution().getExecutionContext()
				.get(GlobalConstants.IS_BUS_ERROR);
		isBusErr = isBusErr != null && isBusErr;
		if (Boolean.TRUE.equals(!isBusErr && isError)) {
			getExecutionContext().put(GlobalConstants.IS_BUS_ERROR, isError);
		}
	}

	private void saveBusLevelError(LineData lineData) {
		lineData.getBusinessErrors().entrySet().forEach(e -> e.getValue().forEach(l -> {
			ProcessLogDtlsEntity ent = new ProcessLogDtlsEntity();
			ent.setProcessControlId(getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
			ent.setProcessId(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
			ent.setProcessName(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
			ent.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
			ent.setColumnName(lineData.getLineTokens().isEmpty() ? null
					: lineData.getLineTokens().get(Integer.parseInt(e.getKey())).getName());
			ent.setLineNo(String.valueOf(lineData.getLineNo()));
			ent.setProcessLogStatus(l.getType());
			ent.setProcessLogMessage(l.getMessage());
			ent.setProcessLogDatetime(Timestamp.from(Instant.now()));
			ent.setCompanyCode(compCode);
			processLogDtlsRepository.save(ent);
		}));
	}

	private void saveFileLevelError(LineData lineData) {
		lineData.getFileLevelErrors().entrySet().forEach(e -> e.getValue().forEach(l -> {
			ProcessLogDtlsEntity ent = new ProcessLogDtlsEntity();
			ent.setProcessControlId(getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
			ent.setProcessId(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
			ent.setProcessName(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
			ent.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
			ent.setColumnName(lineData.getLineTokens().isEmpty() ? null
					: lineData.getLineTokens().get(Integer.parseInt(e.getKey())).getName());
			ent.setLineNo(String.valueOf(lineData.getLineNo()));
			ent.setProcessLogStatus(l.getType());
			ent.setProcessLogMessage(l.getMessage());
			ent.setProcessLogDatetime(Timestamp.from(Instant.now()));
			ent.setCompanyCode(compCode);
			processLogDtlsRepository.save(ent);
		}));
	}

	private void insertData(LineData lineDataInfo) {
		String[] data = lineDataInfo.getLineTokenText();
		if (!Binf301.DATA.equals(data[0])) {
			return;
		}
		EngVinMasterTempEntity entity = new EngVinMasterTempEntity();
		EngVinMasterIdEntity id = new EngVinMasterIdEntity();

		Optional<OemParameterEntity> paraValue = oemParameterRepository.findByOprParaCd(ConstantUtils.ENG_PLT_CD);

		String expCode = "ESTM";
		if (paraValue.isPresent()) {
			expCode = paraValue.get().getOprParaVal();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(ConstantUtils.YYYYMMDD);
		try {
			Date effectiveTo = dateFormat.parse(data[7]);

				if (effectiveTo.after(new Date())) {

				id.setImpCode(data[1]);
				id.setPartNo(data[2]);
				id.setCfCd(data[3]);
				id.setExpCode(expCode);
				id.setModLotCode(data[10]);

				entity.setCompanyCode("STM");
				entity.setQty(data[9]);
				entity.setEngFlag(data[11]);
				entity.setUpdateBy(ConstantUtils.UPD_BY_SCHEDULE);
				entity.setUpdateDate(LocalDate.now());
				entity.setId(id);

				engVinMstTempRepository.save(entity);
			}
		} catch (ParseException e) {
			throw new MyResourceNotFoundException("");
		}
	}

	private ExecutionContext getExecutionContext() {
		return this.stepExecution.getJobExecution().getExecutionContext();
	}
}