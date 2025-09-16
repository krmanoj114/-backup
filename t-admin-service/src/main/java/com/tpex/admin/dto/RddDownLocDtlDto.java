package com.tpex.admin.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RddDownLocDtlDto implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private int reportId;

	private String reportName;

	private String status;

	private String downLoc;

	private String createBy;

	private String createDate;

	private String updateBy;

	private Timestamp updateDate;

	private String companyCode;

}
