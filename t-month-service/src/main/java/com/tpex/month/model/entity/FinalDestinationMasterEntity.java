package com.tpex.month.model.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_FINAL_DESTINATION")
public class FinalDestinationMasterEntity {

	@Id
	@Column(name = "DST_CD")
	private String fdDstCd;
	@Column(name = "DST_NM")
	private String fdDstNm;
	@Column(name = "BUY_CD")
	private String fdBuyCd;
	@Column(name = "CURR_CD")
	private String fdCurrCd;
	@Column(name = "CPO_MOD_CD")
	private String fdCpoModCd;
	@Column(name = "UPD_BY")
	private String fdUpdBy;
	@Column(name = "UPD_DT")
	private Timestamp fdUpdDt;
	@Column(name = "LEAD_DAYS")
	private Integer fdLeadDays;
	@Column(name = "CNTRY_ORG")
	private String fdCntryOrg;
	@Column(name = "CMP_CD")
	private String cmpCd;

}
