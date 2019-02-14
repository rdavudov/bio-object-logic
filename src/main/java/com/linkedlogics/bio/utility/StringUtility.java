package com.linkedlogics.bio.utility;

import java.util.List;

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
	
	public static String join(Object[] array) {
		return join(array, ",") ;
	}
	
	public static String join(Object[] array, String sep) {
		if (array == null || array.length == 0) {
			return null ;
		}
		
		StringBuilder s = new StringBuilder() ;
		for (int i = 0; i < array.length; i++) {
			s.append(sep).append(array[i].toString()) ;
		}
		
		return s.substring(1) ;
	}
	
	public static String join(List<Object> list) {
		return join(list, ",") ;
	}
	
	public static String join(List<Object> list, String sep) {
		if (list == null || list.size() == 0) {
			return null ;
		}
		
		StringBuilder s = new StringBuilder() ;
		for (int i = 0; i < list.size(); i++) {
			s.append(sep).append(list.get(i).toString()) ;
		}
		
		return s.substring(1) ;
	}
}
