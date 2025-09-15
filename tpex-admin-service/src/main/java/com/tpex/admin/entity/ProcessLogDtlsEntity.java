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
@Entity
@Table(name = "TB_L_PROCESS_LOG")
public class ProcessLogDtlsEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROCESS_LOG_ID")
	private Integer id;
	
	@Column(name = "PROCESS_ID")
	private String processId;

	@Column(name = "PROCESS_NAME")
	private String processName;
	
	@Column(name = "PROCESS_LOG_STATUS")
	private String processLogStatus;
	
	@Column(name = "PROCESS_LOG_MESSAGE")
	private String processLogMessage;
	
	@Column(name = "CORRELATION_ID")
	private String correlationId;
	
	@Column(name = "PROCESS_CTRL_ID")
	private int processControlId;
	
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	@Column(name = "PROCESS_LOG_DATETIME")
	private Timestamp processLogDatetime;
	public LocalDateTime getProcessLogDatetime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(processLogDatetime);
	}

}
