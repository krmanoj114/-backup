package com.tpex.admin.exception;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Data
public class InvalidRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String message;
	private final transient Map<String, List<String>> errorMessageListParams;

	public InvalidRequestException() {
		super();
		this.message = "";
		this.errorMessageListParams = null;

	}

	public InvalidRequestException(final String message, final Throwable cause) {
		super(message, cause);
		this.message = message;
		this.errorMessageListParams = null;
	}

	public InvalidRequestException(final Throwable cause) {
		super(cause);
		this.message = "";
		this.errorMessageListParams = null;
	}

	public InvalidRequestException(String message, Map<String, List<String>>
	errorMessageListParams) { 
		super();
		this.message = message;
		this.errorMessageListParams = errorMessageListParams; 
	}

}
