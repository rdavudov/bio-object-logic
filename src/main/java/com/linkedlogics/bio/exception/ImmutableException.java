package com.linkedlogics.bio.exception;

public class ImmutableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ImmutableException() {
		super("bio object is immutable") ; 
	}
}
