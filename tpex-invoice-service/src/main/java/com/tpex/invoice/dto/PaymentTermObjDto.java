package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class PaymentTermObjDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ptCd;
	private String ptDesc;
	
}
