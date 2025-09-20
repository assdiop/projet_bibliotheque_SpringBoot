package com.sougane.users_microservice.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailAlreadyExistsException extends RuntimeException {
	
	@SuppressWarnings("unused")
	private String message;
	
	public EmailAlreadyExistsException(String message) {
		
		super(message);
	}

}
