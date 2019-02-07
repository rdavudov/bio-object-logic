package com.linkedlogics.bio;

import com.linkedlogics.bio.expression.parser.BioExpressionParser;

public interface BioExpression {
	public Object getValue(BioObject... params) ;
	
	default boolean getBooleanValue(BioObject... params) {
		try {
			Object value = getValue(params) ;
			if (value == null) {
				return false ;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ;
			} else {
				return Boolean.parseBoolean(value.toString()) ;
			}
		} catch (Exception e) {
			return false ;
		}
	}
	
	public static BioExpression parse(String expr) {
		// TODO: also check from chache first
		return new BioExpressionParser(expr).parse() ;
	}
	
	public static BioExpression parse2(String expr) {
		StringBuilder unformatted = new StringBuilder();
		unformatted.append("\"") ;
		unformatted.append(expr.replace("${", "\" + ").replace("}$", " + \"")) ;
		unformatted.append("\"") ;
		return new BioExpressionParser(unformatted.toString()).parse() ;
	}
}
