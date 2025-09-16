package com.tpex.batchjob.binf009.chunks;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

import com.tpex.batchjob.common.configuration.model.LineData;
import com.tpex.batchjob.common.configuration.model.LineError;
import com.tpex.batchjob.common.receive.FlatInterfaceFileLineProcessor;
import com.tpex.util.ConstantUtils;
import com.tpex.util.ConstantUtils.Binf009;
import com.tpex.util.GlobalConstants;

public class Binf009LineProcessor extends FlatInterfaceFileLineProcessor {

	private StepExecution stepExecution;

	/** 
	 * Logger to log information. 
	 *
	 **/
	private final Logger logger = LoggerFactory.getLogger(Binf009LineProcessor.class);

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
		return processLineDetails(lineDataInfo, 2);
	}

	@Override
	protected void validateLinesOrder(LineData currLineData) {
		String[] currLineToken = currLineData.getLineTokenText();
		String currTag = currLineToken[0];
		String[] prevLineToken = (String[]) getExecutionContext().get(GlobalConstants.PREV_LINE_TOKEN);
		String prevTag = prevLineToken != null ? prevLineToken[0] : GlobalConstants.BLANK;

		if (checkTag(currTag, prevTag, Binf009.GATEWAY_TAG, GlobalConstants.BLANK) 
				|| checkTag(currTag, prevTag, Binf009.HEAD, Binf009.GATEWAY_TAG)
				|| checkTag(currTag, prevTag, Binf009.DATA, Binf009.HEAD, Binf009.DATA)
				|| checkTag(currTag, prevTag, Binf009.FOOT, Binf009.DATA)) {
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
		validateAndPreparePkgSpecData(lineDataInfo, tag, lineTokenText);
	}

	private void validateAndPreparePkgSpecData(LineData lineDataInfo, String tag, String[] lineTokenText) {
		if (Binf009.DATA.equals(tag)) {

			getExecutionContext().put(GlobalConstants.DATA_LINETOKENTEXT, lineTokenText);

			validateForNumericCheck(lineDataInfo,lineTokenText);
		}
	}

	private void validateForNumericCheck(LineData lineDataInfo, String[] lineTokenText) {
        
		if(lineTokenText[19]!=null && (!NumberUtils.isParsable(lineTokenText[19]) || Integer.parseInt(lineTokenText[19]) <=0 || lineTokenText[19].length() > 5)) {
			setError(lineDataInfo, "19", new LineError(ConstantUtils.PKG_QTY_BOX_INVALID));
		}

		if(lineTokenText[20]!=null && (!NumberUtils.isParsable(lineTokenText[20]) || Integer.parseInt(lineTokenText[20]) <=0 || lineTokenText[20].length() > 8)) {
			setError(lineDataInfo, "20", new LineError(ConstantUtils.BOX_GROSS_WT_INVALID_QTY));
		}

		if(lineTokenText[21]!=null && (!NumberUtils.isParsable(lineTokenText[21]) || Integer.parseInt(lineTokenText[21]) <=0 || lineTokenText[21].length() > 6)) {
			setError(lineDataInfo, "21", new LineError(ConstantUtils.BOX_M3_INVALID_QTY));
		}

		if(lineTokenText[30]!=null && (!NumberUtils.isParsable(lineTokenText[30]) || Integer.parseInt(lineTokenText[30]) <=0 || lineTokenText[30].length() > 5)) {
			setError(lineDataInfo, "30", new LineError(ConstantUtils.BOX_MTRL_INVALID_QTY));
		}

		validatePartMtrlQty1(lineDataInfo, lineTokenText);
	}

	private void validatePartMtrlQty1(LineData lineDataInfo, String[] lineTokenText) {

		if(lineTokenText[36]!=null && (!NumberUtils.isParsable(lineTokenText[36]) || Integer.parseInt(lineTokenText[36]) <=0 || lineTokenText[36].length() > 5)) {
			setError(lineDataInfo, "36", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.ONE  +ConstantUtils.INVALID_QTY));
		}

		if(lineTokenText[42]!=null && (!NumberUtils.isParsable(lineTokenText[42]) || Integer.parseInt(lineTokenText[42]) <=0 || lineTokenText[42].length() > 5)) {
			setError(lineDataInfo, "42", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.TWO +ConstantUtils.INVALID_QTY));
		}

		if(lineTokenText[48]!=null && (!NumberUtils.isParsable(lineTokenText[48]) || Integer.parseInt(lineTokenText[48]) <=0 || lineTokenText[48].length() > 5)) {
			setError(lineDataInfo, "48", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.THREE +ConstantUtils.INVALID_QTY));
		}

		if(lineTokenText[54]!=null && (!NumberUtils.isParsable(lineTokenText[54]) || Integer.parseInt(lineTokenText[54]) <=0 || lineTokenText[54].length() > 5)) {
			setError(lineDataInfo, "54", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.FOUR +ConstantUtils.INVALID_QTY));
		}

		if(lineTokenText[60]!=null && (!NumberUtils.isParsable(lineTokenText[60]) || Integer.parseInt(lineTokenText[60]) <=0 || lineTokenText[60].length() > 5)) {
			setError(lineDataInfo, "60", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.FIVE +ConstantUtils.INVALID_QTY));
		}

		validatePartMtrlQty2(lineDataInfo, lineTokenText);
	}

	private void validatePartMtrlQty2(LineData lineDataInfo, String[] lineTokenText) {

		if(lineTokenText[66]!=null && (!NumberUtils.isParsable(lineTokenText[66]) || Integer.parseInt(lineTokenText[66]) <=0 || lineTokenText[66].length() > 5)) {
			setError(lineDataInfo, "66", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.SIX +ConstantUtils.INVALID_QTY));
		}

		if(lineTokenText[72]!=null && (!NumberUtils.isParsable(lineTokenText[72]) || Integer.parseInt(lineTokenText[72]) <=0 || lineTokenText[72].length() > 5)) {
			setError(lineDataInfo, "72", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.SEVEN +ConstantUtils.INVALID_QTY));
		}

		if(lineTokenText[78]!=null && (!NumberUtils.isParsable(lineTokenText[78]) || Integer.parseInt(lineTokenText[78]) <=0 || lineTokenText[78].length() > 5)) {
			setError(lineDataInfo, "78", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.EIGHT +ConstantUtils.INVALID_QTY));
		}

		if(lineTokenText[84]!=null && (!NumberUtils.isParsable(lineTokenText[84]) || Integer.parseInt(lineTokenText[84]) <=0 || lineTokenText[84].length() > 5)) {
			setError(lineDataInfo, "84", new LineError(ConstantUtils.PART_MTRL_QTY+ ConstantUtils.NINE +ConstantUtils.INVALID_QTY));
		}
	}

	private ExecutionContext getExecutionContext() {
		return this.stepExecution.getJobExecution().getExecutionContext();
	}

}