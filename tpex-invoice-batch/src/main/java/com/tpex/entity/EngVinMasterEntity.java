package com.tpex.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_M_ENG_VIN")
public class EngVinMasterEntity {
	
	@EmbeddedId
	private EngVinMasterIdEntity id;
	
	@Column(name = "QTY")
	private String qty;
	@Column(name = "ENG_FLG")
	private String engFlag;
	@Column(name = "UPD_BY")
	private String updateBy;
	@Column(name = "UPD_DT", columnDefinition = "DATE")
	private LocalDate updateDate;
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
