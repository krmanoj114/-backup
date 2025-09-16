package com.tpex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_M_PLANT")
public class VanningPlantEntity {

	@Id
	@Column(name = "PLANT_CD")
	private String plantCode;

	@Column(name = "PLANT_NAME")
	private String plantName;

	@Column(name = "CMP_CD")
	private String companyCode;

	@Column(name = "PKG_FLG")
	private String packingFlag;

	@Column(name = "VNG_FLG")
	private String vanningFlag;

}
