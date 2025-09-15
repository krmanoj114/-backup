package com.tpex.admin.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.admin.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "TB_M_NATIONAL_CALANDER")
@Entity
public class NatCalEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private NatCalIdEntity id;

	@Column(name = "CRT_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp crtDt;

	public LocalDateTime getCrtDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(crtDt);
	}

	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp updDt;

	public LocalDateTime getUpdDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(updDt);
	}

	@Column(name = "WHD")
	private String whd;

	@Column(name = "CRT_BY")
	private String crtBy;

	@Column(name = "UPD_BY")
	private String updBy;

	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "CMP_CD")
	private String companyCOde;

}
