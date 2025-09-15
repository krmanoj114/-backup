package com.tpex.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeMasterListDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ColumnsDTO columns;
	private List<Map<String, String>> data = new ArrayList<>();
	private PaymentTerm paymentTerm;


}
