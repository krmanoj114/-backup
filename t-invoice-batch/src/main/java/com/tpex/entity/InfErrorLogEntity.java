package com.tpex.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_L_INF_ERROR_LOG")
public class InfErrorLogEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "LOG_ID")
	private Integer logId;
	
	@Column(name = "PROGRAM_ID")
	private String programId;
	
	@Column(name = "FILE_NAME")
	private String fileName;
	
	@Column(name = "LINE_IN_FILE")
	private Integer lineInFile;
	
	@Column(name = "MESSAGE")
	private String message;
	
	@Column(name = "ERROR_TYPE")
	private String errorType;
	
	@Column(name = "EXECUTE_DATE")
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	private Timestamp executeDate;
	
	@Column(name = "ERROR_DATE")
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	private Timestamp errorDate;
	
	@Column(name = "CMP_CD")
	private String cmpCd;

}
