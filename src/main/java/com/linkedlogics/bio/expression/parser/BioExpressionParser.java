package com.linkedlogics.bio.expression.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.exception.ExpressionException;
import com.linkedlogics.bio.expression.Arithmetic;
import com.linkedlogics.bio.expression.Comparison;
import com.linkedlogics.bio.expression.Constant;
import com.linkedlogics.bio.expression.Dynamic;
import com.linkedlogics.bio.expression.Expression;
import com.linkedlogics.bio.expression.Function;
import com.linkedlogics.bio.expression.Index;
import com.linkedlogics.bio.utility.NumberUtility;

public class BioExpressionParser implements Operands {
	private Scanner lex;
	private String text ;
	private int tok;

	public BioExpressionParser(String s) {
		lex = new Scanner(s);
		text = s ;
	}

	public BioExpression parse() throws ExpressionException {
		tok = lex.nextToken();
		Expression e = expr();
		match(EOF);
		e.setText(text);
		return e ;
	}
	
	private Expression expr() throws ExpressionException {
		boolean negateFirst = false;
		boolean existsFirst = false ;
		if (tok == NOT) {
			negateFirst = true;
			match(NOT);
		} else if (tok == EXISTS) {
			existsFirst = true;
			match(EXISTS);
		}
		Expression e = term();
		if (negateFirst) {
			e.setNegative();
		} else if (existsFirst) {
			e.setExists() ;
		}
		int t = tok;
		while (t == AND || t == OR) {
			match(t);
			boolean negateSecond = false;
			boolean existsSecond = false ;
			if (tok == NOT) {
				negateSecond = true;
				match(NOT);
			} else if (tok == EXISTS) {
				existsSecond = true;
				match(EXISTS);
			}
			Expression e2 = term();
			if (negateSecond) {
				e2.setNegative();
			} else if (existsSecond) {
				e2.setExists() ;
			}
			e = new Comparison(e, t, e2);
			t = tok;
		}
		return e;
	}
	
	private Expression term() throws ExpressionException {
		boolean negateFirst = false;
		boolean existsFirst = false ;
		if (tok == NOT) {
			negateFirst = true;
			match(NOT);
		} else if (tok == EXISTS) {
			existsFirst = true;
			match(EXISTS);
		}
		Expression e = low();
		if (negateFirst) {
			e.setNegative();
		} else if (existsFirst) {
			e.setExists() ;
		}
		int t = tok;
		while (t == EQUAL || t == NOT_EQUAL || t == GREATER || t == GREATER_EQUAL || t == SMALLER || t == SMALLER_EQUAL) {
			match(t);
			boolean negateSecond = false;
			boolean existsSecond = false ;
			if (tok == NOT) {
				negateSecond = true;
				match(NOT);
			} else if (tok == EXISTS) {
				existsSecond = true;
				match(EXISTS);
			}
			Expression e2 = low();
			if (negateSecond) {
				e2.setNegative();
			} else if (existsSecond) {
				e2.setExists() ;
			}
			if (tok == EQUAL || tok == NOT_EQUAL || tok == GREATER || tok == GREATER_EQUAL || tok == SMALLER || tok == SMALLER_EQUAL) {
				int t2 = tok ;
				match(t2) ;
				Expression e3 = low();
				if (negateSecond) {
					e3.setNegative();
				} else if (existsSecond) {
					e3.setExists() ;
				}
				e = new Comparison(e, t, e2, t2, e3);
				t = tok ;
			} else {
				e = new Comparison(e, t, e2);
				t = tok;
			}
		}
		return e;
	}
	
	private Expression low() throws ExpressionException {
		Expression e = high();
		int t = tok;
		while (t == PLUS || t == MINUS) {
			match(t);
			Expression e2 = high();
			e = new Arithmetic(e, t, e2);
			t = tok;
		}
		return e;
	}
	
	private Expression high() throws ExpressionException {
		Expression e = factor();
		int t = tok;
		while (t == DIVIDE || t == MULTIPLY || t == MODULE) {
			match(t);
			Expression e2 = factor();
			e = new Arithmetic(e, t, e2);
			t = tok;
		}
		return e;
	}
	
