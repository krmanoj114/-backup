package com.tpex.admin.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CarFamilyDestinationMasterIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name = "CF_CD")
	private String carFmlyCode;

	@Column(name = "DST_CD")
	private String destinationCode;

	@Column(name = "REXP_CD")
	private String reExporterCode;

}
