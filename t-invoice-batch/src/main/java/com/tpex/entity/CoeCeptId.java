package com.tpex.entity;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class CoeCeptId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5261106654792835468L;	
	
	private String cfCd;
	private String series;
	private String partNo;
	private String impCmp;
	private String expCmp;
	private LocalDate partEffDt;	

}
