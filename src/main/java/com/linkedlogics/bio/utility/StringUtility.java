package com.linkedlogics.bio.utility;

public class StringUtility {
	
	public static boolean equal(Object left, Object right) {
		if (left == null || right == null) {
			return false ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return false ;
		}
		String leftStr = left.toString() ;
		String rightStr = right.toString() ;
		
		if (leftStr.length() != rightStr.length()) {
			return false ;
		}
		
		return leftStr.equals(rightStr) ;
	}
	
	public static boolean notEqual(Object left, Object right) {
		if (left == null || right == null) {
			return false ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return false ;
		}
		String leftStr = left.toString() ;
		String rightStr = right.toString() ;
		
		if (leftStr.length() != rightStr.length()) {
			return true ;
		}
		
		return !leftStr.equals(rightStr) ;
	}
	
	public static Object plus(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		String leftStr = left.toString() ;
		String rightStr = right.toString() ;
		
		return leftStr + rightStr ;
	}
	
	public static Object multiply(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		String leftStr = left.toString() ;
		
		if (!(right instanceof Number)) {
			right = NumberUtility.convertAndCheck(right.toString()) ;
		}
		
		StringBuilder result = new StringBuilder() ;
		
		for (int i = 0; i < ((Number) right).intValue(); i++) {
			result.append(leftStr) ;
		}
		
		return result.toString() ;
	}
}
