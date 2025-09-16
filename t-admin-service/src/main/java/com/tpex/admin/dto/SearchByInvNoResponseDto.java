package com.tpex.admin.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchByInvNoResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String invDate;	
	private String etdDate;
	private String etaDate;
	private String invType;
	private String scInv;
	
	private String scAuth;
	private List<String> scAuthorize;
	
	private String oceanVessel;
	private String feederVessel;
	private String oceanVoyage;
	private String feederVoyage;
	
	private List<ProductGrpObjDto> productGrpObj;
	private String productGrp;
	
	private String portOfLoading;
	private String portOfDischarge;
	
	private String freight;
	private String insurance;
	
	private String currencyCode;
	
	private List<PaymentTermObjDto> paymentTermObj;
	private String paymentTerm;
	
	private List<CarFamilyDto> carFamilyDto;
}
