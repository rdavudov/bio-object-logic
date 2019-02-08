package com.linkedlogics.bio;

import java.util.HashMap;

/**
 * Initializer is used to keep initialization properties provided at the beginning and initialize a new instance each time initialize() is called
 * @author rajab
 *
 * @param <T>
 */
public abstract class BioInitializer<T> {
	protected HashMap<String, Object> properties ;
	
	public BioInitializer() {
		
	}
	
	public BioInitializer(HashMap<String, Object> properties) {
		this.properties = properties ;
	}
	
	/**
	 * Creates a new instance
	 * @return
	 */
	public abstract T initialize() ;
}
