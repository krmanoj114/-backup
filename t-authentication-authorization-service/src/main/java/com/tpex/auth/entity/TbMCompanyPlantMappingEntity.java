package com.tpex.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_m_company_plant_mapping")
public class TbMCompanyPlantMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CPM_CD", nullable = false)
	private Long idMapping;
	@Column(name = "USER_ID", length = 10, nullable = false)
	private String userId;
	@Column(name = "CMP_CD", length = 10, nullable = false)
	private String companyCode;
	@Column(name = "PLANT_CD", length = 1, nullable = false)
	private String plantCode;
}
