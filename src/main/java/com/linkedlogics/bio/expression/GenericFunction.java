package com.linkedlogics.bio.expression;

import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.BioObject;

public class GenericFunction implements BioFunction {
	private BioExpression expression ;
	private String[] parameterNames ;
	
	public GenericFunction(String expression, String[] parameterNames) {
		this.expression = BioExpression.parse(expression) ;
		this.parameterNames = parameterNames ;
	}
	
	public String getExpression() {
		return ((Expression) expression).getText() ;
	}
	
	public String[] getParameterNames() {
		return parameterNames;
	}

	@Override
	public Object getValue(Object value, Object... parameters) {
		BioObject input = new BioObject() ; 
		for (int i = 0; i < parameters.length; i++) {
			input.put(parameterNames[i], parameters[i]) ;
		}
		
		return expression.getValue(input);
	}
}
