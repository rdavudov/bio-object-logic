package com.linkedlogics.bio;

import java.util.HashMap;

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
		if (expressionCache.containsKey(expr)) {
			return expressionCache.get(expr) ;
		} else {
			BioExpression e = new BioExpressionParser(expr).parse() ;
			expressionCache.put(expr, e) ;
			return e ;
		}
	}
	
	public static BioExpression parseFormatted(String expr) {
		StringBuilder unformatted = new StringBuilder();
		unformatted.append("\"") ;
		unformatted.append(expr.replace("${", "\" + ").replace("}$", " + \"")) ;
		unformatted.append("\"") ;
		return parse(unformatted.toString()) ;
	}
	
	public static HashMap<String, BioExpression> expressionCache = new HashMap<String, BioExpression>() ; 
}
