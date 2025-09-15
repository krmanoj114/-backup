package com.tpex.daily.commonfiles;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.tpex.daily.util.Util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponseMessage {
	
	private Integer statusCode;
    private String statusMessage;
    private Map<String, Object> errorMessageParams;
    private Map<String, Object[]> errorMessageParamsArray;


    public ApiResponseMessage(HttpStatus status, String message) {
    	this.statusCode = status.value();
        this.statusMessage = message;
    }

	public ApiResponseMessage(String statusCode, String message) {
		this.statusCode = Util.nullCheck(message) ? Integer.parseInt(statusCode):0;
        this.statusMessage = message;
	}
	
	public ApiResponseMessage(HttpStatus status, String message, Map<String, Object> errorMessageParams) {
    	this.statusCode = status.value();
        this.statusMessage = message;
        this.errorMessageParams = errorMessageParams;
    }

	public ApiResponseMessage(String statusCode, String message, Map<String, Object> errorMessageParams) {
		this.statusCode = Util.nullCheck(message) ? Integer.parseInt(statusCode):0;
        this.statusMessage = message;
        this.errorMessageParams = errorMessageParams;
	}
	
	public ApiResponseMessage(HttpStatus status, Map<String, Object[]> errorMessageParamsArray,String message) {
    	this.statusCode = status.value();
        this.statusMessage = message;
        this.errorMessageParamsArray = errorMessageParamsArray;
    }
	
	
}
