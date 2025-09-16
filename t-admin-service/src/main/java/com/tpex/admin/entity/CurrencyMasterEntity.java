package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name= "TB_M_CURRENCY")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyMasterEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "CD")
	private String currencyCode;	
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "CURR_PRNT_NM")
	private String currentPrintName;
	
	@Column(name = "UPD_BY")
	private String updatedBY;
	
	@Column(name = "UPD_DT")
	private String updatedDate;
	
	@Column(name = "CMP_CD")
	private String cmpCd;

}
