package com.tpex.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoemHaisenDtlsEntityDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String etdDate;

	private String buyer;
	
	private String depPort;
	
	private String dstPort;
	
	private String vesselOcean;
	
	private String voyNo;
	
	private String haisenYearMonth;
	
	private String haisenNo;

	private String etaDate;
	
	private String vesselFeeder;
	
	private String shipCoNM;
	
	private Integer noOf20FtContainer;
	
	private Integer noOf40FtContainer;
	
	private BigDecimal containerEffeciency;
	
	private Integer lclVol;
	
	private String updatedBy;
	
	private Date updatedDate;
}