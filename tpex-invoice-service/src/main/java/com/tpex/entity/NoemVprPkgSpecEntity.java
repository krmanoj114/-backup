package com.tpex.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_M_SPEC_VPR_PKG")
public class NoemVprPkgSpecEntity{
	
	@Id
	@Column(name = "MOD_EXP_CD")
	private String nvpsModExpCd;
	
	@Column(name = "SUP_PLNT_CD")
	private String nvpsSupPlntCd;
	
	@Column(name = "SUP_DOC_CD")
	private String nvpsSupDocCd;
	
	@Column(name = "MOD_IMP_CD")
	private String nvpsModImpCd;
	
	@Column(name = "MOD_RECV_PLNT")
	private String nvpsModRecvPlnt;
	
	@Column(name = "PART_NO")
	private String nvpsPartNo;
	
	@Column(name = "CF_CD")
	private String nvpsCfCd;
	
	@Column(name = "IMP_LN_CD")
	private String nvpsImpLnCd;
	
	@Column(name = "REXP_CD")
	private String nvpsRexpCd;
	
	@Column(name = "LOT_CD")
	private String nvpsLotCd;
	
	@Column(name = "CASE_NO")
	private String nvpsCaseNo;
	
	@Column(name = "MOD_CD")
	private String nvpsModCd;
	
	@Column(name = "ORD_TY")
	private String nvpsOrdTy;
	
	@Column(name = "BOX_NO")
	private String nvpsBoxNo;
	
	@Column(name = "EFF_FROM")
	private Date nvpsEffFrom;
	
	@Column(name = "AICO_FLG")
	private String nvpsAicoFlg;
	
	@Column(name = "EFF_TO")
	private Date nvpsEffTo;
	
	@Column(name = "PCK_PLNT")
	private String nvpsPckPlnt;
	
	@Column(name = "QTY_BOX")
	private Integer nvpsQtyBox;
	
	@Column(name = "BOX_GROSS_WT")
	private Integer nvpsBoxGrossWt;
	
	@Column(name = "BOX_M3")
	private Integer nvpsBoxM3;
	
	@Column(name = "IMP_DOCK_CD")
	private String nvpsImpDocCd;
	
	@Column(name = "PRT_NM2")
	private String nvpsPrtNm2;
	
	@Column(name = "ADD")
	private String nvpsAdd;
	
	@Column(name = "BOX_MAT_CD")
	private String nvpsBoxMatCd;
	
	@Column(name = "MAT_PRT_NO")
	private String nvpsMatPrtNo;
	
	@Column(name = "MAT_SRC_CD")
	private String nvpsMatSrcCd;
	
	@Column(name = "MAT_DOC_CD")
	private String nvpsMatDocCd;
	
	@Column(name = "MAT_ADD")
	private String nvpsMatAdd;
	
	@Column(name = "BOX_MAT_QTY")
	private Integer nvpsBoxMatQty;
	
	@Column(name = "MAT_CD1")
	private String nvpsMatCd1;
	
	@Column(name = "MAT_PRT_NO1")
	private String nvpsMatPrtNo1;
	
	@Column(name = "MAT_SRC_CD1")
	private String nvpsMatSrcCd1;
	
	@Column(name = "MAT_DOC_CD1")
	private String nvpsMatDocCd1;
	
	@Column(name = "MAT_ADD1")
	private String nvpsMatAdd1;
	
	@Column(name = "BOX_MAT_QTY1")
	private Integer nvpsBoxMatQty1;
	
	@Column(name = "MAT_CD2")
	private String nvpsMatCd2;
	
	@Column(name = "MAT_PRT_NO2")
	private String nvpsMatPrtNo2;
	
	@Column(name = "MAT_SRC_CD2")
	private String nvpsMatSrcCd2;
	
	@Column(name = "MAT_DOC_CD2")
	private String nvpsMatDocCd2;
	
	@Column(name = "MAT_ADD2")
	private String nvpsMatAdd2;
	
	@Column(name = "BOX_MAT_QTY2")
	private Integer nvpsBoxMatQty2;
	
	@Column(name = "MAT_CD3")
	private String nvpsMatCd3;
	
	@Column(name = "MAT_PRT_NO3")
	private String nvpsMatPrtNo3;
	
	@Column(name = "MAT_SRC_CD3")
	private String nvpsMatSrcCd3;
	
	@Column(name = "MAT_DOC_CD3")
	private String nvpsMatDocCd3;
	
	@Column(name = "MAT_ADD3")
	private String nvpsMatAdd3;
	
	@Column(name = "BOX_MAT_QTY3")
	private Integer nvpsBoxMatQty3;
	
	@Column(name = "MAT_CD4")
	private String nvpsMatCd4;

	@Column(name = "MAT_PRT_NO4")
	private String nvpsMatPrtNo4;

