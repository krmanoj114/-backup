package com.tpex.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
@Table(name = "TB_M_CARFAMILY")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CarFamilyMasterEntity {
	
	@Id
	@Column(name = "CD")
	private String carFmlyCode;
	
	@Column(name = "SRS_NM")
	private String carFmlySrsName;

	@Column(name = "REP")
	private String carFmlyRep;

	@Column(name = "UPD_BY")
	private String carFmlyUpdateBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Timestamp carFmlyUpdateDate;

	public LocalDateTime getIndInvDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(carFmlyUpdateDate);
	}

	@Column(name = "TI_REP")
	private String carFmlyTiRep;

	@Column(name = "STATUS")
	private String carFmlyStatus;
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
