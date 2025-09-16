package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartPricePopupFinalDetailsDto {
	
	private String partNumber;
	private String partName;
	private String partPrice;
	private Integer partUsage;

}
