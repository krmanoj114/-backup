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
@IdClass(VprPartTempId.class)
@Table(name = "TB_S_VPR_PART")
public class VprPartTempEntity {

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
	@Id
	@Column(name = "LOT_MOD_NO", nullable = false)
	private String lotModNo;
	@Id
	@Column(name = "CASE_NO", nullable = false)
	private String caseNo;
	@Column(name = "CTRL_MOD_NO")
	private String ctrlModNo;
	@Id
	@Column(name = "BOX_SEQ_NO", nullable = false)
	private String boxSeqNo;
	@Column(name = "BOX_NO")
	private String boxNo;
	@Id
	@Column(name = "PART_NO", nullable = false)
	private String partNo;
	@Column(name = "PCK_QTY_BOX", nullable = false)
	private Integer pckQtyBox;
	@Column(name = "KAN_NO")
	private String kanNo;
	@Column(name = "PART_NM_1")
	private String partNm1;
	@Column(name = "PART_NM_2")
	private String partNm2;
	@Column(name = "PCK_AREA_CD")
	private String pckAreaCd;
	@Column(name = "STK_AREA_CD")
	private String stkAreaCd;
	@Column(name = "PCK_LOC")
	private String pckLoc;
	@Column(name = "BOX_GROSS_WT", precision = 8, scale = 3)
	private BigDecimal boxGrossWt;
	@Column(name = "BOX_GROSS_M3", precision = 6, scale = 4)
	private BigDecimal boxGrossM3;
	@Column(name = "BOX_MAT_CD")
	private String boxMatCd;
	@Column(name = "BOX_MAT_NO")
	private String boxMatNo;
	@Column(name = "BOX_MAT_DOC_CD")
	private String boxMatDocCd;
	@Column(name = "BOX_MAT_ADD")
	private String boxMatAdd;
	@Column(name = "BOX_MAT_SRC_CD")
	private String boxMatSrcCd;
	@Column(name = "BOX_QTY")
	private Integer boxQty;
	@Column(name = "PART_MAT_CD_1")
	private String partMatCd1;
	@Column(name = "PART_MAT_NO_1")
	private String partMatNo1;
	@Column(name = "PART_MAT_DOC_CD_1")
	private String partMatDocCd1;
	@Column(name = "PART_MAT_ADD_1")
	private String partMatAdd1;
	@Column(name = "PART_MAT_SRC_CD_1")
	private String partMatSrcCd1;
	@Column(name = "PART_QTY_1")
	private Integer partQty1;
	@Column(name = "PART_MAT_CD_2")
	private String partMatCd2;
	@Column(name = "PART_MAT_NO_2")
	private String partMatNo2;
	@Column(name = "PART_MAT_DOC_CD2")
	private String partMatDocCd2;
	@Column(name = "PART_MAT_ADD_2")
	private String partMatAdd2;
	@Column(name = "PART_MAT_SRC_CD_2")
	private String partMatSrcCd2;
	@Column(name = "PART_QTY_2")
	private Integer partQty2;
	@Column(name = "PART_MAT_CD_3")
	private String partMatCd3;
	@Column(name = "PART_MAT_NO_3")
	private String partMatNo3;
	@Column(name = "PART_MAT_DOC_CD3")
	private String partMatDocCd3;
	@Column(name = "PART_MAT_ADD_3")
	private String partMatAdd3;
	@Column(name = "PART_MAT_SRC_CD_3")
	private String partMatSrcCd3;
	@Column(name = "PART_QTY_3")
	private Integer partQty3;
	@Column(name = "PART_MAT_CD_4")
	private String partMatCd4;
	@Column(name = "PART_MAT_NO_4")
	private String partMatNo4;
	@Column(name = "PART_MAT_DOC_CD4")
	private String partMatDocCd4;
	@Column(name = "PART_MAT_ADD_4")
	private String partMatAdd4;
	@Column(name = "PART_MAT_SRC_CD_4")
	private String partMatSrcCd4;
	@Column(name = "PART_QTY_4")
	private Integer partQty4;
	@Column(name = "PART_MAT_CD_5")
	private String partMatCd5;
	@Column(name = "PART_MAT_NO_5")
	private String partMatNo5;
	@Column(name = "PART_MAT_DOC_CD5")
	private String partMatDocCd5;
	@Column(name = "PART_MAT_ADD_5")
	private String partMatAdd5;
	@Column(name = "PART_MAT_SRC_CD_5")
	private String partMatSrcCd5;
	@Column(name = "PART_QTY_5")
	private Integer partQty5;
	@Column(name = "ORD_TY")
	private String ordTy;
	@Column(name = "ORD_IMP_CD", nullable = false)
	private String ordImpCd;
	@Column(name = "ORD_EXP_CD", nullable = false)
	private String ordExpCd;
	@Column(name = "ORD_MSP_TY")
	private String ordMspTy;
	@Column(name = "ORD_PCK_MTH", nullable = false)
	private String ordPckMth;
	@Column(name = "ORD_CF_CD", nullable = false)
	private String ordCfCd;
	@Column(name = "ORD_REXP_CD", nullable = false)
	private String ordRexpCd;
	@Column(name = "ORD_AICO_FLG", nullable = false)
	private String ordAicoFlg;
	@Column(name = "ORD_PART_NO")
	private String ordPartNo;
	@Column(name = "ORD_LOT")
	private Integer ordLot;
	@Column(name = "ORD_IMP_LN")
	private String ordImpLn;
	@Column(name = "ORD_SET_PART_FLG", nullable = false)
	private String ordSetPartFlg;
	@Column(name = "ORD_SRS_NM")
	private String ordSrsNm;
	@Column(name = "ORD_SUP_CD")
	private String ordSupCd;
	@Column(name = "ORD_SUP_PLNT")
	private String ordSupPlnt;
	@Column(name = "ORD_SUP_DOCK")
	private String ordSupDock;
	@Column(name = "ARV_DT_TM")
	private String arvDtTm;
	@Column(name = "ORD_MROS")
	private String ordMros;
	@Column(name = "ORD_MROS_DT")
	private String ordMrosDt;
	@Column(name = "ORD_MROS_GRP")
	private String ordMrosGrp;
	@Column(name = "XDOCK_ARV_DT_TM")
	private String xdockArvDtTm;
	@Column(name = "ORD_RLS_DT", columnDefinition = "DATE")
	private LocalDate ordRlsDt;
	@Column(name = "ORD_RLS_TM")
	private String ordRlsTm;
	@Column(name = "ORD_BO_FLG")
	private String ordBoFlg;
	@Column(name = "ORD_BO_QTY")
	private Integer ordBoQty;
	@Column(name = "SHP_DT_TM")
	private Long shpDtTm;
	@Column(name = "FRY_DT_TM")
	private Long fryDtTm;
	@Column(name = "ORD_QTY")
	private Integer ordQty;
	@Column(name = "ORD_QTY_LOT")
	private Integer ordQtyLot;
	@Column(name = "KAN_PRNT_ADD")
	private String kanPrntAdd;
	@Column(name = "EKAN_ORD_NO")
	private String ekanOrdNo;
	@Column(name = "ORD_EKAN_NO")
	private String ordEkanNo;
	@Column(name = "QTY_PER_CONT")
	private Integer qtyPerCont;
	@Column(name = "ORD_MODE")
	private String ordMode;
	@Column(name = "CONV_RT")
	private String convRt;
	@Column(name = "DNG_PART_DST")
	private String dngPartDst;
	@Column(name = "DNG_PART_IMO")
	private String dngPartImo;
	@Column(name = "DNG_PART_CLS")
	private String dngPartCls;
	@Column(name = "DNG_PART_LVL")
	private String dngPartLvl;
	@Column(name = "DNG_PART_LVL_CD")
	private String dngPartLvlCd;
	@Column(name = "PART_OP_CTRL")
	private String partOpCtrl;
	@Column(name = "IMP_INFO_1")
	private String impInfo1;
	@Column(name = "IMP_INFO_2")
	private String impInfo2;
	@Column(name = "IMP_INFO_3")
	private String impInfo3;
	@Column(name = "IMP_INFO_4")
	private String impInfo4;
	@Column(name = "PAC_TBL_1")
	private Integer pacTbl1;
	@Column(name = "PART_KIT_CD")
	private String partKitCd;
	@Column(name = "PART_KIT_SRL_NO")
	private String partKitSrlNo;
	@Column(name = "PART_KIT_M3", precision = 6, scale = 4)
	private BigDecimal partKitM3;
	@Column(name = "PART_NET_WT", nullable = false, precision = 10, scale = 5)
	private BigDecimal partNetWt;
	@Column(name = "PART_ORD_VAN_DT", columnDefinition = "DATE")
	private LocalDate partOrdVanDt;
	@Column(name = "PART_ORD_RENBAN")
	private String partOrdRenban;
	@Column(name = "UPD_BY", nullable = false)
	private String updBy;
	@Column(name = "UPD_DT", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime updDt;
	@Column(name = "INV_AICO_FLG")
	private String invAicoFlg;
	@Column(name = "CMP_CD")
	private String compCode;

}
