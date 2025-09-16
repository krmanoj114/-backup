package com.tpex.admin.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.admin.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("squid:S107")
public class ProcessBatchDTO {

	private String processId;
	private String processName;
	private List<ProcessControlParameter> parameters;
	private String parameter;
	private String userId;
	private int processControlId;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Timestamp processSubmitTime;

	public LocalDateTime getProcessSubmitTime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(processSubmitTime);
	}

	private String status;
	private String filePath;

	public ProcessBatchDTO(int processControlId, String processId, String processName, String parameter, String userId,
			Timestamp processSubmitTime, String status, String filePath) {
		super();
		this.processControlId = processControlId;
		this.processId = processId;
		this.processName = processName;
		this.parameter = parameter;
		this.userId = userId;
		this.processSubmitTime = processSubmitTime;
		this.status = status;
		this.filePath = filePath;
	}

}
