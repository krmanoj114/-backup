package com.tpex.daily.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@ResponseStatus(value = HttpStatus.OK)
@Data
public class ResourceNotFoundException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String message;
	private final transient Map<String, Object> errorMessageParams;

	public ResourceNotFoundException() {
		super();
        this.message = "";
		this.errorMessageParams = null;
    }

    public ResourceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
		this.errorMessageParams = null;
    }

    public ResourceNotFoundException(final String message) {
        super(message);
        this.message = message;
		this.errorMessageParams = null;
    }

    public ResourceNotFoundException(final Throwable cause) {
    	super(cause);
    	this.message = "";
		this.errorMessageParams = null;
    }

    public ResourceNotFoundException(String message, Map<String, Object> errorMessageParams) {
    	super();
        this.message = message;
        this.errorMessageParams = errorMessageParams;
    }

}
