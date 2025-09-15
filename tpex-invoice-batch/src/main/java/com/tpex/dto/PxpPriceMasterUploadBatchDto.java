package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Instantiates a new pxp price master upload batch dto.
 */
@Data
public class PxpPriceMasterUploadBatchDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cf code. */
	private String cfCode;
	
	/** The dest code. */
	private String destCode;
	
	/** The currency code. */
	private String currencyCode;
	
	/** The part no. */
	private String partNo;
	
	/** The part name. */
	private String partName;
	
	/** The part price. */
	private String partPrice;
}
