package com.tpex.admin.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class InvoiceDetailsResponseWrapper implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   
	private List<InvoiceDetailsResponseDto> invoiceDetailsResponseDto;
	private List<PortGrpObjDto> portGrpObjDto;
}
