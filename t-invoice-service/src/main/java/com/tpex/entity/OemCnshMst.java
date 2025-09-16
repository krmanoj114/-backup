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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_M_CNSG")
@Entity
public class OemCnshMst implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
	@EmbeddedId
	private OemCnshIdMstEntity id;
	
	
	@Column(name = "NAME")
	private String cmpName;
	@Column(name = "ADD_1")
	private String cmpAdd1;
	@Column(name = "ADD_2")
	private String cmpAdd2;
	@Column(name = "ADD_3")
	private String cmpAdd3;
	@Column(name = "ADD_4")
	private String cmpAdd4;
	@Column(name = "ZIP")
	private String cmpZip;
	@Column(name = "SHRT_NM")
	private String cmpShrtNm;
	@Column(name = "CNTRY_CD")
	private String cmpCntryCd;
	
	@Column(name = "TEL_NO")
	private String cmpTelNo;
	@Column(name = "FAX_NO")
	private String cmpFxNo;
	@Column(name = "TELEX")
	private String cmpTelex;
	@Column(name = "EMAIL")
	private String cmpEmail;
	@Column(name = "CONTACT")
	private String cmpContact;
	@Column(name = "SAP_CD")
	private String cmpSapCd;
	@Column(name = "CNSG_CD")
	private String cmpCnsgCd;
	@Column(name = "UPD_BY")
	private String cmpUpdBy;
	@Column(name = "UPD_DT")
	private Date cmpUpdDate;
	
	@Column(name = "INV_ADD_FLG")
	private String invAddFlg;
	@Column(name = "SC_INV_CNTRY_FLG")
	private String scInvCntryFlg;
	@Column(name = "SC_REMARKS")
	private String cmpScRemark;
	@Column(name = "SC_AUTH_EMP1")
	private String cmpScAuthEmp1;
	@Column(name = "SC_AUTH_EMP2")
	private String cmpScAuthEmp2;
	@Column(name = "SC_AUTH_EMP3")
	private String cmpScAuthEmp3;
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
