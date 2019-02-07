package com.linkedlogics.bio.expression;

import com.linkedlogics.bio.BioObject;

public class Constant extends Expression {
	private Object value ;
	
	public Constant(Object value) {
		this.value = value ;
	}

	@Override
	public Object getValue(Object source, BioObject... params) {
		return value ;
	}
}
