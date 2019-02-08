package com.linkedlogics.bio;

import com.linkedlogics.bio.exception.ValidationException;

/**
 * BioFunction is for usage in bio expressions just implement it and thats it
 * @author rajab
 *
 */
public interface BioFunction {
	/**
	 * Returns function result
	 * @param value is given from left size of function, may be NULL
	 * @param parameters are given from parentheses
	 * @return
	 */
	public Object getValue(Object value, Object... parameters) ;
	
	/**
	 * Function is validated prior being executed
	 * @param value
	 * @param parameters
	 * @return
	 * @throws ValidationException
	 */
	default boolean validate(Object value, Object... parameters) throws ValidationException {
		return true ;
	}
}
