package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceReCalculationResponseDTO {

	private String flag;
	private String cfCode;
	private String cfSeries;
	private String partNo;
	private String partName;
	private String lot;
	private String pakageMonth;
	private String invAico;
	private String ixosAico;
	private String incOriginCriteria;
	private String ixosOriginCriteria;
	private String invHSCode;
	private String ixosHSCode;

	//Tpex-1414


	private String invPartPrice; 
	private String priceMaster;

	//Tpex-1415

	private String partNameInPriceMaster; 
	private String partNameInPartMaster;

	//Tpex-1416

	private String boxSize; 
	private String invPartNetWeight; 
	private String revPartNetWeight; 
	private String invBoxtNetWeight; 
	private String revBoxtNetWeight;


}
