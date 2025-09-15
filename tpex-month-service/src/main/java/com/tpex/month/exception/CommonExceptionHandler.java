package com.tpex.month.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import com.tpex.month.model.dto.ErrorInfo;
import com.tpex.month.util.ConstantUtil;

@ControllerAdvice
public class CommonExceptionHandler {

	@ExceptionHandler(MultipartException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorInfo> handleMultipartException(HttpServletRequest request, Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ConstantUtil.ERR_CM_3009,
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> handleGeneralError(HttpServletRequest request, HttpServletResponse response,
			Exception ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(),
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

	@ExceptionHandler(InvalidFileException.class)
	public ResponseEntity<ErrorInfo> handleFileExceptions(HttpServletRequest request, HttpServletResponse response,
			InvalidFileException ex) {
		ErrorInfo error = new ErrorInfo(request.getRequestURI(), ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getErrorMessageParams());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
