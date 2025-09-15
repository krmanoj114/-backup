package com.tpex.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DuplicateRecordException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private final String message;

	public DuplicateRecordException(final String message) {
		super(message);
		this.message = message;
	}
}
