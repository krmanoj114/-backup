package com.tpex.invoice.dto;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartPricePopupDetailsDto {
	
	
	private String partNumber;
	private String partName;
	private Double partPrice;
	private Double partUsage;
}
