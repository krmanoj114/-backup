package com.tpex.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportTypeDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String reportTypeId;
	private String reportTypeName;
	private Boolean isPdf;
	private Boolean isExcel;
}