package com.tpex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_MODIFIED)
public class DataNotModifiedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 public DataNotModifiedException(final String message, final Throwable cause) {
	        super(message, cause);
	    }

	    public DataNotModifiedException(final String message) {
	        super(message);
	    }

	    public DataNotModifiedException(final Throwable cause) {
	        super(cause);
	    }
}
