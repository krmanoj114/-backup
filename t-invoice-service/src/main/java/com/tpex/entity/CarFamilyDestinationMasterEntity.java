package com.tpex.entity;


import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import lombok.Data;

@Data
@Table(name = "TB_M_CAR_FAMILY_DESTINATION")
@Entity
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
