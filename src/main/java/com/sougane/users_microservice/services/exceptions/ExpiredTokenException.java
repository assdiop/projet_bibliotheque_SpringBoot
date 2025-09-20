package com.sougane.users_microservice.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ExpiredTokenException extends RuntimeException{
	
	@SuppressWarnings("unused")
	private String message;
	public ExpiredTokenException (String message){
		super(message);
	
	}
	

}
