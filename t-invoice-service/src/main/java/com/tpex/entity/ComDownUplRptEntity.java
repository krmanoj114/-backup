package com.tpex.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "COM_DOWN_UPL_RPT")
@Entity
public class ComDownUplRptEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ComDownUplRptIdEntity comDownUplRptIdEntity;

	@Column(name = "CDUR_RPT_NAME")
	private String rptName;

	@Column(name = "CDUR_UPD_BY")
	private String updatedBy;
	@Column(name = "RDD_UPDATE_DATE")
	private Date rddUpdateDate;

}
