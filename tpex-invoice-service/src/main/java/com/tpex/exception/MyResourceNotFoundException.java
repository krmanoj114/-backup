package com.tpex.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Data
public final class MyResourceNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The message. */
	private final String message;
	
	/** The error message params. */
	private final transient Map<String, Object> errorMessageParams;

	/**
	 * Instantiates a new my resource not found exception.
	 */
	public MyResourceNotFoundException() {
        super();
        this.message = "";
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new my resource not found exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public MyResourceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new my resource not found exception.
     *
     * @param message the message
     */
    public MyResourceNotFoundException(final String message) {
        super(message);
        this.message = message;
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new my resource not found exception.
     *
     * @param cause the cause
     */
    public MyResourceNotFoundException(final Throwable cause) {
    	super(cause);
		this.message = "";
    	this.errorMessageParams = null;
    }

    /**
     * Instantiates a new my resource not found exception.
     *
     * @param message the message
     * @param errorMessageParams the error message params
     */
    public MyResourceNotFoundException(String message, Map<String, Object> errorMessageParams) {
    	super();
        this.message = message;
        this.errorMessageParams = errorMessageParams;
    }
}