package com.tpex.batchjob.binf009.chunks;

import java.sql.Timestamp;
import java.time.Instant;
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
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.entity.VprPkgSpecTempEntity;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.VprPkgSpecTempRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.ConstantUtils.Binf009;
import com.tpex.util.GlobalConstants;
import com.tpex.util.InterfaceFileUtils;
import com.tpex.util.ProcessLogDtlsUtil;

public class Binf009LinesWriter implements ItemWriter<LineData>, ItemWriteListener<LineData>, StepExecutionListener {

	private final Logger logger = LoggerFactory.getLogger(Binf009LinesWriter.class);

	@Autowired 
	private VprPkgSpecTempRepository vprPkgSpecTempRepository;

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
				saveFileLevelError(lineData);
				return;
			}
			if(!lineData.getBusinessErrors().isEmpty()) {
				putBusErrorIfHas(lineData);
				saveBusLevelError(lineData);
			}

			insertData(lineData);

		}
	}

	private void putBusErrorIfHas(LineData lineData) {
		boolean isError = lineData.getBusinessErrors().entrySet().stream()
				.anyMatch(e -> e.getValue().stream().anyMatch(s -> s.getType().equals(ConstantUtils.PL_STATUS_ERROR)));

		Boolean isBusErr = (Boolean) this.stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IS_BUS_ERROR);
		isBusErr = isBusErr != null && isBusErr;
		if(Boolean.TRUE.equals(!isBusErr && isError)) {
			getExecutionContext().put(GlobalConstants.IS_BUS_ERROR, isError);
		}
	}

	private void saveBusLevelError(LineData lineData) {
		lineData.getBusinessErrors().entrySet().forEach(e -> 
		e.getValue().forEach(l -> {
			ProcessLogDtlsEntity ent = new ProcessLogDtlsEntity();
			ent.setProcessControlId(getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
			ent.setProcessId(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
			ent.setProcessName(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
			ent.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
			ent.setColumnName(lineData.getLineTokens().isEmpty() ? null : lineData.getLineTokens().get(Integer.parseInt(e.getKey())).getName());
			ent.setLineNo(String.valueOf(lineData.getLineNo()));
			ent.setProcessLogStatus(l.getType());
			ent.setProcessLogMessage(l.getMessage());
			ent.setProcessLogDatetime(Timestamp.from(Instant.now()));
			ent.setCompanyCode(compCode);
			processLogDtlsRepository.save(ent);
		})
				);
	}

	private void saveFileLevelError(LineData lineData) {
		lineData.getFileLevelErrors().entrySet().forEach(e -> 
		e.getValue().forEach(l -> {
			ProcessLogDtlsEntity ent = new ProcessLogDtlsEntity();
			ent.setProcessControlId(getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
			ent.setProcessId(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
			ent.setProcessName(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
			ent.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
			ent.setColumnName(lineData.getLineTokens().isEmpty() ? null : lineData.getLineTokens().get(Integer.parseInt(e.getKey())).getName());
			ent.setLineNo(String.valueOf(lineData.getLineNo()));
			ent.setProcessLogStatus(l.getType());
			ent.setProcessLogMessage(l.getMessage());
			ent.setProcessLogDatetime(Timestamp.from(Instant.now()));
			ent.setCompanyCode(compCode);
			processLogDtlsRepository.save(ent);
		})
				);
	}



	private void insertData(LineData lineDataInfo) {
		String[] data = lineDataInfo.getLineTokenText();
		if (!Binf009.DATA.equals(data[0])) {
			return;
		}

		VprPkgSpecTempEntity entity = new VprPkgSpecTempEntity();
		entity.setNvpsModExpCd(data[1]);
		entity.setNvpsSupPlntCd(data[2]);
		entity.setNvpsSupDocCd(data[3]);
		entity.setNvpsModImpCd(data[4]);
		entity.setNvpsModRecvPlnt(data[5]);
		entity.setNvpsPartNo(data[6]);
		entity.setNvpsCfCd(data[7]);
		entity.setNvpsImpLnCd(data[8]);
		entity.setNvpsRexpCd(data[9]);
		entity.setNvpsLotCd(data[10]);
		entity.setNvpsCaseNo(data[11]);
		entity.setNvpsModCd(data[12]);
		entity.setNvpsOrdTy(data[13]);
		entity.setNvpsBoxNo(data[14]);
		entity.setNvpsEffFrom(InterfaceFileUtils.convertStringToLocalDate(data[15], GlobalConstants.YEAR_MONTH_DATE));
		entity.setNvpsAicoFlg(data[16]);
		entity.setNvpsEffTo(InterfaceFileUtils.convertStringToLocalDate(data[17], GlobalConstants.YEAR_MONTH_DATE));
		entity.setNvpsPckPlnt(data[18]);
		entity.setNvpsQtyBox(Integer.parseInt(data[19]));
		entity.setNvpsBoxGrossWt(Integer.parseInt(data[20]));
		entity.setNvpsBoxM3(Integer.parseInt(data[21]));
		entity.setNvpsImpDocCd(data[22]);
		entity.setNvpsPrtNm2(data[23]);
		entity.setNvpsAdd(data[24]);
		entity.setNvpsBoxMatCd(data[25]);
		entity.setNvpsMatPrtNo(data[26]);
		entity.setNvpsMatSrcCd(data[27]);
		entity.setNvpsMatDocCd(data[28]);
		entity.setNvpsMatAdd(data[29]);
		entity.setNvpsBoxMatQty(Integer.parseInt(data[30]));
		entity.setNvpsMatCd1(data[31]);
		entity.setNvpsMatPrtNo1(data[32]);
		entity.setNvpsMatSrcCd1(data[33]);
		entity.setNvpsMatDocCd1(data[34]);
		entity.setNvpsMatAdd1(data[35]);
		entity.setNvpsBoxMatQty1(Integer.parseInt(data[36]));
		entity.setNvpsMatCd2(data[37]);
		entity.setNvpsMatPrtNo2(data[38]);
		entity.setNvpsMatSrcCd2(data[39]);
		entity.setNvpsMatDocCd2(data[40]);
		entity.setNvpsMatAdd2(data[41]);
		entity.setNvpsBoxMatQty2(Integer.parseInt(data[42]));
		entity.setNvpsMatCd3(data[43]);
		entity.setNvpsMatPrtNo3(data[44]);
		entity.setNvpsMatSrcCd3(data[45]);
		entity.setNvpsMatDocCd3(data[46]);
		entity.setNvpsMatAdd3(data[47]);
		entity.setNvpsBoxMatQty3(Integer.parseInt(data[48]));
		entity.setNvpsMatCd4(data[49]);
		entity.setNvpsMatPrtNo4(data[50]);
		entity.setNvpsMatSrcCd4(data[51]);
		entity.setNvpsMatDocCd4(data[52]);
		entity.setNvpsMatAdd4(data[53]);
		entity.setNvpsMatQty4(Integer.parseInt(data[54]));
		entity.setNvpsMatCd5(data[55]);
		entity.setNvpsMatPrtNo5(data[56]);
		entity.setNvpsMatSrcCd5(data[57]);
		entity.setNvpsMatDocCd5(data[58]);
		entity.setNvpsMatAdd5(data[59]);
		entity.setNvpsMatQty5(Integer.parseInt(data[60]));
		entity.setNvpsMatCd6(data[61]);
		entity.setNvpsMatPrtNo6(data[62]);
		entity.setNvpsMatSrcCd6(data[63]);
		entity.setNvpsMatDocCd6(data[64]);
		entity.setNvpsMatAdd6(data[65]);
		entity.setNvpsMatQty6(Integer.parseInt(data[66]));
		entity.setNvpsMatCd7(data[67]);
		entity.setNvpsMatPrtNo7(data[68]);
		entity.setNvpsMatSrcCd7(data[69]);
		entity.setNvpsMatDocCd7(data[70]);
		entity.setNvpsMatAdd7(data[71]);
		entity.setNvpsMatQty7(Integer.parseInt(data[72]));
		entity.setNvpsMatCd8(data[73]);
		entity.setNvpsMatPrtNo8(data[74]);
		entity.setNvpsMatSrcCd8(data[75]);
		entity.setNvpsMatDocCd8(data[76]);
		entity.setNvpsMatAdd8(data[77]);
		entity.setNvpsMatQty8(Integer.parseInt(data[78]));
		entity.setNvpsMatCd9(data[79]);
		entity.setNvpsMatPrtNo9(data[80]);
		entity.setNvpsMatSrcCd9(data[81]);
		entity.setNvpsMatDocCd9(data[82]);
		entity.setNvpsMatAdd9(data[83]);
		entity.setNvpsMatQty9(Integer.parseInt(data[84]));
		entity.setNvpsLblOptCtl(data[85]);
		entity.setNvpsPckZoneCd(data[86]);
		entity.setNvpsStakZoneCd(data[87]);
		entity.setNvpsKbnNo(data[88]);
		entity.setNvpsTmcSepNo(data[89]);
		entity.setNvpsTmcBox(data[90]);
		entity.setNvpsTmcKumi(data[91]);
		entity.setNvpsTmcPat(data[92]);
		entity.setNvpsTmcVlic(data[93]);
		entity.setNvpsTmcContShiKub(data[94]);
		entity.setNvpsTmcBckKub(data[95]);
		entity.setNvpsTmcBunHak(data[96]);
		entity.setNvpsTmcKanCd(data[97]);
		entity.setNvpsTmcGenJyoBul(data[98]);
		entity.setNvpsUpdateBy(ConstantUtils.UPD_BY_SCHEDULE);
		entity.setNvpsUpdateDate(LocalDateTime.now());
		entity.setCompanyCode(compCode);

		vprPkgSpecTempRepository.save(entity);
	}

	private ExecutionContext getExecutionContext() {
		return this.stepExecution.getJobExecution().getExecutionContext();
	}
}