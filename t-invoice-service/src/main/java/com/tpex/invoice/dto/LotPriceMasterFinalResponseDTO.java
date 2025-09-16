package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotPriceMasterFinalResponseDTO {
	
	private String effectiveFromMonth;
	private String effectiveToMonth;
	private String lotCode;
	private String lotPrice;
	private String currency;
	private String curreDesc; 

}
