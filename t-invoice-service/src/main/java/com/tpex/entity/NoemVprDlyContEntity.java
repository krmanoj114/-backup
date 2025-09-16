package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "NOEM_VPR_DLY_CONT")
@Entity
public class NoemVprDlyContEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private NoemVprDlyContIdEntity id;
	
	@Column(name = "NRSM_EFF_TO_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	@NotNull
	private Date effToDt;
	public LocalDate getEta() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effToDt);
	}
	
	@Column(name = "NVDC_RECV_PLNT")
	private String recvPlnt;
	
	@Column(name = "NVDC_SEAL_NO")
	private String sealNo;
	
	@Column(name = "NVDC_PKG_MTH")
	private String pkgMth;
	
	@Column(name = "NVDC_VAN_PLNT_CD")
	private String vanPlntCd;
	
	@Column(name = "NVDC_VAN_PLNT_NM")
	private String vanPlntNm;
	
	@Column(name = "NVDC_ISO_CONT_NO")
	private String isoContNo;
	
	@Column(name = "NVDC_PLN_VAN_STRT_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date plnVanStrtDt;
	public LocalDate getPlnVanStrtDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(plnVanStrtDt);
	}
	
	@Column(name = "NVDC_PLN_VAN_STRT_TM")
	private String plnVanStrtTm;
	
	@Column(name = "NVDC_PLN_VAN_END_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date plnVanEndDt;
	public LocalDate getPlnVanEndDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(plnVanEndDt);
	}
	
	@Column(name = "NVDC_PLN_VAN_END_TM")
	private String plnVanEndTm;
	
	@Column(name = "NVDC_ACT_VAN_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date actVanDt;
	public LocalDate getActVanDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(actVanDt);
	}
	
	@Column(name = "NVDC_ACT_VAN_TM")
	private String actVanTm;
	
	@Column(name = "NVDC_VAN_LN")
	private String vanLn;
	
	@Column(name = "NVDC_VAN_SEQ_NO")
	private Integer vanSeqNo;
	
	@Column(name = "NVDC_PAC_TBL_1")
	private Integer pacTbl1;
	
	@Column(name = "NVDC_VAN_GRP_CD")
	private String vanGrpCd;
	
	@Column(name = "NVDC_VAN_GRP_SEQ")
	private Integer vanVanGrpSeq;
	
	@Column(name = "NVDC_TRNS")
	private String trns;
	
	@Column(name = "NVDC_CONT_TYP")
	private String contTyp;
	
	@Column(name = "NVDC_CONT_SIZE")
	private String contSize;
	
	@Column(name = "NVDC_CONT_GRP_CD")
	private String contGrpCd;
	
	@Column(name = "NVDC_CONT_NET_WT")
	private Integer contNetWt;
	
	@Column(name = "NVDC_CONT_GROSS_WT")
	private Integer contGrossWt;
	
	@Column(name = "NVDC_CONT_TARE_WT")
	private Integer contTareWt;
	
	@Column(name = "NVDC_CONT_MAX_WT")
	private Integer contMaxWt;
	
	@Column(name = "NVDC_CONT_MAX_COF_WT")
	private String contMaxCofWt;
	
	@Column(name = "NVDC_CONT_MAX_M3")
	private Integer contMaxM3;
	
	@Column(name = "NVDC_CONT_MAX_COF_M3")
	private String contMaxCofM3;
	
	@Column(name = "NVDC_XDOC_FLG")
	private String xdocflg;
	
	@Column(name = "NVDC_DEP_PORT")
	private String depPort;
	
	@Column(name = "NVDC_DST_PORT")
	private String dstPort;
	
	@Column(name = "NVDC_VSSL_SRL_1")
	private String vsslSrl1;
	
	@Column(name = "NVDC_VSSL_NM_1")
	private String vsslNm1;
	
	@Column(name = "NVDC_SHP_NM_1")
	private String shpNm1;
	
	@Column(name = "NVDC_VOY_1")
	private String voy1;
	
	@Column(name = "NVDC_ETD_1")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date etd1;
	public LocalDate getEtd1() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etd1);
	}
	
	@Column(name = "NVDC_ETA_1")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date eta1;
	public LocalDate getEta1() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(eta1);
	}
	
	@Column(name = "NVDC_VSSL_SRL_2")
	private String vsslSrl2;
	
	@Column(name = "NVDC_VSSL_NM_2")
	private String vsslNm2;
	
	@Column(name = "NVDC_SHP_NM_2")
	private String shpNm2;
	
	@Column(name = "NVDC_VOY_2")
	private String voy2;
	
	@Column(name = "NVDC_ETD_2")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date etd2;
	public LocalDate getEtd2() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etd2);
	}
	
	@Column(name = "NVDC_ETA_2")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date eta2;
	public LocalDate getEta2() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(eta2);
	}
	
	@Column(name = "NVDC_DST_NM")
	private String dstNm;
	
	@Column(name = "NVDC_UPD_BY")
	private String updBy;
	
	@Column(name = "NVDC_UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updDt;
	public LocalDate getUpdDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updDt);
	}
	
	@Column(name = "NVDC_CP_BAR_FLG")
	private String cpBarFlg;
	
	@Column(name = "NVDC_CB_FLG")
	private String cbFlg;
	
	@Column(name = "NVDC_PREV_CB_FLG")
	private String prevCbFlg;
	
	@Column(name = "NVDC_VAN_START_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date vanStartDt;
	public LocalDate getVanStartDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(vanStartDt);
	}
	
	@Column(name = "NVDC_VAN_END_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date vanEndDt;
	public LocalDate getVanEndDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(vanEndDt);
	}
	
	@Column(name = "NVDC_ICS_VAN_SEND_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date icsVanSendDt;
	public LocalDate getIcsVanSendDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(icsVanSendDt);
	}

}
