package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_M_COUNTRY_ORIGIN")
public class CountryOriginMstEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PART_NO")
	private String partNumber;

	@Column(name = "PART_NM")
	private String partName;

	@Column(name = "CNTRY_ORG")
	private String countryOrigin;

	@Column(name = "EFF_FROM")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private String effectiveFromdate;

	@Column(name = "EFF_TO")
	private String effectivetodate;

	@Column(name = "CREATE_BY")
	private String createdby;

	@Column(name = "CREATE_DT")
	private String createdDate;

	@Column(name = "UPD_BY")
	private String updatedBy;

	@Column(name = "UPD_DT")
	private String updatedDate;

	@Column(name = "CMP_CD")
	private String companyCode;

}
