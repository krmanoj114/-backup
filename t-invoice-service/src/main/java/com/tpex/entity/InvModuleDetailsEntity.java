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
@Table(name = "TB_R_INV_MODULE_D")
@Entity
public class InvModuleDetailsEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private InvModuleDetailsIdEntity id;
	
	@Column(name = "INV_DT")
	private Date invDate;
	
	@Column(name = "GROSS_WT")
	private Double grossWt;

	@Column(name = "TOT_M3")
	private Double totalM3;
	
	@Column(name = "NET_WT")
	private Double netWt;
	
	@Column(name = "AICO_FLG")
	private String aicoFlg;
	
	@Column(name = "PCK_MTH")
	private String pckMth;
	
	@Column(name = "REXP_CD")
	private String rexpCd;
	
	@Column(name = "CF_CD")
	private String cfCd;
	
	@Column(name = "IMP_LN_CD")
	private String impLnCd;
	
	@Column(name = "ORD_SRC")
	private String ordSrc;
	
	@Column(name = "MSP_ORD_TYP")
	private String mspOrdTyp;
	
	@Column(name = "MOD_SIZE")
	private String modSize;
	
	@Column(name = "MOD_TYP")
	private String modType;
	
	@Column(name = "MOD_EFF_WT")
	private Double modEffWt;
	
	@Column(name = "MOD_EFF_M3")
	private Double modEffM3;
	
	@Column(name = "KIT_CD")
	private String kitCd;
	
	@Column(name = "KIT_SRL_NO")
	private String kitSrlNo;
	
	@Column(name = "CONT_SNO")
	private String contSno;
	
	@Column(name = "CONT_DST")
	private String contDst;
	
	@Column(name = "LENGTH")
	private Double length;
	
	@Column(name = "HEIGHT")
	private Double height;
	
	@Column(name = "WIDTH")
	private Double width;
	
	@Column(name = "PLN_PKG_DT")
	private Date planPkgDate;
	
	@Column(name = "DG_FLG")
	private String dgFlg;
	
	@Column(name = "UNIT_LOT")
	private Double unitLot;
	
	@Column(name = "ACT_PKG_DT")
	private Date actPkgDate;
	
	@Column(name = "PCK_PLNT")
	private String pckPlant;
	
	@Column(name = "BUN_MOD")
	private String bunMod;
	
	@Column(name = "PCK_LN_CD")
	private String pckLnCd;
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
	@Column(name = "UPD_BY")
	private String updBy;
	
	@Column(name = "UPD_DT")
	private Date updDate;
	
}
