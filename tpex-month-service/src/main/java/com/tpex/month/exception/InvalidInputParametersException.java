package com.tpex.month.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = false)
@Data
public class InvalidInputParametersException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	private final String message;
	private final transient Map<String, Object> errorMessageParams;
	private final transient Map<String, Object[]> errorMessageParamsArray;
	
	public InvalidInputParametersException() {
		super();
		this.message = "";
		this.errorMessageParams = null;
		this.errorMessageParamsArray = null;
    }

    public InvalidInputParametersException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
		this.errorMessageParams = null;
		this.errorMessageParamsArray = null;
    }

    public InvalidInputParametersException(final String message) {
        super(message);
        this.message = message;
		this.errorMessageParams = null;
		this.errorMessageParamsArray = null;
    }

    public InvalidInputParametersException(final Throwable cause) {
    	super(cause);
    	this.message = "";
		this.errorMessageParams = null;
		this.errorMessageParamsArray = null;
    }

    public InvalidInputParametersException(String message, Map<String, Object> errorMessageParams) {
    	super();
        this.message = message;
        this.errorMessageParams = errorMessageParams;
		this.errorMessageParamsArray = null;
    }
    
    public InvalidInputParametersException(Map<String, Object[]> errorMessageParamsArray,String message) {
    	super();
        this.message = message;
		this.errorMessageParams = null;
        this.errorMessageParamsArray = errorMessageParamsArray;
    }
}
