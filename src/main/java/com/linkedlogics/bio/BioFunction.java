package com.linkedlogics.bio;

import com.linkedlogics.bio.exception.ValidationException;

/**
 * BioFunction is for usage in bio expressions just implement it and thats it
 * @author rajab
 *
 */
public interface BioFunction {
	public Object getValue(Object value, Object... parameters) ;
	
	default boolean validate(Object value, Object... parameters) throws ValidationException {
		return true ;
	}
}
