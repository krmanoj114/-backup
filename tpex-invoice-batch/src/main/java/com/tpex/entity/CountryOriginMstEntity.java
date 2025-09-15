package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="TB_M_COUNTRY_ORIGIN")
public class CountryOriginMstEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PART_NO")
	private String partNumber;

	@Column(name="PART_NM")
	private String partName;

	@Column(name="CNTRY_ORG")
	private String countryOrigin;

	@Column(name = "EFF_FROM")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date effectiveFromdate;
	public LocalDate getEffectiveFromdate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effectiveFromdate);
	}

	@Column(name = "EFF_TO")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date effectivetodate;
	public LocalDate getEffectivetodate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effectivetodate);
	}

	@Column(name="CREATE_BY")
	private String createdby;

	@Column(name = "CREATE_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date createdDate;
	public LocalDate getCreatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(createdDate);
	}

	@Column(name="UPD_BY")
	private String updatedBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updatedDate;
	public LocalDate getUpdatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updatedDate);
	}

	@Column(name="CMP_CD")
	private String companyCode;



}
