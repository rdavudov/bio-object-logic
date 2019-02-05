package com.linkedlogics.bio.exception;

public class DictionaryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DictionaryException(String message) {
		super(message) ; 
	}
	
	public DictionaryException(Throwable exception) {
		super(exception) ; 
	}
	
	public DictionaryException(String message, Throwable exception) {
		super(message, exception) ; 
	}
}
