package com.tpex.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@IdClass(VprDlyVinTempId.class)
@Table(name = "TB_S_VPR_DLY_VIN")
public class VprDlyVinTempEntity {

	@Id
	@Column(name = "PROC_DT", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime procDt;
	@Id
	@Column(name = "INTR_ID", nullable = false)
	private String intrId;
	@Column(name = "EXP_CD", nullable = false)
	private String expCode;
	@Column(name = "IMP_CD", nullable = false)
	private String impCode;
	@Id
	@Column(name = "LOT_CD", nullable = false)
	private String lotCode;
	@Column(name = "VIN_TY", nullable = false)
	private String vinTy;
	@Column(name = "VIN_WMI")
	private String vinWmi;
	@Column(name = "VIN_VDS")
	private String vinVds;
	@Column(name = "VIN_MOD_YR")
	private String vinModYr;
	@Column(name = "VIN_CHK_DGT")
	private String vinChkDgt;
	@Id
	@Column(name = "VIN_FRM_NO", nullable = false)
	private String vinFrmNo;
	@Column(name = "VIN_ENG_NO")
	private String vinEngNo;
	@Column(name = "VIN_ENG_PFX")
	private String vinEngPfx;
	@Column(name = "CMP_CD")
	private String compCode;

}
