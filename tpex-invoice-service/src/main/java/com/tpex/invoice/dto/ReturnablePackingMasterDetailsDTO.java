package com.tpex.invoice.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnablePackingMasterDetailsDTO {
	
	private String packingPlant;
	private String importerCode;
	private String moduleType;
	private String moduleDesciption;
	private String vanningDateFrom;
	private String vanningDateTo;
	private String updBy;
	private String createBy;
	private String createDate;
	private String updDate;

}
