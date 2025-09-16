package com.tpex.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_m_plant")
public class TbMPlantEntity {

	@Id
	@Column(name = "PLANT_CD", length = 1, nullable = false)
	private String plantCode;
	@Column(name = "PLANT_NAME", length = 20)
	private String plantName;
	@Column(name = "CMP_CD", length = 10, nullable = false)
	private String companyCode;
}
