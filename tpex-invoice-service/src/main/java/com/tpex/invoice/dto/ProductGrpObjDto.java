package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductGrpObjDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipgProdGrpCd;
	private String ipgProdGrpDesc;
}
