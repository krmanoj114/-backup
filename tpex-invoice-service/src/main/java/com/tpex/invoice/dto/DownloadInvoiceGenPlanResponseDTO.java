package com.tpex.invoice.dto;

import lombok.Data;

@Data
public class DownloadInvoiceGenPlanResponseDTO {

	private String message;
	private String status;
	private Object blob;
}
