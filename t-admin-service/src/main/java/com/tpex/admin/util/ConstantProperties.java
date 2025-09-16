package com.tpex.admin.util;

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
	
	@Value("${invoice.success.message}")
	private String successInvoiceMessage;
	
	@Value("${invoice.success.code}")
	private String successInvoiceCode;
	
	@Value("${invoice.haisen.no.limit.error.message}")
	private String errorHaisenNoLimitReached;
	
	@Value("${invoice.haisen.no.configuration.missing}")
	private String errorHaisenNoConfigurationMissing;
	
}
