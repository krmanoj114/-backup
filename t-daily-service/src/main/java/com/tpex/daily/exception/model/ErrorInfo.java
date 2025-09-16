package com.tpex.daily.exception.model;

import java.util.Map;

import lombok.Data;
@Data
public class ErrorInfo {
    private final String url;
    private final String exception;
    private Integer statusCode;
    private Map<String, Object> errorMessageParams;
    private Map<String, Object[]> errorMessageParamsArray;
     
    public ErrorInfo(String requestURI, String stringException,Integer statusCode) {

        this.url = requestURI;
        this.exception = stringException;
        this.statusCode = statusCode;
        this.errorMessageParams = null;
	}
	
	public ErrorInfo(String requestURI, String stringException,Integer statusCode, Map<String, Object> errorMessageParams) {

        this.url = requestURI;
        this.exception = stringException;
        this.statusCode = statusCode;
        this.errorMessageParams = errorMessageParams;
    
	}
	
	public ErrorInfo(String requestURI, String stringException,Integer statusCode, Map<String, Object> errorMessageParams,
			Map<String, Object[]> errorMessageParamsArray) {

        this.url = requestURI;
        this.exception = stringException;
        this.statusCode = statusCode;
        this.errorMessageParams = errorMessageParams;
        this.errorMessageParamsArray = errorMessageParamsArray;
    
	}
     
}
