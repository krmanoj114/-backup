package com.tpex.daily.exception;

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

import com.tpex.daily.exception.model.ErrorInfo;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static final String EXCEPTION = "Exception: ";

	@ExceptionHandler(MultipartException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<ErrorInfo> handleMultipartException(HttpServletRequest request, Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), EXCEPTION + ex.getMessage(),
				HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ResponseBody
	public ResponseEntity<ErrorInfo> handleMethodNotSupported(HttpServletRequest request, Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(),
				"HTTP request method not supported for this operation." + ex.getMessage(),
				HttpStatus.METHOD_NOT_ALLOWED.value());
		return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<ErrorInfo> handleIOException(HttpServletRequest request, Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), EXCEPTION + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> handleGeneralError(HttpServletRequest request, HttpServletResponse response,
			Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), EXCEPTION + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * @author Mohd.Javed
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */

	@ExceptionHandler(InvalidInputParametersException.class)
	public ResponseEntity<ErrorInfo> handleGeneralError(HttpServletRequest request, HttpServletResponse response,
			InvalidInputParametersException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(),ex.getMessage(),
				HttpStatus.BAD_REQUEST.value(), ex.getErrorMessageParams(),ex.getErrorMessageParamsArray());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidFileException.class)
	public ResponseEntity<ErrorInfo> handleFileExceptions(HttpServletRequest request, HttpServletResponse response,
			InvalidFileException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(), HttpStatus.BAD_REQUEST.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorInfo> handleValidationExceptions(HttpServletRequest request, MethodArgumentNotValidException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * @author Anurag Tiwari
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorInfo> handleDataNotFoundErrors(HttpServletRequest request, HttpServletResponse response,
			ResourceNotFoundException ex) {				
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), "" + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}
	
	
	@ExceptionHandler(ItemNotFoundException.class)
	public ResponseEntity<ErrorInfo> handleItemNotFoundErrors(HttpServletRequest request, HttpServletResponse response,
			ItemNotFoundException ex) {				
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), "" + ex.getMessage(),
				HttpStatus.OK.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}
	
}
