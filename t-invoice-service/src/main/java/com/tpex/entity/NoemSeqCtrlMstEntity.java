package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "TB_M_SEQUENCE_CONTROLLER")
@Entity
public class NoemSeqCtrlMstEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private NoemSeqCtrlMstIdEntity id;
	
	@Column(name = "SEQ_DESC")
	private String scmSeqDesc;
	
	@Column(name = "SEQ_NO")
	private Integer scmSeqNo;
	
	@Column(name = "UPD_BY")
	private String scmUpdBy;
	
	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date scmUpdDt;
	public LocalDate getScmUpdDt() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(scmUpdDt);
	}
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
