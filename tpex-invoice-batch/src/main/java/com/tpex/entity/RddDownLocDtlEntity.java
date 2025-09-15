package com.tpex.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

/**
 * Instantiates a new rdd down loc dtl entity.
 */
@Data
@Table(name = "TB_R_REPORT_DOWN_D")
@Entity
public class RddDownLocDtlEntity implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The report id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "REPORT_ID")
	private int reportId;
	
	/** The report name. */
	@Column(name = "REPORT_NAME")
	private String reportName;

	/** The status. */
	@Column(name = "STATUS")
	private String status;

	/** The down loc. */
	@Column(name = "DOWN_LOC")
	private String downLoc;

	/** The create by. */
	@Column(name = "CREATE_BY")
	private String createBy;
	
	/** The create date. */
	//@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	@Column(name = "CREATE_DATE")
	private java.sql.Timestamp  createDate;

	/** The update by. */
	@Column(name = "UPDATE_BY")
	private String updateBy;
	
	/** The update date. */
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	@Column(name = "UPDATE_DATE")
	private java.sql.Timestamp  updateDate;

	/** The company code. */
	@Column(name = "CMP_CD")
	private String companyCode;

	/**
	 * Gets the creates the date.
	 *
	 * @return the creates the date
	 */
	public LocalDateTime getCreateDate() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(createDate);
	}

	/**
	 * Gets the update date.
	 *
	 * @return the update date
	 */
	public LocalDateTime getUpdateDate() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(updateDate);
	}
	 
}
