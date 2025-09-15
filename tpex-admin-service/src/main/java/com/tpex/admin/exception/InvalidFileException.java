package com.tpex.admin.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Data
public class InvalidFileException extends RuntimeException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The message. */
	private final String message;
	
	/** The error message params. */
	private final transient Map<String, Object> errorMessageParams;

	/**
	 * Instantiates a new invalid file exception.
	 */
	public InvalidFileException() {
		super();
		this.message = "";
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new invalid file exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public InvalidFileException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new invalid file exception.
     *
     * @param message the message
     */
    public InvalidFileException(final String message) {
        super(message);
        this.message = message;
		this.errorMessageParams = null;
    }

    /**
     * Instantiates a new invalid file exception.
     *
     * @param cause the cause
     */
    public InvalidFileException(final Throwable cause) {
    	super(cause);
    	this.message = "";
		this.errorMessageParams = null;
    }
    
    /**
     * Instantiates a new invalid file exception.
     *
     * @param message the message
     * @param errorMessageParams the error message params
     */
    public InvalidFileException(String message, Map<String, Object> errorMessageParams) {
    	super();
        this.message = message;
        this.errorMessageParams = errorMessageParams;
    }


}
