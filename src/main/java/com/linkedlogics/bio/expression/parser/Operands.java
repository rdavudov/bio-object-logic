package com.linkedlogics.bio.expression.parser;

/**
 * Operands used in bio expressions
 * @author rajab
 *
 */
public interface Operands {

	int EOF = -1;					// End of epxression
	int NUMBER = -2;				// Number 
	int IDENT = -3;					// Identifier
	int STRING = -4;				// String
	int LEFT = -5;					// (
	int RIGHT = -6;					// )
	int MINUS = -7;					// -
	int AND = -8;					// and
	int OR = -9;					// or	
	int DOT = -10;					// .
	int NOT = -11;					// !
	int EQUAL = -12;				// =
	int NOT_EQUAL = -13;			// !=
	int GREATER = -14 ; 			// >
	int GREATER_EQUAL = -15 ; 		// >=
	int SMALLER = -16 ;				// <
	int SMALLER_EQUAL = -17 ; 		// <=
	int EXISTS = -24 ;				// ?
	int COMMA = -25 ;				// ,
	int LEFT_SQUARE = -26 ; 		// [
	int RIGHT_SQUARE = -28 ; 		// ]
	int PLUS = -29 ;				// +	
	int MULTIPLY = -30 ;			// *
	int DIVIDE = -31 ;				// /
	int MODULE = -32 ;				// %
	int LEFT_CURLY = -33 ; 			// {
	int RIGHT_CURLY = -34 ; 		// }
	
}
