package com.tpex.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "TPEX_NAT_CAL")
@Entity
public class NatCalEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private NatCalIdEntity id;

	@Column(name = "TNC_CRT_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp crtDt;
	
	public LocalDateTime getCrtDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(crtDt);
	}


	@Column(name = "TNC_UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp updDt;
	
	public LocalDateTime getUpdDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(updDt);
	}

	@Column(name = "TNC_WHD")
	private String whd;


	@Column(name = "TNC_CRT_BY")
	private String crtBy;

	@Column(name = "TNC_UPD_BY")
	private String updBy;
	
	

	@Column(name = "TNC_REMARKS")
	private String remarks;


}
