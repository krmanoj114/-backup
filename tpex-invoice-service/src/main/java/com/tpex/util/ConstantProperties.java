package com.tpex.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@PropertySource("classpath:constants.properties")
@ConfigurationProperties
@Data
public class ConstantProperties {

	@Value("${invoice.save.error.code}")
	private String invoiceSaveErrorCode;
	
	@Value("${invoice.save.error.code.message}")
	private String invoiceSaveErrorMessage;
	
	@Value("${invoice.haisen.no.limit.error.message}")
	private String errorHaisenNoLimitReached;
	
	@Value("${invoice.haisen.no.configuration.missing}")
	private String errorHaisenNoConfigurationMissing;
	
	@Value("${invoice.PartPrice.update.success.message}")
	private String partPriceUpdateSuccessMessage;

	@Value("${invoice.update.PartPrice.error.code.message}")
	private String partPriceUpdateErrorMessage;

	@Value("${invoice.update.PartPrice.nochanges.code.message}")
	private String partPriceNoChangesFoundErrorMessage;

	@Value("${invoice.update.PartPrice.name.invalid}")
	private String partPriceNameValidationError;
	
	@Value("${invoice.update.PartPrice.price.invalid}")
	private String partPriceZeroValidation;
	
	@Value("${invoice.update.PartPrice.usage.invalid}")
	private String partusageZeroValidation;
	
	@Value("${invoice.update.PartPrice.uploadfile.date.error.message}")
	private String partPriceUploadEffMonthError;
	
	@Value("${invoice.update.PartPrice.usage.missmatch.message}")
	private String partPriceUsageMissMatch;
	
	@Value("${invoice.update.PartPrice.name.missmatch.message}")
	private String partPriceNameMissMatch;
	
	@Value("${invoice.update.PartPrice.name.do.you.want.to.save.message}")
	private String partPriceMissMatchDoYouWantToSave;
	
}
