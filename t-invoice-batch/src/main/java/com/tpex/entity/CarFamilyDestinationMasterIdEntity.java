package com.tpex.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new car family destination master id entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CarFamilyDestinationMasterIdEntity implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The car fmly code. */
	@Column(name = "CF_CD")
	private String carFmlyCode;

	/** The destination code. */
	@Column(name = "DST_CD")
	private String destinationCode;

	/** The re exporter code. */
	@Column(name = "REXP_CD")
	private String reExporterCode;

}