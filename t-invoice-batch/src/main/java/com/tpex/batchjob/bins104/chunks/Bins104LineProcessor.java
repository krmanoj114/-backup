package com.tpex.batchjob.bins104.chunks;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.batchjob.common.configuration.model.LineData;
import com.tpex.batchjob.common.configuration.model.LineError;
import com.tpex.batchjob.common.receive.FlatInterfaceFileLineProcessor;
import com.tpex.entity.CoeCeptTempEntity;
import com.tpex.repository.Bins104CustomRepository;
import com.tpex.repository.CoeCeptTempRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.ConstantUtils.Bins104;
import com.tpex.util.GlobalConstants;
import com.tpex.util.InterfaceFileUtils;

public class Bins104LineProcessor extends FlatInterfaceFileLineProcessor {
	
	@Autowired
	private CoeCeptTempRepository coeCeptTempRepository;
	@Autowired
	Bins104CustomRepository bins104CustomRepository;

	private StepExecution stepExecution;	
	
	/** 
	 * Logger to log information. 
	 *
	 **/
	private final Logger logger = LoggerFactory.getLogger(Bins104LineProcessor.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		super.beforeStep(stepExecution);		
	}	

	/**
	 * Method to process line details.
	 * 
	 */
	public LineData processLine(LineData lineDataInfo) {
		logger.debug("Process :: {}", lineDataInfo);
		if (lineDataInfo.getLineContent().startsWith(ConstantUtils.BINS104_HEADER_START_WITH)) {
			return processLineDetails(lineDataInfo, 2);
		} else {
			return processLineDetails(lineDataInfo, 1);
		}
	}	

	@Override
	protected void validateLinesOrder(LineData currLineData) {
		String prevTag;
		String[] currLineToken = currLineData.getLineTokenText();
		String currTag = currLineToken[0];
		String[] prevLineToken = (String[]) getExecutionContext().get(GlobalConstants.PREV_LINE_TOKEN);
		if (prevLineToken != null && prevLineToken[0].startsWith(ConstantUtils.BINS104_HEADER_START_WITH)) {
			prevTag = prevLineToken[0] + prevLineToken[1];
		} else {
			prevTag = prevLineToken != null ? prevLineToken[0] : GlobalConstants.BLANK;
		}

		if (checkTag(currTag, prevTag, Bins104.HEAD, GlobalConstants.BLANK)
				|| checkTag(currTag, prevTag, Bins104.DATA, Bins104.HEAD, Bins104.DATA)
				|| checkTag(currTag, prevTag, Bins104.FOOT, Bins104.DATA)) {
			setError(currLineData, "0", new LineError(String.format(ConstantUtils.INVALID_TAG, currTag)));
		}

		getExecutionContext().put(GlobalConstants.PREV_LINE_TOKEN, currLineToken);
	}

