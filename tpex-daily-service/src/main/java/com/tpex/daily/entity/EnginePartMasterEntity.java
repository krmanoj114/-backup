package com.tpex.daily.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.daily.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_M_ENG_VIN")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class EnginePartMasterEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private EnginePartMasterIdEntity id;

	@Column(name = "QTY")
	private String quantity;

	@Column(name = "ENG_FLG")
	private String engineFlag;

	@Column(name = "UPD_BY")
	private String updateBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updateDate;
	public LocalDate getUpdateDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updateDate);
	}

	@Column(name = "CMP_CD")
	private String companyCode;

}
