package com.tpex.admin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_PLANT")
public class TbMPlant {

	@Id
	@Column(name = "PLANT_CD")
	private String plantCode;
	@Column(name = "PLANT_NAME")
	private String plantName;
	@Column(name = "CMP_CD")
	private String companyCode;
}
