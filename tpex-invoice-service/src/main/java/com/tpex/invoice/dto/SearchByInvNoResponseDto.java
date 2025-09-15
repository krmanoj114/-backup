package com.tpex.invoice.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchByInvNoResponseDto implements Serializable {

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
	private String shipingMark1;
	private String shipingMark2;
	private String shipingMark3;
	private String shipingMark4;
	private String shipingMark5;
	private String shipingMark6;
	private String shipingMark7;
	private String shipingMark8;

	private String description1;
	private String description2;
	private String description3;
	private String description4;
	private String description5;
	private String description6;
	private String countryOfOriginFlg;

	private List<NotifyCodeDtoObj> notifyrcodeObj1;
	private String notifyCode;
	private String notifyName;
	private String customerCodeName;
	private String cmpCd;
	private String cmpNm;

	private List<CustomerCodeDtoObj> customercodeObj1;
	private List<ConsigneeCompanyNameDto> companyNameObj;
	private String customerCodeAddress;
	private String notifyCodeAddress;

	private String cnsgCodeAddress;
	private String customerCode;

}
