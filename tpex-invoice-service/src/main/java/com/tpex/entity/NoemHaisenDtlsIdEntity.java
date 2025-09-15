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

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoemHaisenDtlsIdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "ETD")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date etdDate;
	public LocalDate getEtdDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etdDate);
	}

	@Column(name = "BUYER")
	private String buyer;
	
	@Column(name = "DEP_PORT")
	private String depPort;
	
	@Column(name = "DST_PORT")
	private String dstPort;
	
	@Column(name = "VSSL_NM_OCEAN")
	private String vesselOcean;
	
	@Column(name = "VOY_NO")
	private String voyNo;
}
