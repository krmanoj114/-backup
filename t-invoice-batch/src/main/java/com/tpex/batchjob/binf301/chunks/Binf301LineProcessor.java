package com.tpex.batchjob.binf301.chunks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

import com.tpex.batchjob.common.configuration.model.LineData;
import com.tpex.batchjob.common.configuration.model.LineError;
import com.tpex.batchjob.common.receive.FlatInterfaceFileLineProcessor;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.util.ConstantUtils;
import com.tpex.util.ConstantUtils.Binf301;
import com.tpex.util.GlobalConstants;

public class Binf301LineProcessor extends FlatInterfaceFileLineProcessor {

	private StepExecution stepExecution;

	/**
	 * Logger to log information.
	 *
	 **/
	private final Logger logger = LoggerFactory.getLogger(Binf301LineProcessor.class);

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
		return processLineDetails(lineDataInfo, 1);
	}

	@Override
	protected void validateLinesOrder(LineData currLineData) {
		String[] currLineToken = currLineData.getLineTokenText();
		String currTag = currLineToken[0];
		String[] prevLineToken = (String[]) getExecutionContext().get(GlobalConstants.PREV_LINE_TOKEN);
		String prevTag = prevLineToken != null ? prevLineToken[0] : GlobalConstants.BLANK;

		if (checkTag(currTag, prevTag, Binf301.HEAD, GlobalConstants.BLANK)
				|| checkTag(currTag, prevTag, Binf301.DATA, Binf301.HEAD, Binf301.DATA)
				|| checkTag(currTag, prevTag, Binf301.FOOT, Binf301.DATA)) {
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
		validateAndPrepareEngVinMstData(tag, lineTokenText);

		if (lineDataInfo.getFileLevelErrors().isEmpty()) {
			validateEngPartMst(lineDataInfo, tag, lineTokenText);
		}

	}

	private void validateEngPartMst(LineData lineDataInfo, String tag, String[] lineTokenText) {
		if (Binf301.DATA.equals(tag)) {
			String[] data = lineDataInfo.getLineTokenText();
			SimpleDateFormat dateFormat = new SimpleDateFormat(ConstantUtils.YYYYMMDD);
			try {
				Date effectiveTo = dateFormat.parse(data[7]);

				if (effectiveTo.before(new Date()))
					setBusinessError(lineDataInfo, ConstantUtils.SEVEN,
							new LineError(ConstantUtils.EFFECT_REC_NOT_FOUND));
			}

			catch (ParseException e) {
				throw new MyResourceNotFoundException("");
			}
		}
	}

	private void validateAndPrepareEngVinMstData(String tag, String[] lineTokenText) {
		if (Binf301.DATA.equals(tag)) {
			getExecutionContext().put(GlobalConstants.DATA_LINETOKENTEXT, lineTokenText);

		}
	}

	private ExecutionContext getExecutionContext() {
		return this.stepExecution.getJobExecution().getExecutionContext();
	}

}
