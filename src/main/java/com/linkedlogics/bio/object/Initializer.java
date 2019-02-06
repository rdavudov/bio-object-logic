package com.linkedlogics.bio.object;

import java.util.HashMap;

public abstract class Initializer<T> {
	private HashMap<String, Object> properties ;
	
	public Initializer() {
		
	}
	
	public Initializer(HashMap<String, Object> properties) {
		this.properties = properties ;
	}
	
	public abstract T initialize() ;
}
