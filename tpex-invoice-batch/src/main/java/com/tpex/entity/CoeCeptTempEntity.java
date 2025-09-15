package com.tpex.entity;


import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_S_COE_CEPT")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CoeCeptId.class)
public class CoeCeptTempEntity {	
	
	@Id
	@Column(name = "CF_CD")
	private String cfCd;
	
	@Id
	@Column(name = "SERIES")
	private String series;
	
	@Id
	@Column(name = "PART_NO")
	private String partNo;
	
	@Id
	@Column(name = "IMP_CMP")
	private String impCmp;
	
	@Id
	@Column(name = "EXP_CMP")
	private String expCmp;
	
	@Column(name = "PART_FLG")
	private String partFlg;
	
	@Id
	@Column(name = "PART_EFF_DT")
	private LocalDate partEffDt;
	
	@Column(name = "PART_EXP_DT")
	private LocalDate partExpDt;
	
	@Column(name = "PART_NM")
	private String partNm;
	
	@Column(name = "UPD_BY")
	private String updBy;
	
	@Column(name = "UPD_DT")
	private LocalDate updDt;
	
	@Column(name = "HS_CD")
	private String hsCd;
	
	@Column(name = "ORG_CRITERIA")
	private String orgCriteria;
	
	@Column(name = "CMP_CD")
	private String compCode;

}
