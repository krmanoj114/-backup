package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRecalculatePart {
	
	private String partNo;
	private String boxSize;
	private String invPartNetWeight; 
	private String revPartNetWeight; 
	private String invBoxNetWeight; 
	private String revBoxNetWeight;

}
