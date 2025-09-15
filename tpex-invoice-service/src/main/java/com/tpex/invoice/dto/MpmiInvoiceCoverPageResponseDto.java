package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpmiInvoiceCoverPageResponseDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String invoiceNo;
	private String invoiceDate;
	private String compNme;
	private String compAdd1;
	private String compAdd2;
	private String compAdd3;
	private String compAdd4;
	private String custName;
	private String custAddr1;
	private String custAddr2;
	private String custAddr3;
	private String custAddr4;
	private String custZip;
	private String lvCustName;
	private String orderNo;
	private String etdDate;
	private String etaDate;
	private String vesselNameOcean;
	private String voyageNoOcean;
	private String vesselNameFeeder;
	private String voyageNoFeeder;
	private String pmName;
	private String originCntry;
	private String lvFinalCntryName;
	private String buyerCntry;
	private String ptDesc;
	private String goodsDesc1;
	private String goodsDesc2;
	private String goodsDesc3;
	private String tradeTerm;
	private String pmName2;
	private Double invoiceAmount;
	private Double invoiceQty;
	private Double grossWeight;
	private Double netWeight;
	private Double measurement;
	private Double noOfCases;
	private Double freight;
	private Double insurance;
	private String mark1;
	private String mark2;
	private String mark3;
	private String mark4_1;
	private String mark4_2;
	private String mark4_3;
	private String mark4_4;
	private String mark5_1;
	private String mark5_2;
	private String mark5_3;
	private String mark5_4;
	private String mark6_1;
	private String mark6_2;
	private String mark6_3;
	private String mark6_4;
	private String mark7;
	private String mark8;
	private String buyerName;
	private String buyerAddr1;
	private String buyerAddr2;
	private String buyerAddr3;
	private String buyerAddr4;
	private String buyerZip;
	private String crmCurrPrntName;
	private String buyerCntryFinal;
	private String goodsDesc4;
	private String goodsDesc5;
	private String goodsDesc6;
	private String haisenNo;
	private String notify;
	private String notifyAddr1;
	private String notifyAddr2;
	private String notifyAddr3;
	private String notifyAddr4;
	private String notifyCntry;
	private String notifyName;
	private String notifyZip;
	private String xdocFlag;
	private String cntryOrg;
	private String coCd;
	
	private String tariffName1;
	private Double tariffQty1 ;
	private Double tariffAmount1 ;
	private String tariffName2;
	private Double tariffQty2;
	private Double tariffAmount2;
	private String tariffName3;
	private Double tariffQty3;
	private Double tariffAmount3;
	private String tariffName4;
	private Double tariffQty4;
	private Double tariffAmount4;
	private String tariffName5;
	private Double tariffQty5;
	private Double tariffAmount5;
	private String tariffName6;
	private Double tariffQty6;
	private Double tariffAmount6;
	
}
