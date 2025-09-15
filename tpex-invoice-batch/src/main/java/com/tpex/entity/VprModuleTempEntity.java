package com.tpex.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@IdClass(VprModuleTempId.class)
@Table(name = "TB_S_VPR_MODULE")
public class VprModuleTempEntity {

	@Id
	@Column(name = "PROC_DT", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime procDt;
	@Id
	@Column(name = "INTR_ID", nullable = false)
	private String intrId;
	@Id
	@Column(name = "MAIN_FLG", nullable = false)
	private String mainFlg;
	@Id
	@Column(name = "CONT_DST_CD", nullable = false)
	private String contDstCd;
	@Id
	@Column(name = "CONT_SNO", nullable = false)
	private String contSno;
	@Id
	@Column(name = "MOD_DST_CD", nullable = false)
	private String modDstCd;
	@Column(name = "MOD_RECV_PLNT")
	private String modRecvPlnt;
	@Column(name = "MOD_IMP_DOC")
	private String modImpDoc;
	@Id
	@Column(name = "LOT_MOD_NO", nullable = false)
	private String lotModNo;
	@Id
	@Column(name = "CASE_NO", nullable = false)
	private String caseNo;
	@Column(name = "EXP_CD", nullable = false)
	private String expCd;
	@Column(name = "EXP_COMP_NM")
	private String expCompNm;
	@Column(name = "IMP_COMP_NM")
	private String impCompNm;
	@Column(name = "CF_CD", nullable = false)
	private String cfCd;
	@Column(name = "IMP_LN_CD")
	private String impLnCd;
	@Column(name = "REXP_CD", nullable = false)
	private String rexpCd;
	@Column(name = "AICO_FLG", nullable = false)
	private String aicoFlg;
	@Column(name = "ORD_TYP")
	private String ordTyp;
	@Column(name = "PKG_MTH", nullable = false)
	private String pkgMth;
	@Column(name = "SUP_PLNT_CD")
	private String supPlntCd;
	@Column(name = "SUP_DOC_CD")
	private String supDocCd;
	@Column(name = "BUN_MOD")
	private String bunMod;
	@Column(name = "BUN_FLG")
	private String bunFlg;
	@Column(name = "PCK_PLNT", nullable = false)
	private String pckPlnt;
	@Column(name = "PCK_PLNT_NM")
	private String pckPlntNm;
	@Column(name = "ORD_TY")
	private String ordTy;
	@Column(name = "SS_NO")
	private String ssNo;
	@Column(name = "EXT_CLR_CD")
	private String extClrCd;
	@Column(name = "INT_CLR_CD")
	private String intClrCd;
	@Column(name = "ORD_SRC", nullable = false)
	private String ordSrc;
	@Column(name = "CONT_LOAD_SEQ_1")
	private Integer contLoadSeq1;
	@Column(name = "CONT_LOAD_SEQ_2")
	private Integer contLoadSeq2;
	@Column(name = "CONT_LOAD_SEQ_3")
	private Integer contLoadSeq3;
	@Column(name = "CONT_LOAD_SEQ_4")
	private Integer contLoadSeq4;
	@Column(name = "CONT_LOAD_SEQ_5")
	private Integer contLoadSeq5;
	@Column(name = "ORG_VAN_DT", nullable = false, columnDefinition = "DATE")
	private LocalDate orgVanDt;
	@Column(name = "PLN_PCK_DT", nullable = false, columnDefinition = "DATE")
	private LocalDate plnPckDt;
	@Column(name = "PLN_PCK_TM")
	private String plnPckTm;
	@Column(name = "ACT_PCK_DT", columnDefinition = "DATE")
	private LocalDate actPckDt;
	@Column(name = "ACT_PCK_TM")
	private String actPckTm;
	@Column(name = "PCK_LN_CD")
	private String pckLnCd;
	@Column(name = "PCK_SEQ_NO")
	private Integer pckSeqNo;
	@Column(name = "MOD_STD_TM")
	private Integer modStdTm;
	@Column(name = "MOD_TY")
	private String modTy;
	@Column(name = "MOD_SZ_CD")
	private String modSzCd;
	@Column(name = "MOD_GRP_NM")
	private String modGrpNm;
	@Column(name = "MOD_NET_WT", precision = 8, scale = 3)
	private BigDecimal modNetWt;
	@Column(name = "MOD_GROSS_WT", precision = 8, scale = 3)
	private BigDecimal modGrossWt;
	@Column(name = "MOD_TARE_WT", precision = 8, scale = 3)
	private BigDecimal modTareWt;
	@Column(name = "MOD_OUT_M3", precision = 6, scale = 4)
	private BigDecimal modOutM3;
	@Column(name = "MOD_IN_M3", precision = 6, scale = 4)
	private BigDecimal modInM3;
	@Column(name = "MOD_LN")
	private Integer modLn;
	@Column(name = "MOD_WH")
	private Integer modWh;
	@Column(name = "MOD_HT")
	private Integer modHt;
	@Column(name = "LOAD_STL")
	private String loadStl;
	@Column(name = "MOD_COM1")
	private String modCom1;
	@Column(name = "MOD_COM2")
	private String modCom2;
	@Column(name = "DISP_MOD_CD")
	private String dispModCd;
	@Column(name = "CTRL_MOD_CD")
	private String ctrlModCd;
	@Column(name = "SRS_NM")
	private String srsNm;
	@Column(name = "RR_TY")
	private String rrTy;
	@Column(name = "UNIT_LOT")
	private Integer unitLot;
	@Column(name = "UNIT_CASE")
	private Integer unitCase;
	@Column(name = "ACC_PART_M3", precision = 6, scale = 4)
	private BigDecimal accPartM3;
	@Column(name = "MOD_EFF_M3")
	private String modEffM3;
	@Column(name = "MOD_MAX_WT", precision = 8, scale = 3)
	private BigDecimal modMaxWt;
	@Column(name = "MOD_EFF_WT")
	private String modEffWt;
	@Column(name = "MOD_M3_COF")
	private Integer modM3Cof;
	@Column(name = "BAR_FLG")
	private String barFlg;
	@Column(name = "PAC_TBL_1")
	private Integer pacTbl1;
	@Column(name = "PAC_TBL_2")
	private Integer pacTbl2;
	@Column(name = "PAC_TBL_3")
	private Integer pacTbl3;
	@Column(name = "PAC_TBL_4")
	private Integer pacTbl4;
	@Column(name = "PAC_TBL_5")
	private Integer pacTbl5;
	@Column(name = "PAC_TBL_6")
	private Integer pacTbl6;
	@Column(name = "PAC_TBL6_DT", columnDefinition = "DATE")
	private LocalDate pacTbl6Dt;
	@Column(name = "PAC_TBL_7")
	private Integer pacTbl7;
	@Column(name = "PAC_TBL7_DT", columnDefinition = "DATE")
	private LocalDate pacTbl7Dt;
	@Column(name = "PAC_TBL7_TM")
	private String pacTbl7Tm;
	@Column(name = "EKAN_ORD_SRT")
	private String ekanOrdSrt;
	@Column(name = "EKAN_ORD_END")
	private String ekanOrdEnd;
	@Column(name = "STS_FLG_PLN")
	private String stsFlgPln;
	@Column(name = "SPC_RSV_FLG")
	private String spcRsvFlg;
	@Column(name = "DST_NM")
	private String dstNm;
	@Column(name = "MOD_MAT_CD")
	private String modMatCd;
	@Column(name = "MOD_PART_NO")
	private String modPartNo;
	@Column(name = "MOD_MAT_DOC_CD")
	private String modMatDocCd;
	@Column(name = "MOD_MAT_ADD")
	private String modMatAdd;
	@Column(name = "MOD_MAT_SRC_CD")
	private String modMatSrcCd;
	@Column(name = "MOD_MAT_QTY")
	private Integer modMatQty;
	@Column(name = "MOD_LOAD_GRP")
	private String modLoadGrp;
	@Column(name = "MOD_KIT_CD")
	private String modKitCd;
	@Column(name = "MOD_KIT_SR_NO")
	private String modKitSrNo;
	@Column(name = "MOD_PLOAD_GRP")
	private String modPloadGrp;
	@Column(name = "LOT_PTTRN")
	private String lotPttrn;
	@Column(name = "UPD_BY", nullable = false)
	private String updBy;
	@Column(name = "UPD_DT", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime updDt;
	@Column(name = "CMP_CD")
	private String compCode;

}
