package com.tpex.exception.model;

import java.util.Map;

import lombok.Data;

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data
public class ErrorInfo {
    
    /** The url. */
    private final String url;
    
    /** The exception. */
    private final String exception;
    
    /** The status code. */
    private Integer statusCode;
    
    /** The error message params. */
    private Map<String, Object> errorMessageParams;
     
    /**
     * Instantiates a new error info.
     *
     * @param url the url
     * @param ex the ex
     */
    public ErrorInfo(String url, Exception ex) {
        this.url = url;
        this.exception = ex.getLocalizedMessage();
    }

	/**
	 * Instantiates a new error info.
	 *
	 * @param requestURI the request URI
	 * @param stringException the string exception
	 * @param statusCode the status code
	 */
	public ErrorInfo(String requestURI, String stringException,Integer statusCode) {

        this.url = requestURI;
        this.exception = stringException;
        this.statusCode = statusCode;
        this.errorMessageParams = null;
	}
	
	/**
	 * Instantiates a new error info.
	 *
	 * @param requestURI the request URI
	 * @param stringException the string exception
	 * @param statusCode the status code
	 * @param errorMessageParams the error message params
	 */
	public ErrorInfo(String requestURI, String stringException,Integer statusCode, Map<String, Object> errorMessageParams) {

        this.url = requestURI;
        this.exception = stringException;
        this.statusCode = statusCode;
        this.errorMessageParams = errorMessageParams;
    
	}
     
}
