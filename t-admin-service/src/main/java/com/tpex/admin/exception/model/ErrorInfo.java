package com.tpex.admin.exception.model;

import java.util.Map;

import lombok.Data;
@Data
public class ErrorInfo {
    private final String url;
    private final String exception;
    private Integer statusCode;
    private Map<String, Object> errorMessageParams;
     
    public ErrorInfo(String url, Exception ex) {
        this.url = url;
        this.exception = ex.getLocalizedMessage();
    }

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

	
     
}
