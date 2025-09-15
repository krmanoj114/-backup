package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCoverPageResponseDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String IND_INV_NO;
	private String INV_DT;
	private String CMP_NAME;
	private String CMP_ADD_1;
	private String CMP_ADD_2;
	private String CMP_ADD_3;
	private String CMP_ADD_4;
	private String IND_CUST_NM;
	private String IND_CUST_ADDR1;
	private String IND_CUST_ADDR2;
	private String IND_CUST_ADDR3;
	private String IND_CUST_ADDR4;
	private String IND_CUST_ZIP;
	private String L_V_CUST_NM;
	private String ORD_NO;
	private String ETD_DT;
	private String ETA_DT;
	private String IND_VESSEL_NAME_OCEAN;
	private String IND_VOYAGE_NO_OCEAN;
	private String IND_VESSEL_NAME_FEEDER;
	private String IND_VOYAGE_NO_FEEDER;
	private String PM_NAME;
	private String ORGIN_CNTRY;
	private String L_V_FNL_CNTRY_NM;
	private String BUYER_CNTRY;
	private String PT_DESC;
	private String IND_GOODS_DESC1;
	private String IND_GOODS_DESC2;
	private String IND_GOODS_DESC3;
	private String IND_TRADE_TRM;
	private String PM_NAME2;
	private Double IND_INV_AMT;
	private Double IND_INV_QTY;
	private Double IND_GROSS_WT;
	private Double IND_NET_WT;
	private Double IND_MEASUREMENT;
	private Double IND_NO_OF_CASES;
	private Double IND_FREIGHT;
	private Double IND_INSURANCE;
	private String IND_MARK1;
	private String IND_MARK2;
	private String IND_MARK3;
	private String IND_MARK4_1;
	private String IND_MARK4_2;
	private String IND_MARK4_3;
	private String IND_MARK4_4;
	private String IND_MARK5_1;
	private String IND_MARK5_2;
	private String IND_MARK5_3;
	private String IND_MARK5_4;
	private String IND_MARK6_1;
	private String IND_MARK6_2;
	private String IND_MARK6_3;
	private String IND_MARK6_4;
	private String IND_MARK7;
	private String IND_MARK8;
	private String IND_BUYER_NM;
	private String IND_BUYER_ADDR1;
	private String IND_BUYER_ADDR2;
	private String IND_BUYER_ADDR3;
	private String IND_BUYER_ADDR4;
	private String IND_BUYER_ZIP;
	private String CRM_CURR_PRNT_NM;
	private String BUYER_CNTRY_FINAL;
	private String IND_GOODS_DESC4;
	private String IND_GOODS_DESC5;
	private String IND_GOODS_DESC6;
	private String IND_HAISEN_NO;
	private String IND_NOTIFY;
	private String IND_NOTIFY_ADDR1;
	private String IND_NOTIFY_ADDR2;
	private String IND_NOTIFY_ADDR3;
	private String IND_NOTIFY_ADDR4;
	private String IND_NOTIFY_CNTRY;
	private String IND_NOTIFY_NM;
	private String IND_NOTIFY_ZIP;
	private String XDOC_FLG;
	private String CNTRY_ORG;
	private String CO_CD;
	
}
