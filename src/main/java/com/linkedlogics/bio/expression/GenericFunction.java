package com.linkedlogics.bio.expression;

import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.BioObject;
/**
 * Generic function is used inside xml configuration where you define your function as a bio expression
 * @author rajab
 *
 */
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
		// since our expression function uses parameters we need to populate their values into a bio object instance
		BioObject input = new BioObject() ; 
		for (int i = 0; i < parameters.length && i < parameterNames.length; i++) {
			input.put(parameterNames[i], parameters[i]) ;
		}
		if (value != null) {
			input.put("self", value) ;
		}
		
		return expression.getValue(input);
	}
}
