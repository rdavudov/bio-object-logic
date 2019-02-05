package com.linkedlogics.bio.exception;

public class EncryptionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EncryptionException(String message) {
		super(message) ; 
	}
	
	public EncryptionException(Throwable exception) {
		super(exception) ; 
	}
	
	public EncryptionException(String message, Throwable exception) {
		super(message, exception) ; 
	}
}
