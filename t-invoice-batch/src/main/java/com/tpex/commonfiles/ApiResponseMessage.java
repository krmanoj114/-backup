package com.tpex.commonfiles;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.tpex.util.Util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data

/**
 * Instantiates a new api response message.
 */
@NoArgsConstructor
public class ApiResponseMessage {
	
	/** The status code. */
	private Integer statusCode;
    
    /** The status message. */
    private String statusMessage;
    
    /** The error message params. */
    private Map<String, Object> errorMessageParams;
    
    /** The error message params array. */
    private Map<String, Object[]> errorMessageParamsArray;


    /**
     * Instantiates a new api response message.
     *
     * @param status the status
     * @param message the message
     */
    public ApiResponseMessage(HttpStatus status, String message) {
    	this.statusCode = status.value();
        this.statusMessage = message;
    }

	/**
	 * Instantiates a new api response message.
	 *
	 * @param statusCode the status code
	 * @param message the message
	 */
	public ApiResponseMessage(String statusCode, String message) {
		this.statusCode = Util.nullCheck(message) ? Integer.parseInt(statusCode):0;
        this.statusMessage = message;
	}
	
	/**
	 * Instantiates a new api response message.
	 *
	 * @param status the status
	 * @param message the message
	 * @param errorMessageParams the error message params
	 */
	public ApiResponseMessage(HttpStatus status, String message, Map<String, Object> errorMessageParams) {
    	this.statusCode = status.value();
        this.statusMessage = message;
        this.errorMessageParams = errorMessageParams;
    }

	/**
	 * Instantiates a new api response message.
	 *
	 * @param statusCode the status code
	 * @param message the message
	 * @param errorMessageParams the error message params
	 */
	public ApiResponseMessage(String statusCode, String message, Map<String, Object> errorMessageParams) {
		this.statusCode = Util.nullCheck(message) ? Integer.parseInt(statusCode):0;
        this.statusMessage = message;
        this.errorMessageParams = errorMessageParams;
	}
	
	/**
	 * Instantiates a new api response message.
	 *
	 * @param status the status
	 * @param errorMessageParamsArray the error message params array
	 * @param message the message
	 */
	public ApiResponseMessage(HttpStatus status, Map<String, Object[]> errorMessageParamsArray,String message) {
    	this.statusCode = status.value();
        this.statusMessage = message;
        this.errorMessageParamsArray = errorMessageParamsArray;
    }
	
}
