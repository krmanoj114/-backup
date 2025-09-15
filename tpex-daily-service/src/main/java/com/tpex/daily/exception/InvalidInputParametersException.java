package com.tpex.daily.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Data
public class InvalidInputParametersException extends RuntimeException{
	
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
    public InvalidInputParametersException(final String message) {
        super(message);
        this.message = message;
		this.errorMessageParams = null;
		this.errorMessageParamsArray = null;
    }


}
