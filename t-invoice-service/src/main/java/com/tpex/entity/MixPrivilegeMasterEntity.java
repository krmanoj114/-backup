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
@Table(name = "TB_M_MIX_PRIVILEGE")
@Entity
public class MixPrivilegeMasterEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PRIV_MST_ID")
	private Integer privMstId;

	@Column(name = "DST_CD")
	private String destinationCode;

	@Column(name = "CF_CD")
	private String carFmlyCode;

	@Column(name = "RE_EXP_CD")
	private String exporterCode;

	@Column(name = "CMP_CD")
	private String companyCode;

	@Column(name = "EFF_FROM")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date effFrom;
	public LocalDate getEffFrom() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effFrom);
	}

	@Column(name = "EFF_TO")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date effTo;
	public LocalDate getEffTo() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effTo);
	}

	@Column(name = "PRIORITY_ONE")
	private String priorityOne;

	@Column(name = "PRIORITY_TWO")
	private String priorityTwo;

	@Column(name = "PRIORITY_THREE")
	private String priorityThree;

	@Column(name = "PRIORITY_FOUR")
	private String priorityFour;

	@Column(name = "PRIORITY_FIVE")
	private String priorityFive;

	@Column(name = "UPD_BY")
	private String updatedBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updatedDate;
	public LocalDate getUpdatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updatedDate);
	}

}
