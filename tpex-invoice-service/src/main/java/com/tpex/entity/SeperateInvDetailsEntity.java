package com.tpex.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@NotEmpty
@Table(name = "TB_R_INS_SEP_INV_D")
@IdClass(SeperateInvDetailsIdEntity.class)
public class SeperateInvDetailsEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "INV_NO")
	private String invNo;
	@Id
	@Column(name = "AICO_FLG")
	private String aicoFlg;

	@Column(name = "DISP_INV_NO")
	private String dispInvNo;

	@Column(name = "INV_AMT")
	private Double invAmt;

	@Column(name = "INV_QTY")
	private Double invQty;

	@Column(name = "GROSS_WT")
	private Double grossWt;

	@Column(name = "NET_WT")
	private Double netWt;

	@Column(name = "MEASUREMENT")
	private Double measurement;

	@Column(name = "NO_CASES")
	private Double noCases;

	@Column(name = "NO_UNITS")
	private Double noUnits;

	@Column(name = "DOC_NO")
	private String docNo;

	@Column(name = "DOC_DT")
	private Date docDt;

	@Column(name = "UPD_BY")
	private String updBy;

	@Column(name = "UPD_DT")
	private Date updDt;

	@Column(name = "CUST_TOT_AMT")
	private Double custTotAmt;

	@Column(name = "CUST_FREIGHT")
	private Double custFreight;

	@Column(name = "CUST_INSURANCE")
	private Double custInsurance;

	@Column(name = "CMP_CD")
	private String cmpCd;

}