	private Expression factor() throws ExpressionException {
		Expression expr = null ;
		switch (tok) {
		case MINUS:
		case NUMBER:
			expr = number() ;
			break ;
		case STRING:
			expr = string() ;
			break ;
		case IDENT:
			expr = ident() ;
			break ;
		case LEFT:
			match(LEFT);
			expr = expr();
			match(RIGHT);
			break ;
		case LEFT_SQUARE:
			expr = array() ;
			break ;
		}
		
		if (expr != null) {
			if (tok == DOT) {
				match(DOT);
				expr.setNext(factor());
			} else if (tok == LEFT_SQUARE) {
				match(LEFT_SQUARE) ;
				Expression i = expr() ;
				match(RIGHT_SQUARE) ;
				Index index = new Index(i) ;
				expr.setNext(index);
			}
		}
		
		return expr ;
	}
	
	private Constant number() throws ExpressionException {
		boolean isMinus = false ;
		Constant expr = null ;
		if (tok == MINUS) {
			isMinus = true ;
			match(MINUS);
		}
		
		String numberStr = String.valueOf(lex.nval) ;
		match(NUMBER);
		if (tok == DOT) {
			match(DOT) ;
			numberStr = numberStr + "." + lex.nval ;
			match(NUMBER) ;
		}
		expr = new Constant(NumberUtility.multiply(NumberUtility.convert(numberStr), isMinus ? -1 : 1)) ;
		return expr ;
	}
	
	private Constant string() throws ExpressionException {
		Constant expr = new Constant(lex.sval) ;
		match(STRING);
		return expr ;
	}
	
	private Expression ident() throws ExpressionException {
		Expression expr = null ;
		String ident = lex.sval ;
		match(IDENT);
		if (tok == LEFT) {
			match(LEFT);
			if (tok == RIGHT) {
				match(RIGHT) ;
				expr = new Function(ident) ;
			} else {
				List<Expression> parameters = new ArrayList<Expression>() ;
				do {
					Expression parameter = expr() ;
					if (parameter != null) {
						parameters.add(parameter) ;
					}
					if (tok == COMMA) {
						match(COMMA);
					}
				} while (tok != RIGHT && tok != EOF) ;
				expr = new Function(ident, parameters) ;
			}
		} else {
			// since true or false are like idents but actually reserved words we catch them here
			if (ident.equalsIgnoreCase("true") || ident.equalsIgnoreCase("false")) {
				expr = new Constant(Boolean.parseBoolean(ident)) ;
			} else {
				expr = new Dynamic(ident) ;
			}
		}
		
		return expr ;
	}
	
	private Constant array() throws ExpressionException {
		match(LEFT_SQUARE);
		ArrayList<Expression> list = new ArrayList<Expression>() ;
		do {
			list.add(factor()) ;
			if (tok == COMMA) {
				match(COMMA);
			}
		} while (tok != RIGHT_SQUARE && tok != EOF) ;
		match(RIGHT_SQUARE);
		
		Object[] array = new Object[list.size()] ;
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i).getValue(null) ;
		}
		Constant expr = new Constant(array) ;
		return expr ;
	}
	
	void match(int k) throws ExpressionException {
		if (tok == k)
			tok = lex.nextToken();
		else
			throw new ExpressionException(Scanner.tokenString(k) + " expected, " + lex.toString() + " found expr=" + text);
	}
	
	public static void main(String[] args) {
		BioObject person = new BioObject(0, "person") ;
		person.set("name", "John");
//		person.set("is_student", false) ;
		person.set("cars", new String[] {"mercedes", "porche"}) ;
		person.set("brother", new BioObject(0, "person") {{
			set("name", "Jacob");
			set("cars", new String[] {"bmw", "toyota"}) ;
		}}) ;
		
		String expr = "(5 + [1,2,3])[1]" ;
		BioExpression e = BioExpression.parse(expr) ;
		Object o = e.getValue(person) ;
		System.out.println(e);
	}
}
