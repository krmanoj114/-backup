package com.tpex.batchjob.binf005.chunks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.tpex.batchjob.common.configuration.model.LineError;
import com.tpex.batchjob.common.configuration.model.LineTokenConfig;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.entity.VprContTempEntity;
import com.tpex.entity.VprDlyVinTempEntity;
import com.tpex.entity.VprModuleTempEntity;
import com.tpex.entity.VprPartTempEntity;
import com.tpex.repository.Binf005CustomRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.VprContTempRepository;
import com.tpex.repository.VprDlyVinTempRepository;
import com.tpex.repository.VprModuleTempRepository;
import com.tpex.repository.VprPartTempRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.InterfaceFileUtils;
import com.tpex.util.ConstantUtils.Binf005;
import com.tpex.util.ProcessLogDtlsUtil;

public class Binf005LinesWriter implements ItemWriter<LineData>, ItemWriteListener<LineData>, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(Binf005LinesWriter.class);

    @Autowired private VprContTempRepository vprContTempRepository;
    @Autowired private VprModuleTempRepository vprModuleTempRepository;
    @Autowired private VprPartTempRepository vprPartTempRepository;
    @Autowired private VprDlyVinTempRepository vprDlyVinTempRepository;
    @Autowired private Binf005CustomRepository binf005CustomRepository;
    @Autowired private ProcessLogDtlsRepository processLogDtlsRepository;
    @Autowired private ProcessLogDtlsUtil processLogDtlsUtil;
    
    private StepExecution stepExecution;
    private LocalDateTime executeDate;
    private String mstrMaintFlg;
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
        String dt = (String) getExecutionContext().get(GlobalConstants.EXECUTE_DATETIME);
        this.executeDate = InterfaceFileUtils.convertStringToLocalDateTime(dt, GlobalConstants.DATETIME_FORMAT);
        this.mstrMaintFlg = (String) getExecutionContext().get(GlobalConstants.MAINTENENCE_FLAG);
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
				saveError(lineData.getFileLevelErrors(), lineData.getLineTokens(), String.valueOf(lineData.getLineNo()));
				return;
			}
			if(!lineData.getBusinessErrors().isEmpty()) {
				putBusErrorIfHas(lineData);
				saveError(lineData.getBusinessErrors(), lineData.getLineTokens(), String.valueOf(lineData.getLineNo()));
			}
			insertD1(lineData);
			insertD2(lineData);
			insertD3(lineData);
			insertD4(lineData);
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
	
	private void saveError(Map<String, List<LineError>> mapErr, List<LineTokenConfig> lineTokens, String lineNo) {
		mapErr.entrySet().forEach(e -> e.getValue().forEach(l -> {
			ProcessLogDtlsEntity ent = new ProcessLogDtlsEntity();
			ent.setProcessControlId(getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
			ent.setProcessId(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
			ent.setProcessName(this.stepExecution.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
			ent.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());
			ent.setId(processLogDtlsUtil.getNextProcessLogId());
			ent.setColumnName(lineTokens.isEmpty() ? null : lineTokens.get(Integer.parseInt(e.getKey())).getName());
			ent.setLineNo(lineNo);
			ent.setProcessLogStatus(l.getType());
			ent.setProcessLogMessage(l.getMessage());
			ent.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
			ent.setCompanyCode(compCode);
			processLogDtlsRepository.save(ent);
		}));
	}

	private void insertD1(LineData lineDataInfo) {
		String[] insD1 = lineDataInfo.getLineTokenText();
		if (!Binf005.D1.equals(insD1[0])) {
			return;
		}
		
		VprContTempEntity cont = new VprContTempEntity();
		cont.setContDestinationCode(insD1[3] + insD1[4]);
		cont.setRecvPlant(insD1[5]);
		cont.setContSno(InterfaceFileUtils.getContSNo(insD1[6]));
		cont.setVanPlantCode(insD1[7]);
		cont.setVanPlantName(insD1[8]);
		cont.setIsoContNo(StringUtils.rightPad(insD1[9], 11, " "));
		cont.setSealNo(insD1[10]);
		cont.setPlanVanStartDate(InterfaceFileUtils.convertStringToLocalDate(insD1[11], GlobalConstants.YEAR_MONTH_DATE));
		cont.setPlanVanStartTime(insD1[12]);
		cont.setActVanDate(InterfaceFileUtils.convertStringToLocalDate(insD1[15], GlobalConstants.YEAR_MONTH_DATE));
		cont.setActVanTime(insD1[16]);
		cont.setVanLn(insD1[17]);
		cont.setVanSeqNo(Integer.parseInt(insD1[18]));
		cont.setPacTbl1(Integer.parseInt(insD1[19]));
		cont.setVanGroupCode(insD1[20]);						
		cont.setVanGroupSeq(Integer.parseInt(insD1[21]));
		cont.setTrns(insD1[22]);
		cont.setContType(insD1[23]);
		cont.setContSize(insD1[24]);
		cont.setContGroupCode(insD1[25]);
		cont.setContNetWeight(new BigDecimal(insD1[26]));
		cont.setContGrossWeight(new BigDecimal(insD1[27]));
		cont.setContTareWeight(Integer.parseInt(insD1[28]));
		cont.setContMaxWeight(Integer.parseInt(insD1[29]));
		cont.setContMaxCofWeight(insD1[30]);
		cont.setContMaxM3(Integer.parseInt(insD1[31]));
		cont.setContMaxCofM3(insD1[32]);
		cont.setXdocFlag(insD1[33]);
		cont.setDepPort(insD1[36]);
		cont.setDstPort(insD1[37]);
		cont.setVsslSrl1(insD1[38]);
		cont.setVsslName1(insD1[39]);
		cont.setShipName1(insD1[40]);
		cont.setVoy1(insD1[41]);
		cont.setEtd1(InterfaceFileUtils.convertStringToLocalDate(insD1[42], GlobalConstants.YEAR_MONTH_DATE));
		cont.setEta1(InterfaceFileUtils.convertStringToLocalDate(insD1[43], GlobalConstants.YEAR_MONTH_DATE));
		cont.setVsslName2(insD1[45]);
		cont.setShipName2(insD1[46]);
		cont.setVoy2(insD1[47]);
		cont.setEtd2(InterfaceFileUtils.convertStringToLocalDate(insD1[48], GlobalConstants.YEAR_MONTH_DATE));
		cont.setEta2(InterfaceFileUtils.convertStringToLocalDate(insD1[49], GlobalConstants.YEAR_MONTH_DATE));
		cont.setProcDt(executeDate);
		cont.setIntrId(GlobalConstants.BINF005_INTR_ID);
		cont.setMainFlag(mstrMaintFlg);
		cont.setUpdateBy(ConstantUtils.UPD_BY_SCHEDULE);
		cont.setUpdateDate(LocalDateTime.now());
		cont.setPlanVanEndDate(InterfaceFileUtils.convertStringToLocalDate(insD1[13], GlobalConstants.YEAR_MONTH_DATE));
		cont.setPlanVanEndTime(insD1[14]);
		cont.setCompCode(compCode);
		
		vprContTempRepository.save(cont);
	}
	
	private void insertD2(LineData lineDataInfo) {
		String[] arrayD1 = (String[]) getExecutionContext().get(GlobalConstants.D1_LINETOKENTEXT);
		String[] insD2 = lineDataInfo.getLineTokenText();
		if (!Binf005.D2.equals(insD2[0]) || "S".equals(insD2[81]) || arrayD1 == null) {
			// skip for Space Reserve
			return;
		}
		 
		VprModuleTempEntity mod = new VprModuleTempEntity();
		mod.setModDstCd(insD2[1] + insD2[2]);
		mod.setModRecvPlnt(insD2[3]);
		mod.setModImpDoc(insD2[4]);
		mod.setLotModNo(insD2[5]);
		mod.setCaseNo(StringUtils.isBlank(insD2[6]) ? GlobalConstants.DUMMY_CASE_NO : insD2[6]);
		mod.setExpCd(insD2[7]);
		mod.setExpCompNm(insD2[9]);
		mod.setImpCompNm(insD2[11]);
		mod.setCfCd(insD2[12]);
		mod.setImpLnCd(StringUtils.isBlank(insD2[13]) ? "1" : insD2[13]);
		mod.setRexpCd(insD2[14]);
		mod.setAicoFlg(insD2[15]);
		mod.setOrdTyp(StringUtils.isBlank(insD2[16]) ? "R" : insD2[16]);
		mod.setPkgMth(insD2[17]);
		mod.setSupPlntCd(insD2[18]);
		mod.setSupDocCd(insD2[19]);
		mod.setBunMod(insD2[20]);
		mod.setBunFlg(insD2[21]);
		mod.setPckPlnt(insD2[22]);
		mod.setPckPlntNm(insD2[23]);
		mod.setOrdTy(insD2[24]);
		mod.setSsNo(insD2[25]);
		mod.setExtClrCd(insD2[26]);
		mod.setIntClrCd(insD2[27]);
		mod.setOrdSrc(insD2[28]);
		mod.setContLoadSeq1(Integer.parseInt(insD2[29]));
		mod.setContLoadSeq2(Integer.parseInt(insD2[30]));
		mod.setContLoadSeq3(Integer.parseInt(insD2[31]));
		mod.setContLoadSeq4(Integer.parseInt(insD2[32]));
		mod.setContLoadSeq5(Integer.parseInt(insD2[33]));
		mod.setOrgVanDt(InterfaceFileUtils.convertStringToLocalDate(insD2[34], GlobalConstants.YEAR_MONTH_DATE));
		mod.setPlnPckDt(InterfaceFileUtils.convertStringToLocalDate(insD2[35], GlobalConstants.YEAR_MONTH_DATE));
		mod.setPlnPckTm(insD2[36]);
		mod.setActPckDt(InterfaceFileUtils.convertStringToLocalDate(insD2[37], GlobalConstants.YEAR_MONTH_DATE));
		mod.setActPckTm(insD2[38]);
		mod.setPckLnCd(insD2[39]);
		mod.setPckSeqNo(Integer.parseInt(insD2[40]));
		mod.setModStdTm(Integer.parseInt(insD2[41]));
		mod.setModTy(insD2[42]);
		mod.setModSzCd(insD2[43]);
		mod.setModGrpNm(insD2[44]);
		mod.setModNetWt(new BigDecimal(insD2[45]));
		mod.setModGrossWt(new BigDecimal(insD2[46]));
		mod.setModTareWt(new BigDecimal(insD2[47]));
		mod.setModOutM3(new BigDecimal(insD2[48]).setScale(3, RoundingMode.HALF_UP));
		mod.setModInM3(new BigDecimal(insD2[49]).setScale(3, RoundingMode.HALF_UP));
		mod.setModLn(Integer.parseInt(insD2[50]));
		mod.setModWh(Integer.parseInt(insD2[51]));
		mod.setModHt(Integer.parseInt(insD2[52]));
		mod.setLoadStl(insD2[53]);
		mod.setModCom1(insD2[54]);
		mod.setModCom2(insD2[55]);
		mod.setDispModCd(insD2[56]);
		mod.setCtrlModCd(insD2[57]);
		mod.setSrsNm(insD2[58]);
		mod.setRrTy(insD2[59]);
		mod.setUnitLot(Integer.parseInt(insD2[60]));
		mod.setUnitCase(Integer.parseInt(insD2[61]));
		mod.setAccPartM3(new BigDecimal(insD2[62]));
		mod.setModEffM3(insD2[63]);
		mod.setModMaxWt(new BigDecimal(insD2[64]));
		mod.setModEffWt(insD2[65]);
		mod.setModM3Cof(Integer.parseInt(insD2[66]));
		mod.setBarFlg(insD2[67]);
		mod.setPacTbl1(Integer.parseInt(insD2[68]));
		mod.setPacTbl2(Integer.parseInt(insD2[69]));
		mod.setPacTbl3(Integer.parseInt(insD2[70]));
		mod.setPacTbl4(Integer.parseInt(insD2[71]));
		mod.setPacTbl5(Integer.parseInt(insD2[72]));
		mod.setPacTbl6(Integer.parseInt(insD2[73]));
		mod.setPacTbl6Dt(InterfaceFileUtils.convertStringToLocalDate(insD2[74], GlobalConstants.YEAR_MONTH_DATE));
		mod.setPacTbl7(Integer.parseInt(insD2[75]));
		mod.setPacTbl7Dt(InterfaceFileUtils.convertStringToLocalDate(insD2[76], GlobalConstants.YEAR_MONTH_DATE));
		mod.setPacTbl7Tm(insD2[77]);
		mod.setEkanOrdSrt(insD2[78]);
		mod.setEkanOrdEnd(insD2[79]);
		mod.setStsFlgPln(insD2[80]);
		mod.setSpcRsvFlg(insD2[81]);
		mod.setDstNm(insD2[82]);
		mod.setModMatCd(insD2[83]);
		mod.setModPartNo(insD2[84]);
		mod.setModMatDocCd(insD2[85]);
		mod.setModMatAdd(insD2[86]);
		mod.setModMatSrcCd(insD2[87]);
		mod.setModMatQty(Integer.parseInt(insD2[88]));
		mod.setModLoadGrp(insD2[89]);
		mod.setModKitCd(insD2[90]);
		mod.setModKitSrNo(insD2[91]);
		mod.setModPloadGrp(insD2[92]);
		mod.setProcDt(executeDate);
		mod.setIntrId(GlobalConstants.BINF005_INTR_ID);
		mod.setMainFlg(mstrMaintFlg);
		mod.setContDstCd(arrayD1[3] + arrayD1[4]);
		mod.setContSno(InterfaceFileUtils.getContSNo(arrayD1[6]));
		mod.setUpdBy(ConstantUtils.UPD_BY_SCHEDULE);
		mod.setUpdDt(LocalDateTime.now());
		mod.setLotPttrn(insD2[93]);
		mod.setCompCode(compCode);
		
		vprModuleTempRepository.save(mod);
	}
	
	private void insertD3(LineData lineDataInfo) {
		String[] arrayD1 = (String[]) getExecutionContext().get(GlobalConstants.D1_LINETOKENTEXT);
		String[] arrayD2 = (String[]) getExecutionContext().get(GlobalConstants.D2_LINETOKENTEXT);
		String[] insD3 = lineDataInfo.getLineTokenText();
		if (!Binf005.D3.equals(insD3[0]) || arrayD1 == null || arrayD2 == null) {
			return;
		}
		
		VprPartTempEntity part = new VprPartTempEntity();
		part.setBoxSeqNo(insD3[1]);
		part.setBoxNo(insD3[2]);
		part.setPartNo(insD3[3]);
		part.setPckQtyBox(Integer.parseInt(insD3[4]));
		part.setKanNo(insD3[5]);
		part.setPartNm1(StringUtils.isBlank(insD3[6]) ? " " : insD3[6]);
		part.setPartNm2(insD3[7]);
		part.setPckAreaCd(insD3[8]);
		part.setStkAreaCd(insD3[9]);
		part.setPckLoc(insD3[10]);
		part.setBoxGrossWt(BigDecimal.valueOf(Integer.parseInt(insD3[11])/1000.0f).setScale(3, RoundingMode.HALF_UP));
		part.setBoxGrossM3(new BigDecimal(insD3[12]).setScale(3, RoundingMode.HALF_UP));
		part.setBoxMatCd(insD3[14]);
		part.setBoxMatNo(insD3[15]);
		part.setBoxMatDocCd(insD3[16]);
		part.setBoxMatAdd(insD3[17]);
		part.setBoxMatSrcCd(insD3[18]);
		part.setBoxQty(Integer.parseInt(insD3[19]));
		part.setPartMatCd1(insD3[20]);
		part.setPartMatNo1(insD3[21]);
		part.setPartMatDocCd1(insD3[22]);
		part.setPartMatAdd1(insD3[23]);
		part.setPartMatSrcCd1(insD3[24]);
		part.setPartQty1(Integer.parseInt(insD3[25]));
		part.setPartMatCd2(insD3[26]);
		part.setPartMatNo2(insD3[27]);
		part.setPartMatDocCd2(insD3[28]);
		part.setPartMatAdd2(insD3[29]);
		part.setPartMatSrcCd2(insD3[30]);
		part.setPartQty2(Integer.parseInt(insD3[31]));
		part.setPartMatCd3(insD3[32]);
		part.setPartMatNo3(insD3[33]);
		part.setPartMatDocCd3(insD3[34]);
		part.setPartMatAdd3(insD3[35]);
		part.setPartMatSrcCd3(insD3[36]);
		part.setPartQty3(Integer.parseInt(insD3[37]));
		part.setPartMatCd4(insD3[38]);
		part.setPartMatNo4(insD3[39]);
		part.setPartMatDocCd4(insD3[40]);
		part.setPartMatAdd4(insD3[41]);
		part.setPartMatSrcCd4(insD3[42]);
		part.setPartQty4(Integer.parseInt(insD3[43]));
		part.setPartMatCd5(insD3[44]);
		part.setPartMatNo5(insD3[45]);
		part.setPartMatDocCd5(insD3[46]);
		part.setPartMatAdd5(insD3[47]);
		part.setPartMatSrcCd5(insD3[48]);
		part.setPartQty5(Integer.parseInt(insD3[49]));
		part.setOrdTy(insD3[50]);
		part.setOrdImpCd(insD3[51]);
		part.setOrdExpCd(insD3[52]);
		part.setOrdMspTy(StringUtils.isBlank(insD3[53]) ? "R" : insD3[53]);
		part.setOrdPckMth(StringUtils.isBlank(insD3[54]) ? ConstantUtils.SPACE : insD3[54]); // prevent not null
		part.setOrdCfCd(StringUtils.isBlank(insD3[55]) ? ConstantUtils.SPACE : insD3[55]); // prevent not null
		part.setOrdRexpCd(StringUtils.isBlank(insD3[56]) ? ConstantUtils.SPACE : insD3[56]); // prevent not null
		part.setOrdAicoFlg(insD3[57]);
		part.setOrdPartNo(insD3[58]);
		part.setOrdLot(StringUtils.isBlank(insD3[59]) ? null : Integer.parseInt(insD3[59])); 
		part.setOrdImpLn(StringUtils.isBlank(insD3[60]) ? "1" : insD3[60]);
		part.setOrdSetPartFlg(insD3[61]);				
		part.setOrdSrsNm(insD3[62]);
		part.setOrdSupCd(insD3[63]);				
		part.setOrdSupPlnt(insD3[64]);
		part.setOrdSupDock(insD3[65]);
		part.setArvDtTm(insD3[66]);
		part.setOrdMros(insD3[67]);
		part.setOrdMrosDt(insD3[68]);
		part.setOrdMrosGrp(insD3[69]);
		part.setXdockArvDtTm(insD3[70]);
		part.setOrdRlsDt(InterfaceFileUtils.convertStringToLocalDate(insD3[71], GlobalConstants.YEAR_MONTH_DATE));
		part.setOrdRlsTm(insD3[72]);
		part.setOrdBoFlg(insD3[73]);
		part.setOrdBoQty(Integer.parseInt(insD3[74]));
		part.setShpDtTm(StringUtils.isBlank(insD3[75]) ? null : Long.parseLong(insD3[75]));
		part.setFryDtTm(StringUtils.isBlank(insD3[76]) ? null : Long.parseLong(insD3[76]));
		part.setOrdQty(Integer.parseInt(insD3[77]));
		part.setOrdQtyLot(Integer.parseInt(insD3[78]));
		part.setKanPrntAdd(insD3[79]);
		part.setEkanOrdNo(insD3[80]);
		part.setOrdEkanNo(insD3[81]);
		part.setQtyPerCont(Integer.parseInt(insD3[82]));				
		part.setOrdMode(insD3[83]);
		part.setConvRt(insD3[84]);
		part.setDngPartDst(insD3[85]);
		part.setDngPartImo(insD3[86]);
		part.setDngPartCls(insD3[87]);
		part.setDngPartLvl(insD3[88]);
		part.setDngPartLvlCd(insD3[89]);
		part.setPartOpCtrl(insD3[90]);
		part.setImpInfo1(insD3[91]);
		part.setImpInfo2(insD3[92]);
		part.setImpInfo3(insD3[93]);
		part.setImpInfo4(insD3[94]);
		part.setPacTbl1(Integer.parseInt(insD3[95]));
		part.setPartKitCd(insD3[104]);
		part.setPartKitSrlNo(insD3[105]);
		part.setPartKitM3(new BigDecimal(insD3[106]));
		part.setPartNetWt(BigDecimal.valueOf(Double.parseDouble(insD3[107])/1000.0f).setScale(5, RoundingMode.HALF_UP));
		part.setPartOrdVanDt(InterfaceFileUtils.convertStringToLocalDate(insD3[108], GlobalConstants.YEAR_MONTH_DATE));
		part.setPartOrdRenban(insD3[109]);
		part.setProcDt(executeDate);
		part.setIntrId(GlobalConstants.BINF005_INTR_ID);
		part.setMainFlg(mstrMaintFlg);
		part.setContDstCd(arrayD1[3] + arrayD1[4]);
		part.setContSno(InterfaceFileUtils.getContSNo(arrayD1[6]));
		part.setModDstCd(arrayD2[1] + arrayD2[2]);
		part.setLotModNo(arrayD2[5]);
		part.setCaseNo(StringUtils.isBlank(arrayD2[6]) ? GlobalConstants.DUMMY_CASE_NO : arrayD2[6]);
		part.setUpdDt(executeDate);
		part.setUpdBy(ConstantUtils.UPD_BY_SCHEDULE);
		part.setInvAicoFlg(binf005CustomRepository.getLocalPrivilegeCode(insD3[57]));
		part.setCompCode(compCode);
		
		vprPartTempRepository.save(part);
	}
	
	private void insertD4(LineData lineDataInfo) {
		String[] insD4 = lineDataInfo.getLineTokenText();
		if (!Binf005.D4.equals(insD4[0])) {
			return;
		}
		
		VprDlyVinTempEntity vin = new VprDlyVinTempEntity();
		vin.setProcDt(executeDate);
		vin.setIntrId(GlobalConstants.BINF005_INTR_ID);
		vin.setExpCode(insD4[1]);
		vin.setImpCode(insD4[2]);
		vin.setLotCode(insD4[3]);
		vin.setVinTy(insD4[5]);
		vin.setVinWmi(insD4[6]);
		vin.setVinVds(insD4[7]);
		vin.setVinModYr(insD4[8]);
		vin.setVinChkDgt(insD4[9]);
		vin.setVinFrmNo(insD4[10]);
		vin.setVinEngNo(insD4[11]);
		vin.setVinEngPfx(insD4[12]);
		vin.setCompCode(compCode);

		vprDlyVinTempRepository.save(vin);
	}
	
	private ExecutionContext getExecutionContext() {
		return this.stepExecution.getJobExecution().getExecutionContext();
	}
}