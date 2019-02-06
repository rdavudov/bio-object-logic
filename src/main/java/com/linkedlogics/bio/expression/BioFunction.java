package com.linkedlogics.bio.expression;

import com.linkedlogics.bio.BioObject;

/**
 * BioFunction is for usage in bio expressions just implement it and thats it
 * @author rajab
 *
 */
public interface BioFunction {
	default void setParams(BioExpression... input) { }
	public Object getValue(Object value, BioObject... params) ;
	public String getName() ;
}
