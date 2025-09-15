package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DgInvoicePackingListResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String INS014_RENBAN;
	private String INS014_TOT_CASES_RB;
	private String INS014_CASE_NO;
	private Double INS014_TOT_NW_CASE;
	private Double INS014_TOT_GW_CASE;
	private Double INS014_TOT_M3_CASE;
	private String INS014_PART_NO;
	private String INS014_PART_NM;
	private String INS014_QTY_BOX;
	private String INS014_TOT_QTY_PCS;
	private String INS014_NET_WT_PC;
	private String INS014_TOT_NET_WT_PC;
	private String INS014_BOX_GROSS_WT_PC;
	private String INS014_BOX_M3;
	private String INS014_INV_LIST;
	private String INS014_TOT_NW_RB;
	private String INS014_TOT_GW_RB;
	private String INS014_TOT_M3_RB;
	private String INS014_BOOKING_NO;
	private String INS014_CTRY_CD;

}
