package com.tpex.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@SuppressWarnings("squid:S107")
@Table(name = "TB_M_FINAL_DESTINATION")
@Entity
public class OemFnlDstMstEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	private Date fdUpdDt;
	
    @Column(name = "LEAD_DAYS")
	private Integer fdLeadDays;
	
    @Column(name = "CNTRY_ORG")
	private String fdCntryOrg;
    
    @Column(name = "CMP_CD")
	private String companyCode;
	
	public OemFnlDstMstEntity() {
		
	}

	public OemFnlDstMstEntity(String fdDstCd, String fdDstNm, String fdBuyCd, String fdCurrCd, String fdCpoModCd,
			String fdUpdBy, Date fdUpdDt, Integer fdLeadDays, String fdCntryOrg) {
		super();
		this.fdDstCd = fdDstCd;
		this.fdDstNm = fdDstNm;
		this.fdBuyCd = fdBuyCd;
		this.fdCurrCd = fdCurrCd;
		this.fdCpoModCd = fdCpoModCd;
		this.fdUpdBy = fdUpdBy;
		this.fdUpdDt = fdUpdDt;
		this.fdLeadDays = fdLeadDays;
		this.fdCntryOrg = fdCntryOrg;
	}

	public String getFdDstCd() {
		return fdDstCd;
	}

	public void setFdDstCd(String fdDstCd) {
		this.fdDstCd = fdDstCd;
	}

	public String getFdDstNm() {
		return fdDstNm;
	}

	public void setFdDstNm(String fdDstNm) {
		this.fdDstNm = fdDstNm;
	}

	public String getFdBuyCd() {
		return fdBuyCd;
	}

	public void setFdBuyCd(String fdBuyCd) {
		this.fdBuyCd = fdBuyCd;
	}

	public String getFdCurrCd() {
		return fdCurrCd;
	}

	public void setFdCurrCd(String fdCurrCd) {
		this.fdCurrCd = fdCurrCd;
	}

	public String getFdCpoModCd() {
		return fdCpoModCd;
	}

	public void setFdCpoModCd(String fdCpoModCd) {
		this.fdCpoModCd = fdCpoModCd;
	}

	public String getFdUpdBy() {
		return fdUpdBy;
	}

	public void setFdUpdBy(String fdUpdBy) {
		this.fdUpdBy = fdUpdBy;
	}

	public Date getFdUpdDt() {
		return fdUpdDt;
	}

	public void setFdUpdDt(Date fdUpdDt) {
		this.fdUpdDt = fdUpdDt;
	}

	public Integer getFdLeadDays() {
		return fdLeadDays;
	}

	public void setFdLeadDays(Integer fdLeadDays) {
		this.fdLeadDays = fdLeadDays;
	}

	public String getFdCntryOrg() {
		return fdCntryOrg;
	}

	public void setFdCntryOrg(String fdCntryOrg) {
		this.fdCntryOrg = fdCntryOrg;
	}

    
}
