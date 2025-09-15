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
	
	private String indInvNo;
	private String invDt;
	private String cmpName;
	private String cmpAdd1;
	private String cmpAdd2;
	private String cmpAdd3;
	private String cmpAdd4;
	private String indCustNm;
	private String indCustAddr1;
	private String indCustAddr2;
	private String indCustAddr3;
	private String indCustAddr4;
	private String indCustZip;
	private String lvCustNm;
	private String ordNo;
	private String etdDt;
	private String etaDt;
	private String indVesselNameOcean;
	private String indVoyageNoOcean;
	private String indVesselNameFeeder;
	private String indVoyageNoFeeder;
	private String pmName;
	private String originCntry;
	private String lvFnlCntryNm;
	private String buyerCntry;
	private String ptDesc;
	private String indGoodsDesc1;
	private String indGoodsDesc2;
	private String indGoodsDesc3;
	private String indTradeTrm;
	private String pmName2;
	private Double indInvAmt;
	private Double indInvQty;
	private Double indGrossWt;
	private Double indNetWt;
	private Double indMeasurement;
	private Double indNoOfCases;
	private Double indFreight;
	private Double indInsurance;
	private String indMark1;
	private String indMark2;
	private String indMark3;
	private String indMark41;
	private String indMark42;
	private String indMark43;
	private String indMark44;
	private String indMark51;
	private String indMark52;
	private String indMark53;
	private String indMark54;
	private String indMark61;
	private String indMark62;
	private String indMark63;
	private String indMark64;
	private String indMark7;
	private String indMark8;
	private String indBuyerNm;
	private String indBuyerAddr1;
	private String indBuyerAddr2;
	private String indBuyerAddr3;
	private String indBuyerAddr4;
	private String indBuyerZip;
	private String crmCurrPrintNm;
	private String buyerCntryFinal;
	private String indGoodsDesc4;
	private String indGoodsDesc5;
	private String indGoodsDesc6;
	private String indHaisenNo;
	private String indNotify;
	private String indNotifyAddr1;
	private String indNotifyAddr2;
	private String indNotifyAddr3;
	private String indNotifyAddr4;
	private String indNotifyCntry;
	private String indNotifyNm;
	private String indNotifyZip;
	private String xdocFlg;
	private String cntryOrg;
	private String coCd;
	
}
