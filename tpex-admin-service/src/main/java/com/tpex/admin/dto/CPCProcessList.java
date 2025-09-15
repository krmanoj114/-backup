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
public class CPCProcessList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String processControlId;
	
	private String processId;
	
	private String processName;
	
	@JsonFormat(pattern="dd/MM/yyyy HH:mm")
	private Timestamp processSubmitTime;
	public LocalDateTime getProcessSubmitTime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(processSubmitTime);
	}
	
	private String status;
	
	private String owner;
	
	private String batchParameters;
	
	private String systemName;

}
