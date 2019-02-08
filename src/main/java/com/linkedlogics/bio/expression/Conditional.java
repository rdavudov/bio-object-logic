package com.linkedlogics.bio.expression;

import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;

public class Conditional extends Expression {
	private Object value ;
	private Object elseValue ;
	private BioExpression condition ;
	
	public Conditional(BioExpression condition, Object value) {
		this(condition, value, null) ;
	}
	
	public Conditional(BioExpression condition, Object value, Object elseValue) {
		this.condition = condition ;
		this.value = value ;
		this.elseValue = elseValue ;
	}
	
	@Override
	protected Object getValue(Object source, BioObject... params) {
		if (condition.getBooleanValue(params)) {
			if (value instanceof BioExpression) {
				return ((BioExpression) value).getValue(params) ;
			}
			return value ;
		} else if (elseValue != null) {
			if (elseValue instanceof BioExpression) {
				return ((BioExpression) elseValue).getValue(params) ;
			}
			return elseValue ;
		}
		return null;
	}
}
