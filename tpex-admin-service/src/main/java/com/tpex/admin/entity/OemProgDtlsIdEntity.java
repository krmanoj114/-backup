package com.tpex.admin.entity;

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
	
	@Column(name = "PROGRAM_ID ")
	private String programId;
	
	@Column(name = "ENTRY_TYP ")
	private String entryTyp;
	
	
	
}
