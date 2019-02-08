package com.linkedlogics.bio;

import java.util.HashMap;

import com.linkedlogics.bio.expression.BioExpressionParser;

/**
 * Bio expression class returns expression value evaluated
 * @author rdavudov
 *
 */
public interface BioExpression {
	/**
	 * Evaluates expression
	 * @param params
	 * @return
	 */
	public Object getValue(BioObject... params) ;
	
	/**
	 * Evaluates expression and parses result to boolean
	 * @param params
	 * @return
	 */
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
	
	/**
	 * Parses expression string
	 * @param expr
	 * @return
	 */
	public static BioExpression parse(String expr) {
		if (expressionCache.containsKey(expr)) {
			return expressionCache.get(expr) ;
		} else {
			BioExpression e = new BioExpressionParser(expr).parse() ;
			expressionCache.put(expr, e) ;
			return e ;
		}
	}
	
	/**
	 * Parses formatted expression string
	 * @param expr
	 * @return
	 */
	public static BioExpression parseFormatted(String expr) {
		StringBuilder unformatted = new StringBuilder();
		unformatted.append("\"") ;
		unformatted.append(expr.replace("${", "\" + ").replace("}$", " + \"")) ;
		unformatted.append("\"") ;
		return parse(unformatted.toString()) ;
	}
	
	/**
	 * Expression cache map in order not to parse same expressions
	 */
	public static HashMap<String, BioExpression> expressionCache = new HashMap<String, BioExpression>() ; 
}
