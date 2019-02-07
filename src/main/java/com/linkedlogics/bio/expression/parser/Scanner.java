package com.linkedlogics.bio.expression.parser;

public class Scanner implements Operands {
	String source;
	int prev; 
	int next; 
	int tok; 
	long nval; 
	String sval; 

	public Scanner(String s) {
		source = s;
		prev = 0;
		next = 0;
	}

	void getNumber() {
		while (next < source.length()) {
			char c = source.charAt(next);
			if (Character.isDigit(c))
				next++;
			else
				break;
		}
		tok = NUMBER;
		String s = source.substring(prev, next);
		nval = Long.parseLong(s) ;
	}

	void getIdent() {
		while (next < source.length()) {
			char c = source.charAt(next);
			if (Character.isLetterOrDigit(c) || c == '_') {
				next++;
			} else if (c == '{') {
				while (c != '}') {
					next++;
					c = source.charAt(next);
				}
				next++;
			} else if (c == '[') {
				while (c != ']') {
					next++;
					c = source.charAt(next);
				}
				next++;
			} else {
				break;
			}
		}
		tok = IDENT;
		sval = source.substring(prev, next);
		
		if (sval.equalsIgnoreCase("and")) {
			tok = AND ;
		} else if (sval.equalsIgnoreCase("or")) {
			tok = OR ;
		}
	}
	
	void getString() {
		StringBuilder s = new StringBuilder() ;
		while (next < source.length()) {
			char c = source.charAt(next);
			if (c == '\\' && source.charAt(next+1) == '\"')
				next++;
			if (c != '"')
				s.append(source.charAt(next++)) ;
			else
				break;
		}
		tok = STRING;
		sval = s.toString() ; //source.substring(prev+1, next);
		next++;
	}
	
	void getStringSingleQuote() {
		StringBuilder s = new StringBuilder() ;
		while (next < source.length()) {
			char c = source.charAt(next);
			if (c == '\\' && source.charAt(next+1) == '\'')
				next++;
			if (c != '\'')
				s.append(source.charAt(next++)) ;
			else
				break;
		}
		tok = STRING;
		sval = s.toString() ; //source.substring(prev+1, next);
		next++;
	}
	
	void getEqual() {
		while (next < source.length()) {
			char c = source.charAt(next);
			if (c == '=')
				next++;
			else
				break;
		}
		tok = EQUAL;
		sval = source.substring(prev, next);
	}
	
	void getNotEqual() {
		boolean isNegation = false ;
		while (next < source.length()) {
			char c = source.charAt(next);
			if (c == '=')
				next++;
			else
				isNegation = true ;
			break ;
		}
		if (isNegation) {
			tok = NOT ;
		} else {
			tok = NOT_EQUAL;
		}
		sval = source.substring(prev, next);
	}
	
	void getGreater() {
		boolean isEqual = false ;
		while (next < source.length()) {
			char c = source.charAt(next);
			if (c == '=') {
				isEqual = true ;
				next++;
			}
			
			break ;
		}
		if (isEqual) {
			tok = GREATER_EQUAL;
		} else {
			tok = GREATER;
		}
		sval = source.substring(prev, next);
	}
	
	void getSmaller() {
		boolean isEqual = false ;
		while (next < source.length()) {
			char c = source.charAt(next);
			if (c == '=') {
				isEqual = true ;
				next++;
			}
			
			break ;
		}
		if (isEqual) {
			tok = SMALLER_EQUAL;
		} else {
			tok = SMALLER;
		}
		sval = source.substring(prev, next);
	}

	public int nextToken() {
		nval = 0;
		sval = "";
		char c;
		do {
			if (next >= source.length())
				return (tok = EOF);
			c = source.charAt(next++); // read next char
		} while (Character.isWhitespace(c));
		prev = next - 1;
		tok = c;
		if (Character.isLetter(c))
			getIdent();
		else if (Character.isDigit(c))
			getNumber();
		else if (c == '"')
			getString();
		else if (c == '\'')
			getStringSingleQuote();
		else if (c == '=')
			tok = EQUAL ;
		else if (c == '!')
			getNotEqual();
		else if (c == '.')
			tok = DOT ;
		else if (c == '(')
			tok = LEFT ;
		else if (c == ')')
			tok = RIGHT ;
		else if (c == '-')
			tok = MINUS ;
		else if (c == '+')
			tok = PLUS ;
		else if (c == '*')
			tok = MULTIPLY ;
		else if (c == '/')
			tok = DIVIDE ;
		else if (c == '%')
			tok = MODULE ;
		else if (c == ',')
			tok = COMMA ;
		else if (c == '>')
			getGreater() ;
		else if (c == '<')
			getSmaller() ;
		else if (c == '?')
			tok = EXISTS ;
		else if (c == '[')
			tok = LEFT_SQUARE ;
		else if (c == ']')
			tok = RIGHT_SQUARE ;
		else if (c == '{')
			tok = LEFT_CURLY ;
		else if (c == '}')
			tok = RIGHT_CURLY ;
		return tok;
	}
	
	public void listTokens() {
		while (tok != EOF) {
			nextToken();
		}
	}

	public String toString() {
		String s = tokenString(tok);
		if (tok == NUMBER)
			return s + nval;
		if (tok == IDENT)
			return s + sval;
		if (tok == STRING)
			return s + sval;
		return s + sval;
	}
	
	 public static String numToStr(float n) {
	      String s = ""+n;
	      if (s.endsWith(".0"))  //integers need no decimal point
	         s = s.substring(0, s.length()-2);
	      return s;
	   }
	
	public static String tokenString(int t) {
//	      if (t > 32) return "\'"+(char)t+"\'";
	      switch (t) {
	      case EOF:    return "eof";
	      case NUMBER: return "number ";
	      case IDENT:  return "ident  ";
	      case STRING:  return "string  ";
	      case AND:  return "logical  ";
	      case OR:  return "logical  ";
	      case LEFT:  return "open (  ";
	      case RIGHT:  return "close )  ";
	      case EQUAL:  return "comparison  ";
	      case NOT_EQUAL:  return "comparison  ";
	      case GREATER:  return "comparison  ";
	      case GREATER_EQUAL:  return "comparison  ";
	      case SMALLER:  return "comparison  ";
	      case SMALLER_EQUAL:  return "comparison  ";
	      case NOT:  return "negation  ";
	      case MINUS:  return "minus sign  ";
	      case DOT:  return "function  ";
	      case EXISTS:  return "exists  ";
	      default:     return "???";
	      }
	   }
}
