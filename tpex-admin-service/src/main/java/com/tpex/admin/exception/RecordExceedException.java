package com.tpex.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecordExceedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private final String message;

	public RecordExceedException(final String message) {
		super(message);
		this.message = message;
	}
}
