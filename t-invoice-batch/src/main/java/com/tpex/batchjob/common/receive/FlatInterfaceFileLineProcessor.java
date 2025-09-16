package com.tpex.batchjob.common.receive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

import com.tpex.batchjob.common.configuration.model.LineConfig;
import com.tpex.batchjob.common.configuration.model.LineData;
import com.tpex.batchjob.common.configuration.model.LineError;
import com.tpex.batchjob.common.configuration.model.LineTokenConfig;
import com.tpex.util.GlobalConstants;
import com.tpex.util.InterfaceFileUtils;

public abstract class FlatInterfaceFileLineProcessor implements ItemProcessor <LineData, LineData>, StepExecutionListener {

	/**
	 * Map to store the information of different line types in interface file.
	 * 
	 * 
	 */
    private Map <String, LineConfig> linesInfo;
    /**
.    * Method to process any information before step starts.
     * 
     */
	@Override
    @SuppressWarnings("unchecked")
    public void beforeStep(StepExecution stepExecution) {
    	linesInfo = (Map <String, LineConfig>) stepExecution.getJobExecution().getExecutionContext().get(GlobalConstants.IF_LINES_INFO);
    }

    @Override
    public LineData process(LineData lineDataInfo) throws Exception {
    	return processLine(lineDataInfo);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
    	return ExitStatus.COMPLETED;
    }
    
    public abstract LineData processLine(LineData lineDataInfo);

    protected LineData processLineDetails(LineData lineDataInfo, int recordIdentifierTextLength) {

    	//Blank line
		if (isBlank(lineDataInfo)) {
			return lineDataInfo;
		}
    	
    	//Incomplete line
    	if (lineDataInfo.getLineContent().length() < recordIdentifierTextLength) {
    		setError(lineDataInfo, "0", new LineError(String.format(ERR_MSG_FORMAT, "Incomplete line, line no.", lineDataInfo.getLineNo(), GlobalConstants.BLANK, GlobalConstants.BLANK)));
    		return lineDataInfo;
    	}

    	LineConfig lineInfo = linesInfo.get(lineDataInfo.getLineContent().substring(0, recordIdentifierTextLength));
    	
    	if (lineDataInfo.getLineContent().length() < lineInfo.getRecordLength()) {
    		setError(lineDataInfo, "0", new LineError(String.format(ERR_MSG_FORMAT, "Invalid record length, line no.", lineDataInfo.getLineNo(), GlobalConstants.BLANK, GlobalConstants.BLANK)));
    		return lineDataInfo;
    	}

    	//Split line to tokens
    	splitLineToTokens(lineInfo, lineDataInfo);
    	
    	//validate line
    	validateLinesOrder(lineDataInfo);
    	
    	validateBusinessAndPrepareData(lineDataInfo);
    	
    	return lineDataInfo;
    }
    
    protected boolean isBlank(LineData lineDataInfo) {
    	return lineDataInfo == null || lineDataInfo.getLineContent() == null; 
    }
    
    private void splitLineToTokens(LineConfig lineInfo, LineData lineDataInfo) {

    	List <LineTokenConfig> lineTokens = lineInfo.getTokens();

    	String [] tokens = new String[lineTokens.size()];

    	for (int i = 0; i < lineTokens.size(); i++) {

    		String columnNo   = (i + 0) + GlobalConstants.BLANK;
    		
    		int startIndex = lineTokens.get(i).getStartDigit() - 1; 
    		int endIndex   = lineTokens.get(i).getEndDigit(); 

    		String token = lineDataInfo.getLineContent().substring(startIndex, endIndex);
    		token = "Y".equals(lineTokens.get(i).getNotTrim()) ? token : InterfaceFileUtils.rtrim(token);
    		token = StringUtils.isBlank(token) ? null : token; 
    		
    		tokens[i] = token;
    		
    		String tokenValidationInfo = validateToken(token, lineTokens.get(i));
    		
    		if (StringUtils.isNotBlank(tokenValidationInfo)) {
    			setError(lineDataInfo, columnNo, new LineError(tokenValidationInfo));
    		}    		
    	}
    	
    	lineDataInfo.setLineTokens(lineTokens);
    	lineDataInfo.setLineTokenText(tokens);
    }
    
