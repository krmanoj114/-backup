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

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "TB_M_PARAMETER")
@Entity
public class OemParameterEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "PARA_CD")
	private String oprParaCd;
	
	@Column(name = "PARA_NM")
	private String oprParaNm;
	
	@Column(name = "PARA_TYP")
	private String oprParaTyp;
	
	@Column(name = "PARA_VAL")
	private String oprParaVal;
	
	@Column(name = "VISIBLE_FLG")
	private String oprVisibleFlg;
	
	@Column(name = "UPD_BY")
	private String oprUpdBy;
	
	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date oprUpdDt;
	public LocalDate getOprUpdDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(oprUpdDt);
	}
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
