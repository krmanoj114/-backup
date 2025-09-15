package com.tpex.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import lombok.Data;

@Data
@Embeddable
public class CarFamilyDestinationMasterIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name = "CF_CD")
	private String carFmlyCode;

	@Column(name = "DST_CD")
	private String destinationCode;

	@Column(name = "REXP_CD")
	private String reExporterCode;

}
