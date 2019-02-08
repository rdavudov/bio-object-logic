package com.linkedlogics.bio.exception;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ValidationException(String message) {
		super(message) ; 
	}
	
	public ValidationException(Throwable exception) {
		super(exception) ; 
	}
	
	public ValidationException(String message, Throwable exception) {
		super(message, exception) ; 
	}
}
