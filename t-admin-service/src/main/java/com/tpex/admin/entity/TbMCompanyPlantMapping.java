package com.tpex.admin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_COMPANY_PLANT_MAPPING")
public class TbMCompanyPlantMapping {

	@Id
	@Column(name = "CPM_ID")
	private Long cpmId;
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "CMP_CD")
	private String companyCode;
	@Column(name = "PLANT_CD")
	private String plantCode;
}
