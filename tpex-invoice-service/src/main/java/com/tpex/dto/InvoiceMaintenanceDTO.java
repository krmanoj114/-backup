package com.tpex.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class InvoiceMaintenanceDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceMaintenanceDTO implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private String orderType;
	private String invoiceNumber;

}
