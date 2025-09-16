package com.tpex.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_R_INV_INVOICE_H")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class InsInvDtlsEntity {
	@Id
	@Column(name = "INV_NO")
	private String indInvNo;

	@Column(name = "INV_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indInvDt;

	public LocalDateTime getIndInvDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indInvDt);
	}

	@Column(name = "SHIPMENT_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indShipmentDt;

	public LocalDateTime getIndShipmentDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indShipmentDt);
	}

	@Column(name = "ETD")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indEtd;

	public LocalDateTime getIndEtd() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indEtd);
	}

	@Column(name = "ETA")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indEta;

	public LocalDateTime getIndEta() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indEta);
	}

	@Column(name = "SHIPPING_COMP")
	private String indShippingComp;

	@Column(name = "VESSEL_NAME_FEEDER")
	private String indVesselNameFeeder;

	@Column(name = "VOYAGE_NO_FEEDER")
	private String indVoyageNoFeeder;

	@Column(name = "VESSEL_NAME_OCEAN")
	private String indvesselnameocean;

	@Column(name = "VOYAGE_NO_OCEAN")
	private String indvoyagenoocean;

	@Column(name = "VANNING_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indVanningDt;

	public LocalDateTime getindVanningDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indVanningDt);
	}

	@Column(name = "CNTRY_ORG")
	private String indCntryOrg;

	@Column(name = "DEST_PORT_CD")
	private String indDestPortCd;

	@Column(name = "FINAL_DST")
	private String indFinalDst;

	@Column(name = "DEP_PORT_CD")
	private String indDepPortCd;

	@Column(name = "INV_AMT")
	private String indInvAmt;

	@Column(name = "INV_QTY")
	private String indInvQty;

	@Column(name = "PRINT_FLG")
	private String indPrintFlg;

	@Column(name = "GROSS_WT")
	private String indGrossWt;

	@Column(name = "NET_WT")
	private String indNetWt;

	@Column(name = "MEASUREMENT")
	private String indMeasurement;

	@Column(name = "NO_OF_CASES")
	private String indNoOfCases;

	@Column(name = "BUYER")
	private String indBuyer;

	@Column(name = "MARK1")
	private String indMark1;

	@Column(name = "MARK2")
	private String indMark2;

	@Column(name = "MARK3")
	private String indMark3;

	@Column(name = "MARK4")
	private String indMark4;

	@Column(name = "MARK5")
	private String indMark5;

	@Column(name = "MARK6")
	private String indMark6;

	@Column(name = "MARK7")
	private String indMark7;

	@Column(name = "MARK8")
	private String indMark8;

	@Column(name = "PAY_CRNCY")
	private String indPayCrncy;

	@Column(name = "PAY_TERM")
	private String indPayTerm;

	@Column(name = "FREIGHT")
	private Double indFreight;

	@Column(name = "INSURANCE")
	private Double indInsurance;

	@Column(name = "EXP_CREDIT")
	private String indExpCredit;

	@Column(name = "CUST")
	private String indCust;

	@Column(name = "CUST_NM")
	private String indCustNm;

	@Column(name = "CUST_ADDR1")
	private String indCustAddr1;

	@Column(name = "CUST_ADDR2")
	private String indCustAddr2;

	@Column(name = "CUST_ZIP")
	private String indCustZip;

	@Column(name = "CUST_CNTRY")
	private String indCustCntry;

	@Column(name = "BUYER_NM")
	private String indBuyerNm;

	@Column(name = "BUYER_ADDR1")
	private String indBuyerAddr1;

	@Column(name = "BUYER_ADDR2")
	private String indBuyerAddr2;

	@Column(name = "BUYER_ZIP")
	private String indBuyerZip;

	@Column(name = "BUYER_CNTRY")
	private String indBuyerCntry;

	@Column(name = "GOODS_DESC1")
	private String indGoodsDesc1;

	@Column(name = "GOODS_DESC2")
	private String indGoodsDesc2;

	@Column(name = "GOODS_DESC3")
	private String indGoodsDesc3;

	@Column(name = "GOODS_DESC4")
	private String indGoodsDesc4;

	@Column(name = "GOODS_DESC5")
	private String indGoodsDesc5;

	@Column(name = "GOODS_DESC6")
	private String indGoodsDesc6;

	@Column(name = "DECLR_NO")
	private String indDeclrNo;

	@Column(name = "DECLR_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indDeclrDt;

	public LocalDateTime getIndDeclrDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indDeclrDt);
	}

	@Column(name = "EXCH_RT")
	private String indExchRt;

	@Column(name = "EXCH_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indExchDt;

	public LocalDateTime getIndExchDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indExchDt);
	}

	@Column(name = "TRADE_TRM")
	private String indTradeTrm;

	@Column(name = "UPD_BY")
	private String indUpdBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indUpdDt;

	public LocalDateTime getIndUpdDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indUpdDt);
	}

	@Column(name = "TNSO_FLG")
	private String indTnsoFlg;

	@Column(name = "TRNSPRT_CD")
	private String indTrnsprtCd;

	@Column(name = "BBC")
	private String indBbc;

	@Column(name = "PKG_MTH")
	private String indPkgMth;

	@Column(name = "XDOC")
	private String indXdoc;

	@Column(name = "TNSO_INTERFACE_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp indTnsoInterfaceDt;

	public LocalDateTime getIndTnsoInterfaceDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indTnsoInterfaceDt);
	}

	@Column(name = "DOC_NO")
	private String indDocNo;

	@Column(name = "DOC_DT")
	private Timestamp indDocDt;

	public LocalDateTime getIndDocDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indDocDt);
	}

	@Column(name = "INV_TYP")
	private String indInvTyp;

	@Column(name = "SHIPPING_COST")
	private String indShippingCost;

	@Column(name = "PKG_NO")
	private String indPkgNo;

	@Column(name = "ORD_TYP")
	private String indOrdTyp;

	@Column(name = "CANCEL_FLG")
	private String indCancelFlag;

	@Column(name = "PKG_COST")
	private String indPkgCost;

	@Column(name = "PROCESS_FLG")
	private String indProcessFlag;

	@Column(name = "INV_SEQ")
	private String indInvSeq;

	@Column(name = "HAISEN_NO")
	private String indHaisenNo;

	@Column(name = "HAISEN_YR_MTH")
	private String indHaisenYearMonth;

	@Column(name = "EKANBAN_FLG")
	private String indEkanbanFlag;

	@Column(name = "SET_PART")
	private String indSetPart;

	@Column(name = "FORMD_NO")
	private String indFormdNo;

	@Column(name = "NOTIFY")
	private String indNotify;

	@Column(name = "NOTIFY_NM")
	private String indNotifyName;

	@Column(name = "NOTIFY_ADDR1")
	private String indNotifyAddr1;

	@Column(name = "NOTIFY_ADDR2")
	private String indNotifyAddr2;

	@Column(name = "NOTIFY_ZIP")
	private String indNotifyZip;

	@Column(name = "NOTIFY_CNTRY")
	private String indNotifyCntry;

	@Column(name = "NONCOM_FLG")
	private String indNoncomFlag;

	@Column(name = "IXOS_FLG")
	private String indIxosFlag;

	@Column(name = "PROD_GRP_CD")
	private String indProdGrpCd;

	@Column(name = "INV_CAT")
	private String indInvCat;

	@Column(name = "ICS_FLG")
	private String indIcsFlag;

	@Column(name = "DUE_DT")
	private Timestamp indDueDt;

	public LocalDateTime getIndDueDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indDueDt);
	}

	@Column(name = "CPO_LOAD_DT")
	private Timestamp indCpoLoadDt;

	public LocalDateTime getIndCpoLoadDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indCpoLoadDt);
	}

	@Column(name = "CPO_PKG_COMP")
	private String indCpoPkgComp;

	@Column(name = "CPO_FWD_NM")
	private String indCpoFwdNm;

	@Column(name = "BUYER_HAISEN")
	private String indBuyerHaisen;

	@Column(name = "LOT_PTTRN")
	private String indLotPttrn;

	@Column(name = "INV_CREATE_DT")
	private Timestamp indInvCreateDt;

	public LocalDateTime getIndInvCreateDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indInvCreateDt);
	}

	@Column(name = "TMAPTH_INV_FLG")
	private String indTmapthInvFlag;

	@Column(name = "IXOS_SEND_DT")
	private Timestamp indIxosSendDt;

	public LocalDateTime getIndIxosSendDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indIxosSendDt);
	}

	@Column(name = "ICS_SEND_DT")
	private Timestamp indIcsSendDt;

	public LocalDateTime getIndIcsSendDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indIcsSendDt);
	}

	@Column(name = "PLS_SND_DT")
	private Timestamp indPlsSndDt;

	public LocalDateTime getIndPlsSndDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indPlsSndDt);
	}

	@Column(name = "PLS_RSND_DT")
	private Timestamp indPlsRsndDt;

	public LocalDateTime getIndPlsRsndDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(indPlsRsndDt);
	}

	@Column(name = "CUST_ADDR3")
	private String indCustAddr3;

	@Column(name = "CUST_ADDR4")
	private String indCustAddr4;

	@Column(name = "BUYER_ADDR3")
	private String indBuyerAddr3;

	@Column(name = "BUYER_ADDR4")
	private String indBuyerAddr4;

	@Column(name = "NOTIFY_ADDR3")
	private String indNotifyAddr3;

	@Column(name = "NOTIFY_ADDR4")
	private String indNotifyAddr4;

	@Column(name = "SC_INV_FLG")
	private String indScInvFlag;

	@Column(name = "SC_REMARK")
	private String indScRemark;

	@Column(name = "SC_AUTH_EMP")
	private String indScAuthEmp;

	@Column(name = "ETWC_FLG")
	private String indEtwcFlag;

	@Column(name = "CMP_CD")
	private String companyCode;
}
