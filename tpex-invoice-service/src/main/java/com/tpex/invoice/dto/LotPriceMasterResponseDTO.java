package com.tpex.invoice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotPriceMasterResponseDTO {
	
	private String effectiveFromMonth;
	private String effectiveToMonth;
	private String lotCode;
	private BigDecimal lotPrice;
	private String currency;
	private String curreDesc;
	
}
