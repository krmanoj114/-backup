package com.tpex.admin.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.admin.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "TB_R_REPORT_DOWN_D")
@Entity
public class RddDownLocDtlEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REPORT_ID")
	private int reportId;
	@Column(name = "REPORT_NAME")
	private String reportName;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "DOWN_LOC")
	private String downLoc;

	@Column(name = "CREATE_BY")
	private String createBy;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(name = "CREATE_DATE")
	private Timestamp createDate;

	@Column(name = "UPDATE_BY")
	private String updateBy;

	@Column(name = "UPDATE_DATE")
	private Timestamp updateDate;

	@Column(name = "CMP_CD")
	private String companyCode;

	public LocalDateTime getUpdateDate() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(updateDate);
	}

}
