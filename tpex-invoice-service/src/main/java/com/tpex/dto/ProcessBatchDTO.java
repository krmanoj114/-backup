package com.tpex.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

@Data
public class ProcessBatchDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String processId;
	private String processName;
	private List<String> parameters;
	private String parameter;
	private String userId;
	private int processControlId;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Timestamp processSubmitTime;

	public LocalDateTime getProcessSubmitTime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(processSubmitTime);
	}

	private String status;

	public ProcessBatchDTO(int processControlId, String processId, String processName, String parameter, String userId,
			Timestamp processSubmitTime, String status) {
		super();
		this.processControlId = processControlId;
		this.processId = processId;
		this.processName = processName;
		this.parameter = parameter;
		this.userId = userId;
		this.processSubmitTime = processSubmitTime;
		this.status = status;
	}

}
