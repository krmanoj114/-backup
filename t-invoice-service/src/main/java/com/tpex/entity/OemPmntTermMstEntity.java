package com.tpex.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TB_M_PAYMENT_TERM")
@Entity
public class OemPmntTermMstEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CD")
	private String ptCd;
	@Column(name = "DESCRIPTION")
	private String ptDesc;
	
	@Column(name = "NO_OF_DAYS")
	private Integer ptNoOfDay;
	
	@Column(name = "DAY_OF_MTH")
	private Integer ptDayOfMth;
	
	@Column(name = "MTH_NO")
	private Integer ptMthNo;
	
	@Column(name = "UPD_BY")
	private String ptUpdatedBy;
	
	@Column(name = "UPD_DT")
	private Date ptUpdDate;
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
