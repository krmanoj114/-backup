package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "TB_M_INS_SETUP_D")
@Entity
public class InvoiceSetupDetailEntity implements Serializable{

	private static final long serialVersionUID = 1L;	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "INV_DET_ID")
	private Integer id;

	@Column(name = "CFC")
	private String carFamilyCode;

	@Column(name = "EXP_CD")
	private String exporterCode;

	@Column(name = "RE_EXP_CD")
	private String reExporterCode;
	
	@Column(name = "LINE_CD")
	private String lineCode;
	
	@Column(name = "PKG_MTH")
	private String packagingMnth;
	
	@Column(name = "PRICE_MTHD")
	private String priceMethod;
	
	@Column(name = "EFF_FROM_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date effFrom;
	public LocalDate getEffFrom() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effFrom);
	}

	@Column(name = "EFF_TO_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date effTo;
	public LocalDate getEffTo() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effTo);
	}
	
	@Column(name = "INV_SET_ID")
	private Integer invSetupId;
	
	@Column(name = "UPD_BY")
	private String updatedBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updatedDate;
	public LocalDate getUpdatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updatedDate);
	}
	
	@Column(name = "CMP_CD")
	private String companyCode;

}