	@Column(name = "MAT_SRC_CD4")
	private String nvpsMatSrcCd4;

	@Column(name = "MAT_DOC_CD4")
	private String nvpsMatDocCd4;
	
	@Column(name = "MAT_ADD4")
	private String nvpsMatAdd4;
	
	@Column(name = "MAT_QTY4")
	private Integer nvpsMatQty4;
	
	@Column(name = "MAT_CD5")
	private String nvpsMatCd5;

	@Column(name = "MAT_PRT_NO5")
	private String nvpsMatPrtNo5;

	@Column(name = "MAT_SRC_CD5")
	private String nvpsMatSrcCd5;

	@Column(name = "MAT_DOC_CD5")
	private String nvpsMatDocCd5;
	
	@Column(name = "MAT_ADD5")
	private String nvpsMatAdd5;
	
	@Column(name = "MAT_QTY5")
	private Integer nvpsMatQty5;
	
	@Column(name = "MAT_CD6")
	private String nvpsMatCd6;
	
	@Column(name = "MAT_PRT_NO6")
	private String nvpsMatPrtNo6;

	@Column(name = "MAT_SRC_CD6")
	private String nvpsMatSrcCd6;
	
	@Column(name = "MAT_DOC_CD6")
	private String nvpsMatDocCd6;
	
	@Column(name = "MAT_ADD6")
	private String nvpsMatAdd6;
	
	@Column(name = "MAT_QTY6")
	private Integer nvpsMatQty6;
	
	@Column(name = "MAT_CD7")
	private String nvpsMatCd7;
	
	@Column(name = "MAT_PRT_NO7")
	private String nvpsMatPrtNo7;
	
	@Column(name = "MAT_SRC_CD7")
	private String nvpsMatSrcCd7;
	
	@Column(name = "MAT_DOC_CD7")
	private String nvpsMatDocCd7;
	
	@Column(name = "MAT_ADD7")
	private String nvpsMatAdd7;

	@Column(name = "MAT_QTY7")
	private Integer nvpsMatQty7;

	@Column(name = "MAT_CD8")
	private String nvpsMatCd8;
	
	@Column(name = "MAT_PRT_NO8")
	private String nvpsMatPrtNo8;
	
	@Column(name = "MAT_SRC_CD8")
	private String nvpsMatSrcCd8;
	
	@Column(name = "MAT_DOC_CD8")
	private String nvpsMatDocCd8;
	
	@Column(name = "MAT_ADD8")
	private String nvpsMatAdd8;
	
	@Column(name = "MAT_QTY8")
	private Integer nvpsMatQty8;
	
	@Column(name = "MAT_CD9")
	private String nvpsMatCd9;
	
	@Column(name = "MAT_PRT_NO9")
	private String nvpsMatPrtNo9;
	
	@Column(name = "MAT_SRC_CD9")
	private String nvpsMatSrcCd9;
	
	@Column(name = "MAT_DOC_CD9")
	private String nvpsMatDocCd9;
	
	@Column(name = "MAT_ADD9")
	private String nvpsMatAdd9;
	
	@Column(name = "MAT_QTY9")
	private Integer nvpsMatQty9;
	
	@Column(name = "LBL_OPT_CTL")
	private String nvpsLblOptCtl;
	
	@Column(name = "PCK_ZONE_CD")
	private String nvpsPckZoneCd;
	
	@Column(name = "STAK_ZONE_CD")
	private String nvpsStakZoneCd;
	
	@Column(name = "KBN_NO")
	private String nvpsKbnNo;
	
	@Column(name = "TMC_SEP_NO")
	private String nvpsTmcSepNo;
	
	@Column(name = "TMC_BOX")
	private String nvpsTmcBox;
	
	@Column(name = "TMC_KUMI")
	private String nvpsTmcKumi;
	
	@Column(name = "TMC_PAT")
	private String nvpsTmcPat;
	
	@Column(name = "TMC_VLIC")
	private String nvpsTmcVlic;
	
	@Column(name = "TMC_CONT_SHI_KUB")
	private String nvpsTmcContShiKub;
	
	@Column(name = "TMC_BCK_KUB")
	private String nvpsTmcBckKub;
	
	@Column(name = "TMC_BUN_HAK")
	private String nvpsTmcBunHak;
	
	@Column(name = "TMC_KAN_CD")
	private String nvpsTmcKanCd;
	
	@Column(name = "TMC_GEN_JYO_BUL")
	private String nvpsTmcGenJyoBul;
	
	@Column(name = "UPD_BY")
	private String nvpsUpdateBy;
	
	@Column(name = "UPD_DT")
	private Date nvpsUpdateDate;
	
	@Column(name = "CMP_CD")
	private String companyCode;

}
