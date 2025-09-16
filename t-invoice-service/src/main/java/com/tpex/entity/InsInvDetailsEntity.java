package com.tpex.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "TB_R_INV_INVOICE_H")
@Entity
public class InsInvDetailsEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "INV_NO")
	private String invoiceNo;
	
	@Column(name = "INV_DT")
	private Date invoiceDate;
	
	@Column(name = "SHIPMENT_DT")
	private Date shipmentDate;
	
	@Column(name = "ETD")
	private Date etdDate;
	
	@Column(name = "ETA")
	private Date etaDate;
	
	@Column(name = "SHIPPING_COMP")
	private String shipmentComp;
	
	@Column(name = "VESSEL_NAME_FEEDER")
	private String vesselNameFeeder;
	
	@Column(name = "VOYAGE_NO_FEEDER")
	private String voyageNoFeeder;
	
	@Column(name = "VESSEL_NAME_OCEAN")
	private String vesselNameOcean;
	
	@Column(name = "VOYAGE_NO_OCEAN")
	private String voyageNoOcean;
	
	@Column(name = "VANNING_DT")
	private Date vanningDate;
	
	@Column(name = "CNTRY_ORG")
	private String countryOrg;
	
	@Column(name = "DEST_PORT_CD")
	private String destPortCd;
	
	@Column(name = "FINAL_DST")
	private String finalDst;
	
	@Column(name = "DEP_PORT_CD")
	private String depPortCd;
	
	@Column(name = "INV_AMT")
	private int invoiceAmt;
	
	@Column(name = "INV_QTY")
	private int invoiceQty;
	
	@Column(name = "PRINT_FLG")
	private String pringflag;
	
	@Column(name = "GROSS_WT")
	private int grossWt;
	
	@Column(name = "NET_WT")
	private int netWt;
	
	@Column(name = "MEASUREMENT")
	private int measurement;
	
	@Column(name = "NO_OF_CASES")
	private int noOfCases;
	
	@Column(name = "BUYER")
	private String buyer;
	
	@Column(name = "MARK1")
	private String mark1;
	
	@Column(name = "MARK2")
	private String mark2;
	
	@Column(name = "MARK3")
	private String mark3;
	
	@Column(name = "MARK4")
	private String mark4;
	
	@Column(name = "MARK5")
	private String mark5;
	
	@Column(name = "MARK6")
	private String mark6;
	
	@Column(name = "MARK7")
	private String mark7;
	
	@Column(name = "MARK8")
	private String mark8;
	
	@Column(name = "PAY_CRNCY")
	private String payCurrency;
	
	@Column(name = "PAY_TERM")
	private String payTerm;
	
	@Column(name = "FREIGHT")
	private int freight;
	
	@Column(name = "INSURANCE")
	private int insurance;
	
	@Column(name = "EXP_CREDIT")
	private int expCredit;
	
	@Column(name = "CUST")
	private String cust;
	
	@Column(name = "CUST_NM")
	private String custNo;
	
	@Column(name = "CUST_ADDR1")
	private String custAddr1;
	
	@Column(name = "CUST_ADDR2")
	private String custAddr2;
	
	@Column(name = "CUST_ZIP")
	private String custZip;
	
	@Column(name = "CUST_CNTRY")
	private String custCountry;
	
	@Column(name = "BUYER_NM")
	private String buyerNm;
	
	@Column(name = "BUYER_ADDR1")
	private String buyerAddr1;
	
	@Column(name = "BUYER_ADDR2")
	private String buyerAddr2;
	
	@Column(name = "BUYER_ZIP")
	private String buyerZip;
	
	@Column(name = "BUYER_CNTRY")
	private String buyerCountry;
	
	@Column(name = "GOODS_DESC1")
	private String goodsDesc1;
	
	@Column(name = "GOODS_DESC2")
	private String goodsDesc2;
	
	@Column(name = "GOODS_DESC3")
	private String goodsDesc3;
	
	@Column(name = "GOODS_DESC4")
	private String goodsDesc4;
	
	@Column(name = "GOODS_DESC5")
	private String goodsDesc5;
	
	@Column(name = "GOODS_DESC6")
	private String goodsDesc6;
	
	@Column(name = "DECLR_DT")
	private Date declrDate;
	
	@Column(name = "EXCH_RT")
	private int exchangeRt;
	
	@Column(name = "EXCH_DT")
	private Date exchangeDate;
	
	@Column(name = "TRADE_TRM")
	private String tradeTrm;
	
	@Column(name = "UPD_BY")
	private String updatedBy;
	
	@Column(name = "UPD_DT")
	private Date updatedDate;
	
	@Column(name = "TNSO_FLG")
	private String trsoFlg;
	
	@Column(name = "TRNSPRT_CD")
	private String trnsprtCd;
	
	@Column(name = "BBC")
	private String bbc;
	
	@Column(name = "PKG_MTH")
	private String pkgMth;
	
	@Column(name = "XDOC")
	private String xdoc;
	
	@Column(name = "TNSO_INTERFACE_DT")
	private Date tnsoInterfaceDate;
	
	@Column(name = "DOC_NO")
	private String docNo;
	
	@Column(name = "DOC_DT")
	private Date docDate;
	
	@Column(name = "INV_TYP")
	private String invTyp;
	
	@Column(name = "SHIPPING_COST")
	private Integer shipingCost;
	
	@Column(name = "PKG_NO")
	private String pkgNo;
	
	@Column(name = "ORD_TYP")
	private String ordTyp;
	
	@Column(name = "CANCEL_FLG")
	private String cancelFlg;
	
	@Column(name = "PKG_COST")
	private Integer  pkgCost;
	
	@Column(name = "PROCESS_FLG")
	private String processFlg;
	
	@Column(name = "INV_SEQ")
	private Integer invSeq;
	
	@Column(name = "HAISEN_NO")
	private String haisenNo;
	
	@Column(name = "HAISEN_YR_MTH")
	private String haisenYearMonth;
	
	@Column(name = "EKANBAN_FLG")
	private String ekanbanFlg;
	
	@Column(name = "SET_PART")
	private String setPart;
	
	@Column(name = "FORMD_NO")
	private String formdNo;
	
	@Column(name = "NOTIFY")
	private String notify;
	
	@Column(name = "NOTIFY_NM")
	private String notifyNm;
	
	@Column(name = "NOTIFY_ADDR1")
	private String notifyAddr1;
	
	@Column(name = "NOTIFY_ADDR2")
	private String notifyAddr2;
	
	@Column(name = "NOTIFY_ZIP")
	private String notifyZip;
	
	@Column(name = "NOTIFY_CNTRY")
	private String notifyCountry;
	
	@Column(name = "NONCOM_FLG")
	private String noncomFlg;
	
	@Column(name = "IXOS_FLG")
	private String ixosFlg;
	
	@Column(name = "PROD_GRP_CD")
	private String propGrpCd;
	
	@Column(name = "INV_CAT")
	private String invCat;
	
	@Column(name = "ICS_FLG")
	private String icsFlg;
	
	@Column(name = "DUE_DT")
	private Date dueDate;
	
	@Column(name = "CPO_LOAD_DT")
	private Date cpoLoadDate;
	
	@Column(name = "CPO_PKG_COMP")
	private String cpoPkgComp;
	
	@Column(name = "CPO_FWD_NM")
	private String cpoFwdNm;
	
	@Column(name = "BUYER_HAISEN")
	private String buyerHaisen;
	
	@Column(name = "LOT_PTTRN")
	private String lotPttrn;
	
	@Column(name = "INV_CREATE_DT")
	private Date invCreateDate;
	
	@Column(name = "TMAPTH_INV_FLG")
	private String tmapthInvFlg;
	
	
	@Column(name = "IXOS_SEND_DT")
	private Date ixosDendDate;
	
	@Column(name = "ICS_SEND_DT")
	private Date icsSendDate;
	
	@Column(name = "PLS_SND_DT")
	private Date plsDendDate;
	
	@Column(name = "PLS_RSND_DT")
	private Date plsRsndDate;
	
	@Column(name = "CUST_ADDR3")
	private String custAddr3;
	
	@Column(name = "CUST_ADDR4")
	private String custAddr4;
	
	@Column(name = "BUYER_ADDR3")
	private String buyerAddr3;
	
	@Column(name = "BUYER_ADDR4")
	private String buyerAddr4;
	
	@Column(name = "NOTIFY_ADDR3")
	private String notifyAddr3;
	
	@Column(name = "NOTIFY_ADDR4")
	private String notifyAddr4;
	
	@Column(name = "SC_INV_FLG")
	private String scInvFlg;
	
	@Column(name = "SC_REMARK")
	private String scRemark;
	
	@Column(name = "SC_AUTH_EMP")
	private String scAuthEmp;
	
	@Column(name = "ETWC_FLG")
	private String etwcFlg;
	
	
}
