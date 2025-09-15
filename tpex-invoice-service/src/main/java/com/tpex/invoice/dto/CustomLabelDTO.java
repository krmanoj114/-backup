package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor

public class CustomLabelDTO {
	
	private String id;
	
	private String name;
	
	private String barcodeInputRequired;

}
