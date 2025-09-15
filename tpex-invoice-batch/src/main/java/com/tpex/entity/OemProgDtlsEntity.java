package com.tpex.entity;



import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_R_PROGRAM_D")
public class OemProgDtlsEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OemProgDtlsIdEntity id;

	@Column(name = "PROGRAM_DESC")
	private String programDesc;

	@Column(name = "CMP_CD")
	private String companyCode;
	
}