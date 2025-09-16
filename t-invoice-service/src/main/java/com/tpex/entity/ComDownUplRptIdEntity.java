package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;

public class ComDownUplRptIdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "CDUR_RPT_ID")
	private Integer rptId;
	@Column(name = "CDUR_BATCH_ID")
	private String batchId;

}