    protected void setError(LineData lineDataInfo, String columnNo, LineError errorInfo) {
    	
    	List <LineError> errors;
    	
    	if (lineDataInfo.getFileLevelErrors().containsKey(columnNo)) {
        	errors = lineDataInfo.getFileLevelErrors().get(columnNo);
    	} else {
    		errors = new ArrayList <> ();
    	}

    	errors.add(errorInfo);
    	
    	lineDataInfo.getFileLevelErrors().put(columnNo, errors);
    	
    	lineDataInfo.setFileLevelErrorFlag(GlobalConstants.FLAG_Y);
    }
    
    protected void setBusinessError(LineData lineDataInfo, String columnNo, LineError errorInfo) {
    	
    	List <LineError> errors;
    	
    	if (lineDataInfo.getBusinessErrors().containsKey(columnNo)) {
    		errors = lineDataInfo.getBusinessErrors().get(columnNo);
    	} else {
    		errors = new ArrayList <> ();
    	}
    	
    	errors.add(errorInfo);
    	
    	lineDataInfo.getBusinessErrors().put(columnNo, errors);
    	
    	lineDataInfo.setBusinessErrorFlag(GlobalConstants.FLAG_Y);
    }
    
    private static final String ERR_MSG_FORMAT = "%s %s %s %s";
    
    private String validateToken(String columnContent, LineTokenConfig tokenInfo) {
    	String ret = null;
    	// Mandatory
		if (GlobalConstants.FLAG_Y.equals(tokenInfo.getMandatory()) && StringUtils.isBlank(columnContent)) {
			ret = String.format(ERR_MSG_FORMAT, tokenInfo.getName(), "cannot be blank.", GlobalConstants.BLANK, GlobalConstants.BLANK);
		}
		// Date + Mandatory
		else if (GlobalConstants.DATE_TYPE.equals(tokenInfo.getDataType()) && GlobalConstants.FLAG_Y.equals(tokenInfo.getMandatory())
				&& !InterfaceFileUtils.isDate(columnContent, tokenInfo.getFormat())) {
			ret = String.format(ERR_MSG_FORMAT, tokenInfo.getName(), "Date format is Invalid, Follow ", tokenInfo.getFormat(), "format.");
		}
		// Number + Mandatory
		else if (GlobalConstants.NUMBER_TYPE.equals(tokenInfo.getDataType()) && GlobalConstants.FLAG_Y.equals(tokenInfo.getMandatory())
				&& !NumberUtils.isParsable(columnContent)) {
			ret = String.format(ERR_MSG_FORMAT, tokenInfo.getName(), "is not Numeric", GlobalConstants.BLANK, GlobalConstants.BLANK);
		}
		// Date
		else if (GlobalConstants.DATE_TYPE.equals(tokenInfo.getDataType()) && StringUtils.isNotBlank(columnContent)
				&& !InterfaceFileUtils.isDate(columnContent, tokenInfo.getFormat())) {
			ret = String.format(ERR_MSG_FORMAT, tokenInfo.getName(), "Date format is Invalid, Follow ", tokenInfo.getFormat(), "format.");
		}
		// Number
		else if (GlobalConstants.NUMBER_TYPE.equals(tokenInfo.getDataType()) && StringUtils.isNotBlank(columnContent)
				&& !NumberUtils.isParsable(columnContent)) {
			ret = String.format(ERR_MSG_FORMAT, tokenInfo.getName(), "is not Numeric", GlobalConstants.BLANK, GlobalConstants.BLANK);
		}
		//Length 
    	else if(StringUtils.isNotBlank(columnContent) && columnContent.length()>tokenInfo.getLength()) {
    		ret = String.format(ERR_MSG_FORMAT, tokenInfo.getName(), "Length is more than limit.", GlobalConstants.BLANK, GlobalConstants.BLANK);
    	}
    	return ret;    	
    }
    

    /**
     * Implement method to validate line order.
     * 
     * @return
     */
    protected abstract void validateLinesOrder(LineData lineTokenText);
    
    protected abstract void validateBusinessAndPrepareData(LineData lineTokenText);
    
}