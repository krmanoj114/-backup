package com.tpex.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

/**
 * The Class InvalidInputParametersException.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data
public class InvalidInputParametersException extends RuntimeException{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The message. */
	private final String message;
	
	/** The error message params. */
	private final transient Map<String, Object> errorMessageParams;
	
	/**
	 * Instantiates a new invalid input parameters exception.
	 */
	public InvalidInputParametersException() {
		super();
		this.message = "";
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new invalid input parameters exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public InvalidInputParametersException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new invalid input parameters exception.
     *
     * @param message the message
     */
    public InvalidInputParametersException(final String message) {
        super(message);
        this.message = message;
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new invalid input parameters exception.
     *
     * @param cause the cause
     */
    public InvalidInputParametersException(final Throwable cause) {
    	super(cause);
    	this.message = "";
		this.errorMessageParams = null;
		
    }

    /**
     * Instantiates a new invalid input parameters exception.
     *
     * @param message the message
     * @param errorMessageParams the error message params
     */
    public InvalidInputParametersException(String message, Map<String, Object> errorMessageParams) {
    	super();
        this.message = message;
        this.errorMessageParams = errorMessageParams;
    }
}
