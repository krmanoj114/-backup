package com.tpex.month.model.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.month.util.DateUtils;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_PROCESS_CONTROLER")
public class OemProcessCtrlEntity implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OemProcessCtrlIdEntity id;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "PKG_MTH")
	private String pkgMth;

	@Column(name = "PROGRAM_ID ")
	private String programId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Column(name = "STRT_TIME ")
	private Timestamp startTime;

	public LocalDateTime getStartTime() {
		return DateUtils.convertSqlDateToLocalDateTimeOfEntityAttribute(startTime);
	}

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Column(name = "END_TIME")
	private Timestamp endTime;

	public LocalDateTime getEndTime() {
		return DateUtils.convertSqlDateToLocalDateTimeOfEntityAttribute(endTime);
	}

	@Column(name = "STATUS")
	private int status;

	@Column(name = "PARAMETER")
	private String parameter;

	@Column(name = "SUBMIT_TIME")
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Timestamp submitTime;

	public LocalDateTime getSubmitTime() {
		return DateUtils.convertSqlDateToLocalDateTimeOfEntityAttribute(submitTime);
	}

	@Column(name = "DAEMON")
	private String deamon;

	@Column(name = "CMP_CD")
	private String companyCOde;

	@Column(name = "SYSTEM_NM")
	private String systemName;

	@Column(name = "PROGRAM_NM ")
	private String programName;

	@Column(name = "ERROR_FILE_PATH")
	private String errorFilePath;

}
