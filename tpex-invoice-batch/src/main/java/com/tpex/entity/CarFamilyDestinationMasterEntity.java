package com.tpex.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import lombok.Data;

/**
 * Instantiates a new car family destination master entity.
 */
@Data
@Table(name = "TB_M_CAR_FAMILY_DESTINATION")
@Entity
public class CarFamilyDestinationMasterEntity {
	
	/** The id. */
	@EmbeddedId
	private CarFamilyDestinationMasterIdEntity id;
	
	/** The srs name. */
	@Column(name = "SRS_NM")
	private String srsName;
	
	/** The updated by. */
	@Column(name = "UPD_BY")
	private String updatedBy;
	
	/** The updated date. */
	@Column(name = "UPD_DT")
	private String updatedDate;
	
	/** The company code. */
	@Column(name = "CMP_CD")
	private String companyCode;

}
