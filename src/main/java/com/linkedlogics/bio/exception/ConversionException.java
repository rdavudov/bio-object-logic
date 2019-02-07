package com.linkedlogics.bio.exception;

public class ConversionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConversionException(String message) {
		super(message) ; 
	}
	
	public ConversionException(Throwable exception) {
		super(exception) ; 
	}
	
	public ConversionException(String message, Throwable exception) {
		super(message, exception) ; 
	}
}
