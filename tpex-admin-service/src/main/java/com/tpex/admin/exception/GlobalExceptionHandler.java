package com.tpex.admin.exception;

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

import com.tpex.admin.exception.model.ErrorInfo;
import com.tpex.admin.util.ConstantUtils;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MultipartException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<ErrorInfo> handleMultipartException(HttpServletRequest request, Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), "" + ConstantUtils.ERR_CM_3009,
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
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
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ConstantUtils.EXCEPTION + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> handleGeneralError(HttpServletRequest request, HttpServletResponse response,
			Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ConstantUtils.EXCEPTION + ex.getMessage(),
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
	@ExceptionHandler(DateFormateException.class)
	public ResponseEntity<ErrorInfo> handleDateException(HttpServletRequest request, HttpServletResponse response,
			DateFormateException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), "" + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle duplicate record
	 * 
	 * @author Mohd.Javed
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(DuplicateRecordException.class)
	public ResponseEntity<ErrorInfo> handleDuplicateRecordException(HttpServletRequest request,
			HttpServletResponse response, DuplicateRecordException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), "" + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle duplicate record
	 * 
	 * @author Mohd.Javed
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(RecordExceedException.class)
	public ResponseEntity<ErrorInfo> handleRecordException(HttpServletRequest request, HttpServletResponse response,
			RecordExceedException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), "" + ex.getMessage(),
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
	@ExceptionHandler(MyResourceNotFoundException.class)
	public ResponseEntity<ErrorInfo> handleGeneralErrors(HttpServletRequest request, HttpServletResponse response,
			MyResourceNotFoundException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), "" + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidInputParametersException.class)
	public ResponseEntity<ErrorInfo> handleGeneralError(HttpServletRequest request, HttpServletResponse response,
			InvalidInputParametersException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(), HttpStatus.BAD_REQUEST.value(),
				ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidFileException.class)
	public ResponseEntity<ErrorInfo> handleFileExceptions(HttpServletRequest request, HttpServletResponse response,
			InvalidFileException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorInfo> handleValidationExceptions(HttpServletRequest request,
			MethodArgumentNotValidException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getAllErrors().get(0).getDefaultMessage(),
				HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
