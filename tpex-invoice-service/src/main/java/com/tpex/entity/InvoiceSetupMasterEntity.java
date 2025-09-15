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
@Table(name = "TB_M_INS_SETUP")
@Entity
public class InvoiceSetupMasterEntity implements Serializable{

	private static final long serialVersionUID = 1L;	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "INV_SET_ID")
	private Integer invSetupId;

	@Column(name = "SET_TYP")
	private String setupType;

	@Column(name = "DST_CD")
	private String destinationCode;

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
