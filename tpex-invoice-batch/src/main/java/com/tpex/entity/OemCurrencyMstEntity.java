package com.tpex.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_M_CURRENCY")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OemCurrencyMstEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CD")
	private String crmCd;
	@Column(name = "DESCRIPTION")
	private String crmDesc;
	
	@Column(name = "CURR_PRNT_NM")
	private String crmCurrPrntNm;
	
	@Column(name = "UPD_BY")
	private String crmUpdBy;
	
	@Column(name = "UPD_DT")
	private Date crmUpdDate;
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