	private boolean checkTag(String currTag, String prevTag, String expectCurrTag, String... expectPrevTag) {
		if (expectCurrTag.equals(currTag)) {
			for (int i = 0; i < expectPrevTag.length; i++) {
				if (expectPrevTag[i].equals(prevTag)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected void validateBusinessAndPrepareData(LineData lineDataInfo) {
		String tag = lineDataInfo.getLineTokenText()[0];
		String[] lineTokenText = lineDataInfo.getLineTokenText();		
		validateAndPreparePkgSpecData(lineDataInfo, tag, lineTokenText);
	}
	

	private void validateAndPreparePkgSpecData(LineData lineDataInfo, String tag, String[] lineTokenText) {
		if (Bins104.DATA.equals(tag)) {
			getExecutionContext().put(GlobalConstants.DATA_LINETOKENTEXT_BINS104, lineTokenText);			
			if ((InterfaceFileUtils.isDate(lineTokenText[8], GlobalConstants.YEAR_MONTH_DATE))
					&& (InterfaceFileUtils.isDate(lineTokenText[9], GlobalConstants.YEAR_MONTH_DATE))) {
			validateForDateFormat(lineDataInfo,lineTokenText);
			
			validateEffFromAndToDate(lineDataInfo,lineTokenText);			
		}
			validateForPrivilegeFlag(lineDataInfo,lineTokenText);
		}
	}

	private void validateForDateFormat(LineData lineDataInfo, String[] lineTokenText) {
		LocalDate effFromDate = InterfaceFileUtils.convertStringToLocalDate(lineTokenText[8],
				GlobalConstants.YEAR_MONTH_DATE);
		LocalDate effToDate = InterfaceFileUtils.convertStringToLocalDate(lineTokenText[9],
				GlobalConstants.YEAR_MONTH_DATE);
		if (effFromDate.compareTo(effToDate) > 0) {
			setBusinessError(lineDataInfo, "8", new LineError(ConstantUtils.EFFECT_FROM_EFFECTIVE_TO_DATE_INVALID));
		}
	}
	
	private void validateForPrivilegeFlag(LineData lineDataInfo, String[] lineTokenText) {		
			if (StringUtils.isNotBlank(lineTokenText[7]) && !bins104CustomRepository.getValidPrivilegeRange().contains(lineTokenText[7])) {
				setBusinessError(lineDataInfo, "7", new LineError(ConstantUtils.INVALID_PRIVILEGE_FLAG));
			}		

		if (StringUtils.isNotBlank(lineTokenText[7]) && lineTokenText[7].equals(ConstantUtils.ATIGA)) {
			if (lineTokenText[10] == null) {
				setBusinessError(lineDataInfo, "10", new LineError(ConstantUtils.HSCODE_BLANK));
			} else if (lineTokenText[11] == null) {
				setBusinessError(lineDataInfo, "11", new LineError(ConstantUtils.ORIGIN_CRITERIA_BLANK));
			}
		}
	}
	
	private void validateEffFromAndToDate(LineData lineDataInfo, String[] lineTokenText) {
		List<CoeCeptTempEntity> listEntity =coeCeptTempRepository.findByCfCdAndSeriesAndPartNoAndImpCmpAndExpCmpAndPartEffDt(lineTokenText[1], lineTokenText[2],
				lineTokenText[3], lineTokenText[5], lineTokenText[6],
				InterfaceFileUtils.convertStringToLocalDate(lineTokenText[8], GlobalConstants.YEAR_MONTH_DATE));
		
		if(!listEntity.isEmpty()) {
            validateOverlappedDate(lineDataInfo, lineTokenText, listEntity);
        }
	}

	private void validateOverlappedDate(LineData lineDataInfo, String[] lineTokenText,
			List<CoeCeptTempEntity> listEntity) {
		for (CoeCeptTempEntity e : listEntity) {
			LocalDate entityEffFromDate = null;
			LocalDate entityEffToDate = null;

			if (e.getPartEffDt() != null) {
				entityEffFromDate = e.getPartEffDt();
			}
			if (e.getPartExpDt() != null) {
				entityEffToDate = e.getPartExpDt();
			}
			extractedValidDate(lineDataInfo, lineTokenText, entityEffFromDate, entityEffToDate);
		}
	}

	private void extractedValidDate(LineData lineDataInfo, String[] lineTokenText, LocalDate entityEffFromDate,
			LocalDate entityEffToDate) {
		LocalDate effFromDate = InterfaceFileUtils.convertStringToLocalDate(lineTokenText[8],
				GlobalConstants.YEAR_MONTH_DATE);
		LocalDate effToDate = InterfaceFileUtils.convertStringToLocalDate(lineTokenText[9],
				GlobalConstants.YEAR_MONTH_DATE);

		if (effFromDate.isBefore(entityEffFromDate) && effToDate.isAfter(entityEffFromDate)
				|| effFromDate.isBefore(entityEffToDate) && effToDate.isAfter(entityEffToDate)
				|| effFromDate.isBefore(entityEffFromDate) && effToDate.isAfter(entityEffToDate)
				|| effFromDate.isAfter(entityEffFromDate) && effToDate.isBefore(entityEffToDate)
				|| effFromDate.isEqual(entityEffFromDate) && effToDate.isBefore(entityEffToDate)
				|| effFromDate.isEqual(entityEffFromDate) && effToDate.isEqual(entityEffToDate)) {
			setBusinessError(lineDataInfo, "8", new LineError(ConstantUtils.EFFECTIVE_FROM_TO_DATE_OVERLAP));
		}
	}	

	private ExecutionContext getExecutionContext() {
		return this.stepExecution.getJobExecution().getExecutionContext();
	}
}
