package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor
public class InvPackingListResponseDTO {

	private String INS_CNSG_NAME;
	private String INS_CNSG_ADD1;
	private String INS_CNSG_ADD2;
	private String INS_CNSG_ADD3;
	private String INS_CNSG_ADD4;
	private String INS_INV_NO;
	private String INS_INV_DT;
	private String INS_PART_NO;
	private int INS_UNIT_PER_BOX;
	private int INS_SUM_TOT_UNIT;
	private String INS_ICO_FLG;
	private double INS_PART_PRICE;
	private String INS_PART_NAME;
	private double INS_PART_WT;
	private double INS_GROSS_WT;
	private double INS_MEASUREMENT;
	private String INS_SHIPMARK_4;
	private String INS_SHIPMARK_5;
	
	private String SHIP_MARK_GP;
	private String CASE_MOD;
	private String INS_CF_CD;
	private String INS_SRS_NAME;
	private int INS_NO_OF_CASES;
	
   /* --DECODE ( l_v_short_inv ,'Y' , SUBSTR(INS_SHIPMARK_5, 1, 9), SUBSTR(INS_SHIPMARK_5, 1, 5)) SHIP_MARK_GP,
    INS_SHIPMARK_4  SHIP_MARK_GP,
    DECODE(SUBSTR(INS_SHIPMARK_4, 1, 3),
    l_v_cmp_cd,
    SUBSTR(INS_SHIPMARK_4, 1, 3) || '-' ||
    SUBSTR(INS_SHIPMARK_4, 4, 1) || '-' ||
    SUBSTR(INS_SHIPMARK_4, 5, 1) || '-' ||
    SUBSTR(INS_SHIPMARK_4, 6),INS_SHIPMARK_4) CASE_MOD,
    INS_CF_CD,
    INS_SRS_NAME,
    INS_NO_OF_CASES*/
	
}
