package com.tpex.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException.NotFound;

@ControllerAdvice
public class GlobalErrorHandler {
 
    @ResponseBody
    @ExceptionHandler(NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(HttpServletRequest request,
            HttpServletResponse response, Exception ex) {
         
        // do something with request or response   
         
        return ex.getMessage();
    }
     
}
