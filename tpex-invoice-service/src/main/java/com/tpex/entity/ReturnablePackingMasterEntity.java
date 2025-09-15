package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "TB_M_RRACK_MOD_TYPE")
@Entity
@DynamicInsert
public class ReturnablePackingMasterEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private ReturnablePackingMasterIdEntity id;
	
	@Column(name = "MOD_DESC")
	private String modDescription;
	
	@Column(name = "VAN_TO")
	@JsonFormat(pattern=ConstantUtils.DEFAULT_DATE_FORMATE)
	private Date vanTo;
	public LocalDate getVanTo() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(vanTo);
	}
	
	@Column(name = "CREATE_BY")
	private String createBy;
	
	@Column(name = "CREATE_DT")
	@JsonFormat(pattern=ConstantUtils.DEFAULT_DATE_FORMATE)
	private Date createDate;
	public LocalDate getCreateDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(createDate);
	}

	@Column(name = "UPD_BY")
	private String updBy;
	
	@Column(name = "UPD_DT")
	@JsonFormat(pattern=ConstantUtils.DEFAULT_DATE_FORMATE)
	private Date updDate;
	public LocalDate getUpdDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updDate);
	}
	
	@Column(name = "RRACK_TYP")
	private String rrackType;
	
	@Column(name = "CMP_CD")
	private String cmpCd;

}
