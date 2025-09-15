package com.tpex.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

@Data
@Entity
@Table(name = "OEM_PROCESS_CTRL_TPEX")
public class OemProcessCtrlEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OemProcessCtrlIdEntity id;

	@Column(name = "PRC_USER_ID")
	private String userId;

	@Column(name = "PRC_PKG_MTH")
	private String pkgMth;

	@Column(name = "PRC_PROGRAM_ID ")
	private String programId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Column(name = "PRC_STRT_TIME ")
	private Timestamp startTime;

	public LocalDateTime getStartTime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(startTime);
	}

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Column(name = "PRC_END_TIME")
	private Timestamp endTime;

	public LocalDateTime getEndTime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(endTime);
	}

	@Column(name = "PRC_STATUS")
	private int status;

	@Column(name = "PRC_PARAMETER")
	private String parameter;

	@Column(name = "PRC_SUBMIT_TIME")
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Timestamp submitTime;

	public LocalDateTime getSubmitTime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(submitTime);
	}

	@Column(name = "PRC_DAEMON")
	private String deamon;

}
