package com.linkedlogics.bio.exception;

public class ParserException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ParserException(String message) {
		super(message) ; 
	}
	
	public ParserException(Throwable exception) {
		super(exception) ; 
	}
	
	public ParserException(String message, Throwable exception) {
		super(message, exception) ; 
	}
}
