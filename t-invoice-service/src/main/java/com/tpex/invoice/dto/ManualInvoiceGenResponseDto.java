package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualInvoiceGenResponseDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ranbanNumber;
	private String vanPlant;
	private String shippingResult;
	private String type;
	private String invoiceStatus;

}
