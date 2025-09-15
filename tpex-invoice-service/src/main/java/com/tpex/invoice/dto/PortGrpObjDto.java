package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortGrpObjDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cd;
	private String name;
}
