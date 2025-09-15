package com.tpex.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Data
public class InvalidInputParametersException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	private String message;
	private Map<String, Object> errorMessageParams;
	private Map<String, Object[]> errorMessageParamsArray;
	
	public InvalidInputParametersException() {
        super();
    }

    public InvalidInputParametersException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public InvalidInputParametersException(final String message) {
        super(message);
        this.message = message;
    }

    public InvalidInputParametersException(final Throwable cause) {
        super(cause);
    }

    public InvalidInputParametersException(String message, Map<String, Object> errorMessageParams) {
    	super();
        this.message = message;
        this.errorMessageParams = errorMessageParams;
    }
    
    public InvalidInputParametersException(Map<String, Object[]> errorMessageParamsArray,String message) {
    	super();
        this.message = message;
        this.errorMessageParamsArray = errorMessageParamsArray;
    }
}
