package com.tpex.entity;

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
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new process log dtls entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_L_PROCESS_LOG")
public class ProcessLogDtlsEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROCESS_LOG_ID")
	private Integer id;
	
	/** The process id. */
	@Column(name = "PROCESS_ID")
	private String processId;

	/** The process name. */
	@Column(name = "PROCESS_NAME")
	private String processName;
	
	/** The process log status. */
	@Column(name = "PROCESS_LOG_STATUS")
	private String processLogStatus;
	
	/** The process log message. */
	@Column(name = "PROCESS_LOG_MESSAGE")
	private String processLogMessage;
	
	/** The correlation id. */
	@Column(name = "CORRELATION_ID")
	private String correlationId;
	
	/** The process control id. */
	@Column(name = "PROCESS_CTRL_ID")
	private int processControlId;
	
	/** The process log datetime. */
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	@Column(name = "PROCESS_LOG_DATETIME")
	private Timestamp processLogDatetime;
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
	@Column(name = "LINE_NO")
	private String lineNo;
	
	@Column(name = "COLUMN_NAME")
	private String columnName;
	
	/**
	 * Gets the process log datetime.
	 *
	 * @return the process log datetime
	 */
	public LocalDateTime getProcessLogDatetime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(processLogDatetime);
	}

}
