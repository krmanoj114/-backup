package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_M_FINAL_DESTINATION")
public class PackingAndVanningEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DST_CD")
	private String destinationCode;

	@Column(name = "DST_NM")
	private String destinationName;

	@Column(name = "BUY_CD")
	private String buyerCode;

	@Column(name = "CURR_CD")
	private String currencyCode;

	@Column(name = "CPO_MOD_CD")
	private String cpoModeCode;

	@Column(name = "UPD_BY")
	private String updateBy;

	@Column(name = "UPD_DT")
	private String updateDate;

	@Column(name = "LEAD_DAYS")
	private String leadDays;

	@Column(name = "CNTRY_ORG")
	private String countryOrigin;

	@Column(name = "CMP_CD")
	private String cmpCode;

}
