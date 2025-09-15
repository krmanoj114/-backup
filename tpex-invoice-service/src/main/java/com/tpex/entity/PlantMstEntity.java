package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TB_M_PLANT")
@Entity
public class PlantMstEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "PLANT_CD")
	private String plantCd;
	@Column(name = "PLANT_NAME")
	private String plantName;
	@Column(name = "CMP_CD")
	private String cmpCd;
	
}
