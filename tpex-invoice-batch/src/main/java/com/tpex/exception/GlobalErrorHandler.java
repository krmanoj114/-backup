package com.tpex.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException.NotFound;

/**
 * The Class GlobalErrorHandler.
 */
@ControllerAdvice
public class GlobalErrorHandler {
 
    /**
     * Employee not found handler.
     *
     * @param request the request
     * @param response the response
     * @param ex the ex
     * @return the string
     */
    @ResponseBody
    @ExceptionHandler(NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(HttpServletRequest request,
            HttpServletResponse response, Exception ex) {
         
        return ex.getMessage();
    }
     
}
