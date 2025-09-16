package com.tpex.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

/**
 * The Class MyResourceNotFoundException.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data
public final class BatchWriteFailedException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The message. */
	private final String message;
	
	/** The error message params. */
	private final transient Map<String, Object> errorMessageParams;

	/**
	 * Instantiates a new my resource not found exception.
	 */
	public BatchWriteFailedException() {
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
    public BatchWriteFailedException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new my resource not found exception.
     *
     * @param message the message
     */
    public BatchWriteFailedException(final String message) {
        super(message);
        this.message = message;
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new my resource not found exception.
     *
     * @param cause the cause
     */
    public BatchWriteFailedException(final Throwable cause) {
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
    public BatchWriteFailedException(String message, Map<String, Object> errorMessageParams) {
    	super();
        this.message = message;
        this.errorMessageParams = errorMessageParams;
    }
}