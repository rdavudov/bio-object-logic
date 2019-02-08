package com.linkedlogics.bio.expression;

import com.linkedlogics.bio.BioObject;

public class Conditional extends Expression {
	private Expression value ;
	private Expression elseValue ;
	private Expression condition ;
	
	public Conditional(Expression condition, Expression value) {
		this(condition, value, null) ;
	}
	
	public Conditional(Expression condition, Expression value, Expression elseValue) {
		this.condition = condition ;
		this.value = value ;
		this.elseValue = elseValue ;
	}
	
	@Override
	protected Object getValue(Object source, BioObject... params) {
		if (condition.getBooleanValue(params)) {
			return value.getValue(params) ;
		} else if (elseValue != null) {
			return elseValue.getValue(params) ;
		}
		return null;
	}
}
