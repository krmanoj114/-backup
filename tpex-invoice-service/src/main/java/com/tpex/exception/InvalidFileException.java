package com.tpex.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Data
public class InvalidFileException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private String message;
	private Map<String, Object> errorMessageParams;

	public InvalidFileException() {
        super();
    }

    public InvalidFileException(final String message, final Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public InvalidFileException(final String message) {
        super(message);
        this.message = message;
    }

    public InvalidFileException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidFileException(String message, Map<String, Object> errorMessageParams) {
    	super();
        this.message = message;
        this.errorMessageParams = errorMessageParams;
    }

}
