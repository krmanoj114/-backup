package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkInstrReportDTO implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	private String IICD_BKG_NO;
	private String IND_HAISEN_NO;
	private String IND_FINAL_DST;
	private String INM_PCK_MTH;
	private String IND_ETD;
	private String IND_ETA;
	private String IND_VANNING_DT;
	private String IND_SHIPPING_COMP;
	private String IND_VESSEL_NAME_OCEAN;
	private String IND_VOYAGE_NO_OCEAN;
	private String IICD_VAN_PLNT_CD;
	private Integer INV_COUNT;
	private String IICD_INV_NO;
	private Integer INM_MOD_NO;
	private String IND_INV_AMT;
	private String INP_AICO_NAME;
	private String INP_SERIES;
	private String INP_PCK_MTH;
	private String RACK_COUNT;
	private String NON_DG_20;
	private String NON_DG_40;
	private String DG_20;
	private String DG_40;
	private String IND_BUYER;
	private String MODEL;
	private String RBOXDATA_CNT;
	

}
