package com.tpex.entity;


import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "TB_M_PRIVILEGE")
@Entity
public class PrivilegeMasterEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PrivilegeMasterIdEntity id;

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
