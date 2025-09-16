package com.tpex.admin.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReportNameDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer reportId;
	private String reportName;
	private String reportJsonPath;
	private String excelName;
	private String excelHeaderNames;
	private String userId;
	

}
