package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Instantiates a new car family dst master upload batch dto.
 */
@Data
public class CarFamilyDstMasterUploadBatchDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The car fmly code. */
	private String carFmlyCode;
	
	/** The destination code. */
	private String destinationCode;
	
	/** The re exporter code. */
	private String reExporterCode;
	
	/** The srs name. */
	private String srsName;

}