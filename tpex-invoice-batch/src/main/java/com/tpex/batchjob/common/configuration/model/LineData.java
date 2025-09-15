package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class LineData implements Serializable {

	private static final long serialVersionUID = 5790033848385818157L;

	private Integer lineNo;

	private Integer recordLength;
	
	private String lineContent;
	
	private String [] lineTokenText;
	
	private List<LineTokenConfig> lineTokens = new ArrayList<>();

	private Map<String, List<LineError>> fileLevelErrors = new HashMap<>();

	private Map<String, List<LineError>> businessErrors = new HashMap<>();
	
	private String fileLevelErrorFlag;
	
	private String businessErrorFlag;

}
