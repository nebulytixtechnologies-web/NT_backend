package com.neb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(CustomeException.class)
	    public ResponseEntity<ErrorResponse> handleCustomException(CustomeException ex, WebRequest request) {
	        ErrorResponse response = new ErrorResponse(
	                HttpStatus.BAD_REQUEST.value(),
	                HttpStatus.BAD_REQUEST.getReasonPhrase(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	 
	 @ExceptionHandler(RuntimeException.class)
	    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
	        ErrorResponse response = new ErrorResponse(
	                HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
}
