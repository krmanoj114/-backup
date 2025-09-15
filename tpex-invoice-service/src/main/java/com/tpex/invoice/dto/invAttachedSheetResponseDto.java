package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class invAttachedSheetResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String INS_CNSG_NAME;
	private String INS_CNSG_ADD1;
	private String INS_CNSG_ADD2;
	private String INS_CNSG_ADD3;
	private String INS_CNSG_ADD4;
	private String INS_INV_NO;
	private String INS_INV_DT;
	private String INS_PART_NO;
	private Double INS_UNIT_PER_BOX;
	private Integer INS_SUM_TOT_UNIT;
	private String INS_ICO_FLG;
	private Double INS_PART_PRICE;
	private String INS_PART_NAME;
	private Double INS_PART_WT;
	private Double INS_GROSS_WT;
	private Double INS_MEASUREMENT;
	private String INS_SHIPMARK_4;
	private String INS_SHIPMARK_5;
	private String INS_CF_CD;
	private String INS_SRS_NAME;
	private String INS_CURR_CD;
	private String INS_ICO_DESC;
	private String INS_CO_CD;
	private String ORG_CRITERIA;
	private String SC_REMARK;
	private String SC_AUTH_EMP;
	private Double INS_SORT_SEQ;     
    private String INS_HS_CD;  



}
