package com.linkedlogics.bio.exception;

public class ExpressionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExpressionException(String message) {
		super(message) ; 
	}
	
	public ExpressionException(Throwable exception) {
		super(exception) ; 
	}
	
	public ExpressionException(String message, Throwable exception) {
		super(message, exception) ; 
	}
}
