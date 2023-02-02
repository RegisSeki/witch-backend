package com.rtseki.witch.backend.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.DatabaseException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;

//@AllArgsConstructor
@ControllerAdvice
public class ApiExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		Problem problem = new Problem();
		List<Problem.Field> fields = new ArrayList<>();
		
		String name = "";
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			if (error instanceof FieldError) {
				FieldError fieldError = (FieldError) error;
				name = fieldError.getField();
			}

			String msg = error.getDefaultMessage();
			
			fields.add(new Problem.Field(name, msg));
		}
		
		problem.setStatus(status.value());
		problem.setDateHour(OffsetDateTime.now()); 
		problem.setTitle("One or more fields are not correct! Fill the fields correctly and try again!");
		problem.setFields(fields);
		
		return new ResponseEntity<>(problem, status);
	}
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handleBusiness(BusinessException ex, WebRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		Problem problem = new Problem();
		problem.setStatus(status.value()); 
		problem.setDateHour(OffsetDateTime.now()); 
		problem.setTitle(ex.getMessage());
		
		return new ResponseEntity<>(problem, status);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		
		Problem problem = new Problem();
		problem.setStatus(status.value()); 
		problem.setDateHour(OffsetDateTime.now()); 
		problem.setTitle(ex.getMessage());

		return new ResponseEntity<>(problem, status);
	}
	
	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<Object> handleDataIntegrity(DatabaseException ex, WebRequest request) {
		HttpStatus status = HttpStatus.FORBIDDEN;
		
		Problem problem = new Problem();
		problem.setStatus(status.value()); 
		problem.setDateHour(OffsetDateTime.now()); 
		problem.setTitle(ex.getMessage());

		return new ResponseEntity<>(problem, status);
	}
}
