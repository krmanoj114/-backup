package com.tpex.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotPartPriceUploadBatchErrorDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String currencyCode;
	private String lotCode;
	private String cfcCode;
	private String impCode;
	private String partNo;
	private String partName;
	private BigDecimal firstOfPrice;
	private int usage;
	private String errorReason;
	private String warningReason;

}
