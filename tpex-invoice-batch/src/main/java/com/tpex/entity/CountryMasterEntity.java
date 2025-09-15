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
@Table(name = "TB_M_COUNTRY_CODE")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CountryMasterEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Column(name = "COUNTRY_NAME")
	private String countryName;
	
	@Id
	@Column(name = "COUNTRY_CODE")
	private String countryCode;
	
	@Column(name = "C00_CODE")
	private String cooCode;
	
	@Column(name = "CMP_CD")
	private String cmpCd;
	
	@Column(name = "UPD_BY")
	private String updatedBy;
	
	@Column(name = "UPD_DT")
	private String updatedDate;
}
