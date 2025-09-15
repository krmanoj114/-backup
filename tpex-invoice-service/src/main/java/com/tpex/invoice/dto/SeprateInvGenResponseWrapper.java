package com.tpex.invoice.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeprateInvGenResponseWrapper implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<SeprateInvGenResponseDto> sepInvGenRespDto;
	private List<SeperateInvoiceDto> sepInvDto;
}
