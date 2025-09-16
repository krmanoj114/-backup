package com.tpex.daily.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_M_FINAL_DESTINATION")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FinalDestEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "DST_CD")
	private String destinationCd;

	@Column(name = "DST_NM")
	private String destinationName;

	@Column(name = "BUY_CD")
	private String buyCd;

	@Column(name = "CURR_CD")
	private String currencyCd;

	@Column(name = "CPO_MOD_CD")
	private String cpoModCd;

	@Column(name = "UPD_BY")
	private String updatedBy;

	@Column(name = "UPD_DT")
	private Date updatedDate;

	@Column(name = "LEAD_DAYS")
	private BigInteger leadDays;

	@Column(name = "CNTRY_ORG")
	private String countryOrg;

	@Column(name = "CMP_CD")
	private String cmpCd;

}

