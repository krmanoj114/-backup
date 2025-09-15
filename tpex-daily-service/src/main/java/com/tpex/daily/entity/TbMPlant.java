package com.tpex.daily.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
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
