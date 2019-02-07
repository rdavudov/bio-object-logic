package com.linkedlogics.bio;

/**
 * BioFunction is for usage in bio expressions just implement it and thats it
 * @author rajab
 *
 */
public interface BioFunction {
	public Object getValue(Object value, Object... parameters) ;
	public String getName() ;
}
