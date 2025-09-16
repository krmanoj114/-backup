package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_M_MTH_RENBAN_SETUP")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class NoemRenbanSetupMstEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private NoemRenbanSetupMstIdEntity id;
	
	@Column(name = "EFF_TO_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	@NotNull
	private Date effToDt;
	public LocalDate getEta() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effToDt);
	}
	
	@Column(name = "UPD_BY")
	@NotNull
	private String updatedBy;
	
	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	@NotNull
	private Date updatedDate;
	public LocalDate getUpdatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updatedDate);
	}
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
	@Column(name = "FOLDER_NAME")
	private String folderName;
	
}
