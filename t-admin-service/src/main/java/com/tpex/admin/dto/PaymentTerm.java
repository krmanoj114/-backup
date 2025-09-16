package com.tpex.admin.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class PaymentTerm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Map<String, String>> dayMonth;
}
