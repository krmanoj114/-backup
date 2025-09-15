package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class MixPrivilegeMasterIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;

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

	@Column(name = "DST_CD")
	private String destinationCode;

	@Column(name = "CF_CD")
	private String carFmlyCode;

	@Column(name = "RE_EXP_CD")
	private String exporterCode;
	
	@Column(name = "CMP_CD")
	private String companyCode;

}
