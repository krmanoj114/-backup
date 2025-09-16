package com.tpex.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import com.tpex.exception.model.ErrorInfo;

/**
 * The Class GlobalExceptionHandler.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handle multipart exception.
	 *
	 * @param request the request
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(MultipartException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<ErrorInfo> handleMultipartException(HttpServletRequest request, Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(),
				HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle method not supported.
	 *
	 * @param request the request
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ResponseBody
	public ResponseEntity<ErrorInfo> handleMethodNotSupported(HttpServletRequest request, Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(),
				"HTTP request method not supported for this operation." + ex.getMessage(),
				HttpStatus.METHOD_NOT_ALLOWED.value());
		return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * Handle IO exception.
	 *
	 * @param request the request
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<ErrorInfo> handleIOException(HttpServletRequest request, Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle general error.
	 *
	 * @param request the request
	 * @param response the response
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> handleGeneralError(HttpServletRequest request, HttpServletResponse response,
			Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle general errors.
	 *
	 * @param request the request
	 * @param response the response
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(MyResourceNotFoundException.class)
	public ResponseEntity<ErrorInfo> handleGeneralErrors(HttpServletRequest request, HttpServletResponse response,
			MyResourceNotFoundException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle general error.
	 *
	 * @param request the request
	 * @param response the response
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(InvalidInputParametersException.class)
	public ResponseEntity<ErrorInfo> handleGeneralError(HttpServletRequest request, HttpServletResponse response,
			InvalidInputParametersException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(),
				HttpStatus.BAD_REQUEST.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle file exceptions.
	 *
	 * @param request the request
	 * @param response the response
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(InvalidFileException.class)
	public ResponseEntity<ErrorInfo> handleFileExceptions(HttpServletRequest request, HttpServletResponse response,
			InvalidFileException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(), 
				HttpStatus.BAD_REQUEST.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle validation exceptions.
	 *
	 * @param request the request
	 * @param ex the ex
	 * @return the response entity
	 */
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorInfo> handleValidationExceptions(HttpServletRequest request, MethodArgumentNotValidException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
