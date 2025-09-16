package com.tpex.batchjob.binf005.chunks;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.batchjob.common.configuration.model.LineData;
import com.tpex.batchjob.common.configuration.model.LineError;
import com.tpex.batchjob.common.configuration.model.LineTokenConfig;
import com.tpex.batchjob.common.receive.FlatInterfaceFileLineProcessor;
import com.tpex.dto.InterfaceSetupDTO;
import com.tpex.entity.OemParameterEntity;
import com.tpex.repository.Binf005CustomRepository;
import com.tpex.repository.OemParameterRepository;
import com.tpex.util.ConstantUtils.Binf005;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.InterfaceFileUtils;

public class Binf005LineProcessor extends FlatInterfaceFileLineProcessor {

	@Autowired private Binf005CustomRepository binf005CustomRepository;
	@Autowired private OemParameterRepository oemParameterRepository;
	
	private StepExecution stepExecution;
	private InterfaceSetupDTO infSetup;
	
	private static final String XDOC = "Y";
	private static final String DIRECT = "N";
	private static final String PXP = "P";
	private static final String CKD = "L";
	private static final String MBP = "B";
	private static final String GPAC_CKD = "1";
	private static final String GPAC_MBP = "2";
	
	private String mstrPartSeriesName = GlobalConstants.BLANK;
	private String mstrLastPartCarFamily = GlobalConstants.BLANK;
	private String mstrLastModuleDest = GlobalConstants.BLANK;
	private String mstrLastReExpCd = GlobalConstants.BLANK;
	
    /** 
     * Logger to log information. 
     *
     **/
    private final Logger logger = LoggerFactory.getLogger(Binf005LineProcessor.class);

    @Override
	public void beforeStep(StepExecution stepExecution) {
    	this.stepExecution = stepExecution;
    	infSetup = binf005CustomRepository.querySetupByBatchId(GlobalConstants.BINF005_PROGRAM_ID);
    	resetValue();
		super.beforeStep(stepExecution);
	}

