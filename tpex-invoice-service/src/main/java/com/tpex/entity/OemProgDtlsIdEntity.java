package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
@Embeddable
@Data
public class OemProgDtlsIdEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "PGD_PROGRAM_ID ")
	private String programId;
	
	@Column(name = "PGD_ENTRY_TYP ")
	private String entryTyp;
	
	
	
}
