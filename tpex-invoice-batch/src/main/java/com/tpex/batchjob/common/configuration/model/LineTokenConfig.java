package com.tpex.batchjob.common.configuration.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class LineTokenConfig implements Serializable  {
	
	private static final long serialVersionUID = -8275290573644629350L;
	
	private String name;
	private String desc;
	private String dataType;
	
	private Integer length;
	private Integer startDigit;
	private Integer endDigit;
	
	private String mandatory;
	private String fixLength;
	private String needToSkip;
	
	private String format;
	private String fixValue;
	private String decimalPlaces;
	private String notTrim;

}