	private void resetValue() {
		mstrPartSeriesName = GlobalConstants.BLANK;
    	mstrLastPartCarFamily = GlobalConstants.BLANK;
    	mstrLastModuleDest = GlobalConstants.BLANK;
    	mstrLastReExpCd = GlobalConstants.BLANK;
    	String dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern(GlobalConstants.DATETIME_FORMAT));
    	getExecutionContext().put(GlobalConstants.EXECUTE_DATETIME, dt);
	}
    
    /**
     * Method to process line details.
     * 
     */
    public LineData processLine(LineData lineDataInfo) {
    	logger.debug("Process :: {}", lineDataInfo);
    	return processLineDetails(lineDataInfo, 2);
    }

	@Override
	protected void validateLinesOrder(LineData currLineData) {
		String[] currLineToken = currLineData.getLineTokenText();
		String currTag = currLineToken[0];
		String[] prevLineToken = (String[]) getExecutionContext().get(GlobalConstants.PREV_LINE_TOKEN);
		String prevTag = prevLineToken != null ? prevLineToken[0] : GlobalConstants.BLANK;
		
		if (checkTag(currTag, prevTag, Binf005.GATEWAY_TAG, GlobalConstants.BLANK) 
				|| checkTag(currTag, prevTag, Binf005.HEAD, Binf005.GATEWAY_TAG)
				|| checkTag(currTag, prevTag, Binf005.H1, Binf005.HEAD)
				|| checkTag(currTag, prevTag, Binf005.D1, Binf005.H1)
				|| checkTag(currTag, prevTag, Binf005.H2, Binf005.D1)
				|| checkTag(currTag, prevTag, Binf005.D2, Binf005.H2, Binf005.T3)
				|| checkTag(currTag, prevTag, Binf005.H3, Binf005.D2)
				|| checkTag(currTag, prevTag, Binf005.D3, Binf005.H3, Binf005.D3)
				|| checkTag(currTag, prevTag, Binf005.T3, Binf005.D3, Binf005.H3)
				|| checkTag(currTag, prevTag, Binf005.T2, Binf005.T3)
				|| checkTag(currTag, prevTag, Binf005.T1, Binf005.T2)
				|| checkTag(currTag, prevTag, Binf005.H4, Binf005.T1)
				|| checkTag(currTag, prevTag, Binf005.D4, Binf005.H4, Binf005.D4)
				|| checkTag(currTag, prevTag, Binf005.T4, Binf005.D4, Binf005.H4)
				|| checkTag(currTag, prevTag, Binf005.FOOT, Binf005.T4)) {
			setError(currLineData, "0", new LineError(String.format(ConstantUtils.INVALID_TAG, currTag)));
		}
		
		getExecutionContext().put(GlobalConstants.PREV_LINE_TOKEN, currLineToken);
	}

	private boolean checkTag(String currTag, String prevTag, String expectCurrTag, String... expectPrevTag) {
		if(expectCurrTag.equals(currTag)) {
			for(int i=0; i < expectPrevTag.length; i++) {
				if(expectPrevTag[i].equals(prevTag)) {
					return false;
				}
			}
			// no any previous expected tag same as previous tag
			return true;
		}
		// ignore if current expected tag not same as current tag
		return false;
	}

	@Override
	protected void validateBusinessAndPrepareData(LineData lineDataInfo) {
		String tag = lineDataInfo.getLineTokenText()[0];
		String[] lineTokenText = lineDataInfo.getLineTokenText();
		validateHeaderFileName(lineDataInfo);
		validateHeaderCont(lineDataInfo, tag);
		if(lineDataInfo.getFileLevelErrors().isEmpty()) {
			validateAndPrepareD1(lineDataInfo, tag, lineTokenText);
			validateAndPrepareD2(lineDataInfo, tag, lineTokenText);
			validateAndPrepareD3(lineDataInfo, tag, lineTokenText);
			validateAndPrepareD4(lineDataInfo, tag, lineTokenText);
		}
	}

	private void validateAndPrepareD4(LineData lineDataInfo, String tag, String[] arrayD4) {
		if (Binf005.D4.equals(tag) && StringUtils.length(arrayD4[7]) < 6 && StringUtils.isBlank(arrayD4[9])) {
			setBusinessError(lineDataInfo, "7", new LineError(ConstantUtils.INVALID_VDS_LEN));
		}
	}

	private void validateAndPrepareD3(LineData lineDataInfo, String tag, String[] arrayD3) {
		if(Binf005.D3.equals(tag)) {
			String[] arrayD2 = (String[]) getExecutionContext().get(GlobalConstants.D2_LINETOKENTEXT);
			
			List<LineTokenConfig> listTokenConfig = lineDataInfo.getLineTokens();
            if (arrayD2 != null && (CKD.equals(arrayD2[93]) || MBP.equals(arrayD2[93]))) {
				arrayD3[53] = arrayD2[16];
				arrayD3[54] = arrayD2[17];
				arrayD3[55] = arrayD2[12];
				arrayD3[56] = arrayD2[14];
				arrayD3[59] = arrayD2[60];
				arrayD3[60] = arrayD2[13];
				arrayD3[62] = arrayD2[58];
			}
			
			BigDecimal dblVal = InterfaceFileUtils.getBigDecimal(arrayD3[12], Integer.parseInt(listTokenConfig.get(12).getDecimalPlaces()));
			arrayD3[12] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(arrayD3[106], Integer.parseInt(listTokenConfig.get(106).getDecimalPlaces()));
			arrayD3[106] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(arrayD3[107], Integer.parseInt(listTokenConfig.get(107).getDecimalPlaces()));
			arrayD3[107] = dblVal + GlobalConstants.BLANK;
			
			validateD3PartSeriesName(lineDataInfo, arrayD3, arrayD2);
			
			if(!binf005CustomRepository.getValidPrivilegeRange().contains(arrayD3[57])){
				setBusinessError(lineDataInfo, "57", new LineError(ConstantUtils.INVALID_PRIVILEGE_FLAG));
			}
		}
	}

	private void validateAndPrepareD2(LineData lineDataInfo, String tag, String[] lineTokenText) {
		if(Binf005.D2.equals(tag)) {
			getExecutionContext().put(GlobalConstants.D2_LINETOKENTEXT, lineTokenText);
			
			validateLotPatternD2(lineDataInfo, lineTokenText);
			
			if("S".equals(lineTokenText[81])) {
				// skip for Space Reserve
				return;
			}
			List<LineTokenConfig> listTokenConfig = lineDataInfo.getLineTokens();
			
			// If Importer Line Code is Blank default 1
			lineTokenText[13] = StringUtils.isBlank(lineTokenText[13]) ? "1" : lineTokenText[13];
			// If MSP Order Type is Blank USe R = Regular
			lineTokenText[16] = StringUtils.isBlank(lineTokenText[16]) ? "R" : lineTokenText[16];
			
			BigDecimal dblVal = InterfaceFileUtils.getBigDecimal(lineTokenText[45], Integer.parseInt(listTokenConfig.get(45).getDecimalPlaces()));
			lineTokenText[45] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(lineTokenText[46], Integer.parseInt(listTokenConfig.get(46).getDecimalPlaces()));
			lineTokenText[46] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(lineTokenText[47], Integer.parseInt(listTokenConfig.get(47).getDecimalPlaces()));
			lineTokenText[47] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(lineTokenText[48], Integer.parseInt(listTokenConfig.get(48).getDecimalPlaces()));
			lineTokenText[48] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(lineTokenText[49], Integer.parseInt(listTokenConfig.get(49).getDecimalPlaces()));
			lineTokenText[49] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(lineTokenText[62], Integer.parseInt(listTokenConfig.get(62).getDecimalPlaces()));
			lineTokenText[62] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(lineTokenText[64], Integer.parseInt(listTokenConfig.get(64).getDecimalPlaces()));
			lineTokenText[64] = dblVal + GlobalConstants.BLANK;
		}
	}

	private void validateAndPrepareD1(LineData lineDataInfo, String tag, String[] arrayD1) {
		if (Binf005.D1.equals(tag)) {
			getExecutionContext().put(GlobalConstants.D1_LINETOKENTEXT, arrayD1);
			
			List<LineTokenConfig> listTokenConfig = lineDataInfo.getLineTokens();
			// Check for operating Exporter Company Code
			if (!isValidCompCode(arrayD1[1])) {
				setBusinessError(lineDataInfo, "1", new LineError(ConstantUtils.INCORRECT_OPR_EXP_COMP));
			}
			// Check for Vanning Exporter Company Code
			if (!isValidCompCode(arrayD1[2])) {
				setBusinessError(lineDataInfo, "2", new LineError(ConstantUtils.INCORRECT_VAN_EXP_COMP));
			}
			
			BigDecimal dblVal = InterfaceFileUtils.getBigDecimal(arrayD1[26], Integer.parseInt(listTokenConfig.get(26).getDecimalPlaces()));
			arrayD1[26] = dblVal + GlobalConstants.BLANK;
			dblVal = InterfaceFileUtils.getBigDecimal(arrayD1[27], Integer.parseInt(listTokenConfig.get(27).getDecimalPlaces()));
			arrayD1[27] = dblVal + GlobalConstants.BLANK;
			
			Optional<OemParameterEntity> optXdoc = oemParameterRepository.findByOprParaCd(ConstantUtils.X_DOC_CODE);
			if (optXdoc.isPresent() && optXdoc.get().getOprParaVal().equals(arrayD1[3] + arrayD1[4])) {
				arrayD1[33] = XDOC;
			} else {
				arrayD1[33] = DIRECT;
			}
			
			if(StringUtils.isNotBlank(arrayD1[60])) {
				arrayD1[45] = arrayD1[57]; //Vessel Name4
				arrayD1[46] = arrayD1[58]; //Ship. Comp. Name
				arrayD1[47] = arrayD1[59]; //Voyage#4
				arrayD1[48] = arrayD1[60]; //ETD4
				arrayD1[49] = arrayD1[61]; //ETA4
				arrayD1[50] = GlobalConstants.BLANK;
			} else if(StringUtils.isNotBlank(arrayD1[51])) {
				arrayD1[45] = arrayD1[51]; //Vessel Name3
				arrayD1[46] = arrayD1[52]; //Ship. Comp. Name
				arrayD1[47] = arrayD1[53]; //Voyage#3
				arrayD1[48] = arrayD1[54]; //ETD3
				arrayD1[49] = arrayD1[55]; //ETA3
				arrayD1[50] = arrayD1[56]; //Trans-shipment port3
			}
			
			validateISOContD1(lineDataInfo, arrayD1);
		}
	}
	
	private void validateISOContD1(LineData lineDataInfo, String[] arrayD1) {
		String isoContNoCB = binf005CustomRepository.getIsoContNo(arrayD1[13], arrayD1[3] + arrayD1[4], arrayD1[42],
				InterfaceFileUtils.getContSNo(arrayD1[6]));
		
		String typeErrISO = GlobalConstants.BLANK;
				
		if (StringUtils.isBlank(isoContNoCB)) {
			setBusinessError(lineDataInfo, "6", new LineError(String.format(ConstantUtils.ISO_CONT_NOT_FOUND,
					arrayD1[3] + arrayD1[4], arrayD1[6], arrayD1[9])));
		} else {
			typeErrISO = checkIsoContErrWarn(arrayD1, isoContNoCB, typeErrISO);
			if (StringUtils.isNotBlank(typeErrISO)) {
				setBusinessError(lineDataInfo, "6", new LineError(String.format(ConstantUtils.ISO_CONT_NOT_SAME,
						arrayD1[3] + arrayD1[4], arrayD1[6], arrayD1[9]), typeErrISO));
			}
		}
	}

	private String checkIsoContErrWarn(String[] arrayD1, String isoContNoCB, String typeErrISO) {
		if (isoContNoCB.length() >= 5 && isoContNoCB.length() <= 9) {
			// CB 5-9 digits CB must contain in GPAC
			if (arrayD1[9].contains(isoContNoCB)) {
				typeErrISO = ConstantUtils.PL_STATUS_WARNING;
				arrayD1[9] = isoContNoCB;
			} else {
				typeErrISO = ConstantUtils.PL_STATUS_ERROR;
			}
		} else if (isoContNoCB.length() == 11 && arrayD1[9].length() == 11) {
			// CB, GPAC 11 digits ISO Cont no must exact same
			if (!isoContNoCB.equals(arrayD1[9])) {
				typeErrISO = ConstantUtils.PL_STATUS_ERROR;
			}
		} else if (isoContNoCB.length() >= 10 && isoContNoCB.length() <= 15) {
			// CB 10-15 digits GPAC must contain in CB
			if (isoContNoCB.contains(arrayD1[9])) {
				typeErrISO = ConstantUtils.PL_STATUS_WARNING;
				arrayD1[9] = isoContNoCB;
			} else {
				typeErrISO = ConstantUtils.PL_STATUS_ERROR;
			}
		}
		return typeErrISO;
	}
	
	private void validateLotPatternD2(LineData lineDataInfo, String[] arrayD2) {
		if(StringUtils.isBlank(arrayD2[93])) {
			arrayD2[93] = PXP;
		} else if (GPAC_CKD.equals(arrayD2[93])) {
			arrayD2[93] = CKD;
		} else if (GPAC_MBP.equals(arrayD2[93])) {
			arrayD2[93] = MBP;
		} else {
			setBusinessError(lineDataInfo, "93", new LineError(ConstantUtils.INVALID_LOT_PATTERN));
		}
	}
	
	private void validateD3PartSeriesName(LineData lineDataInfo, String[] arrayD3, String[] arrayD2) {
		if (arrayD2 != null && StringUtils.isBlank(arrayD3[62])) {
			
			arrayD3[55] = arrayD3[55] == null ? GlobalConstants.BLANK : arrayD3[55];
			
			if ((!mstrLastPartCarFamily.equals(arrayD3[55]))
					|| (!mstrLastModuleDest.equals(arrayD2[1] + arrayD2[2]))
					|| (!mstrLastReExpCd.equals(arrayD2[14]))) {

				mstrLastReExpCd = arrayD2[14];
				mstrLastModuleDest = arrayD2[1] + arrayD2[2];
				mstrLastPartCarFamily = arrayD3[55];
				
				mstrPartSeriesName = binf005CustomRepository.getPartSeriesName(mstrLastPartCarFamily, mstrLastModuleDest, mstrLastReExpCd);

				if (StringUtils.isBlank(mstrPartSeriesName)) {
					setBusinessError(lineDataInfo, "62", new LineError(ConstantUtils.PART_SNM_NOT_EXIST));
				}

			}
			arrayD3[62] = mstrPartSeriesName;
		}
	}
	
	private boolean isValidCompCode(String compCode) {
		return compCode.equals(infSetup.getCmpCd());
	}
	
	private void validateHeaderCont(LineData lineDataInfo, String tag) {
		if(Binf005.H1.equals(tag)) {
			String mstrMaintFlg = lineDataInfo.getLineTokenText()[1];
			getExecutionContext().put(GlobalConstants.MAINTENENCE_FLAG, mstrMaintFlg);
			if (!"R".equals(mstrMaintFlg)) {
				setError(lineDataInfo, "1", new LineError(ConstantUtils.INCORRECT_MAINT_FLG));
			}
		}
	}

	private void validateHeaderFileName(LineData lineDataInfo) {
		if(lineDataInfo.getLineNo() == 1) {
			String headerFileName = lineDataInfo.getLineTokenText()[4];
			String fileNm = infSetup.getFileNm();
			if (!headerFileName.equals(fileNm)) {
				setError(lineDataInfo, "4", new LineError(String.format(ConstantUtils.INCORRECT_FNM_HEADER, fileNm, headerFileName)));
			}
		}
	}

	private ExecutionContext getExecutionContext() {
		return this.stepExecution.getJobExecution().getExecutionContext();
	}
	
}