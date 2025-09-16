package com.tpex.admin.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.admin.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPCProcessLogsResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int processLogId;
	private String requestId;
	private String processId;
	private String processName;
	private String correlationId;
	private String logMessage;
	private String status;
	
	@JsonFormat(pattern="dd/MM/yyyy HH:mm")
	private Timestamp logTime;
	public LocalDateTime getLogTime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(logTime);
	}

}
