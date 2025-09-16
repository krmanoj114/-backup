package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class LotPartPriceUploadBatchInputDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String currencyCode;
	private String lot;
	private String cfcCode;
	private String impCode;
	private String partNo;
	private String partName;
	private String firstOfPrice;
	private String usage;

}
