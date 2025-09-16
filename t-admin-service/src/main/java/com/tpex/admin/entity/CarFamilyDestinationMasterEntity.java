package com.tpex.admin.entity;


import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_M_CAR_FAMILY_DESTINATION")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CarFamilyDestinationMasterEntity {
	
	
	@EmbeddedId
	private CarFamilyDestinationMasterIdEntity id;
	
	@Column(name = "SRS_NM")
	private String srsName;
	
	@Column(name = "UPD_BY")
	private String updatedBy;
	
	@Column(name = "UPD_DT")
	private String updatedDate;
	
	@Column(name = "CMP_CD")
	private String companyCode;

}
