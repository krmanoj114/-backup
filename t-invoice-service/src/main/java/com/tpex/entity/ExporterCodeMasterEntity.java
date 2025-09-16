package com.tpex.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Table(name = "TB_M_EXPORTER")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ExporterCodeMasterEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;	

	@Id
	@Column(name = "EXP_CODE")
	private String expCode;
	
	@Column(name = "EXP_NAME")
	private String expName;

	@Column(name = "UPD_BY")
	private String updateBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp updateDate;
	
	public LocalDateTime getIndInvDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(updateDate);
	}
	
	@Column(name = "CMP_CD")
	private String cmpCode;
}
