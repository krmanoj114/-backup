package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_R_INV_PART_D ")
@Entity
public class InsInvPartsDetailsEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private InsInvPartsDetailsIdEntity id;
	
	@Column(name = "INV_DT")
	private Date inpInvDate;

	@Column(name = "UNIT_PER_BOX")
	private Double inpUnitPerBox;
	
	@Column(name = "AICO_FLG")
	private String inpAicoFlg;
	
	@Column(name = "BOX_M3")
	private Double inpBocM3;
	
	@Column(name = "PRC")
	private Double inpPrc;
	
	@Column(name = "ORIGINAL_COST")
	private Double inpOriginalCost;
	
	@Column(name = "PART_NM")
	private String inpPartName;
	
	@Column(name = "BOX_GROSS_WT")
	private Double inpBoxGrossWt;
	
	@Column(name = "IMP_CD")
	private String inpImpCd;
	
	@Column(name = "EXP_CD")
	private String inpExpCd;
	
	@Column(name = "MSP_ORD_TYP")
	private String inpMspOrdTyp;
	
	@Column(name = "PCK_MTH")
	private String inpPckMth;
	
	@Column(name = "CF_CD")
	private String inpCfCd;
	
	@Column(name = "REXP_CD")
	private String inpRexpCd;
	
	@Column(name = "IMP_LN_CD")
	private String inpImpLnCd;
	
	@Column(name = "SERIES")
	private String inpSeries;
	//////////////////////////////
	@Column(name = "SS_NO")
	private String inpSsNo;
	@Column(name = "DISP_MODEL_CD")
	private String inpDispModelCd;
	@Column(name = "EXT_CLR_CD")
	private String inpExpClrCd;
	@Column(name = "INT_CLR_CD")
	private String inpIntClrCd;
	@Column(name = "LOT_SIZE")
	private Double inpLotSize;
	@Column(name = "ORD_NO")
	private String inpOrdNo;
	@Column(name = "CPO_CTRL_NO")
	private String inpCpoCtlrNo;
	@Column(name = "REQ_ETA")
	private Date inpReqEta;
	@Column(name = "RESON_CD")
	private String inpResonCd;
	@Column(name = "UPD_BY")
	private String inpUpdBy;
	@Column(name = "UPD_DT")
	private Date inpUpdDate;
	@Column(name = "NET_WT")
	private Double inpNetWt;
	@Column(name = "COE_AICO_FLG")
	private String inpCoeAicoFlg;
	@Column(name = "BUN_MOD")
	private String inpBunMod;
	@Column(name = "SCHEDULE_ETD")
	private Date inpScheduleEtd;
	@Column(name = "SCHEDULE_ETA")
	private Date inpScheduleEta;
	@Column(name = "DG_FLG")
	private String inpDgFlg;
	@Column(name = "CO_CD")
	private String inpCoCd;
	@Column(name = "HS_CD")
	private String inpHsCd;
	@Column(name = "ORG_CRITERIA")
	private String inpOrgCriteria;
	@Column(name = "CMP_CD")
	private String companyCode;
}
