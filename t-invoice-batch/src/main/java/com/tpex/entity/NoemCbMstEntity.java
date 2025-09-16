package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TB_M_CUSTOM_BROCKER")
@Entity
public class NoemCbMstEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "CB_CD")
	private String cbCd;
	@Column(name = "CB_NM")
	private String cbNm;

	@Column(name = "FIRST_FILE_NM")
	private String firstFileNm;

	@Column(name = "SECOND_FILE_NM")
	private String secondFileNm;

	@Column(name = "THRD_FILE_NM")
	private String thrdFileNm;
	
	
	@Column(name = "FOURTH_FILE_NM")
	private String fourthFileNm;












}
