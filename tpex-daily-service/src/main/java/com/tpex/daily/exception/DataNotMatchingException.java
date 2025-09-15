package com.tpex.daily.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DataNotMatchingException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	 public DataNotMatchingException(final String message, final Throwable cause) {
	        super(message, cause);
	    }

	    public DataNotMatchingException(final String message) {
	        super(message);
	    }

	    public DataNotMatchingException(final Throwable cause) {
	        super(cause);
	    }

}
