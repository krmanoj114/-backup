package com.tpex.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class LotPartPriceUploadBatchDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String partPricericeCFCode;
	private String partPriceDestCode;
	private String partPriceLotCode;
	private String partPriceCurrCode;
	private String partPriceNo;
	private Double partPricePrc;
	private String partPriceUpdateBy;
	private String partPriceUpdateDate;
	private String partPriceName;
	private String partPriceEffFromMonth;
	private String partPriceEffToMonth;
	private Double partPriceUsage;
	private String companyCode;
	
	private String priceCFCode;
	private String priceDestCode;
	private String priceLotCode;
	private String priceCurrCode;
	private Double lotPrice;
	private String updateBy;
	private Date updateDate;
	private String effFromMonth;
	private String effFromToMonth;
	private String lotPriceStatus;

}