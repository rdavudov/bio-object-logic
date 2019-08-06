package com.linkedlogics.bio.utility;

import java.util.regex.Pattern;

import com.linkedlogics.bio.exception.ConversionException;

public class NumberUtility {
	
	public static boolean greater(Object left, Object right) {
		if (left == null || right == null) {
			return false ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return false ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return false ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return false ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() > ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() > ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() > ((Number) right).intValue() ;
			}
		} 
	}
	
	public static boolean greaterOrEqual(Object left, Object right) {
		if (left == null || right == null) {
			return false ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return false ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return false ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return false ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() >= ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() >= ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() >= ((Number) right).intValue() ;
			}
		} 
	}
	
	public static boolean smaller(Object left, Object right) {
		if (left == null || right == null) {
			return false ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return false ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return false ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return false ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() < ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() < ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() < ((Number) right).intValue() ;
			}
		} 
	}
	
	public static boolean smallerOrEqual(Object left, Object right) {
		if (left == null || right == null) {
			return false ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return false ;
		} 
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return false ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return false ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() <= ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() <= ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() <= ((Number) right).intValue() ;
			}
		} 
	}
	
	public static boolean equal(Object left, Object right) {
		if (left == null || right == null) {
			return false ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return false ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return false ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return false ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() == ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() == ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() == ((Number) right).intValue() ;
			}
		} 
	}
	
	public static boolean notEqual(Object left, Object right) {
		if (left == null || right == null) {
			return false ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return false ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return false ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return false ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() != ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() != ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() != ((Number) right).intValue() ;
			}
		} 
	}
	
	public static Object plus(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return null ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return null ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() + ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() + ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() + ((Number) right).intValue() ;
			}
		} 
	}
	
	public static Object minus(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return null ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return null ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() - ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() - ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() - ((Number) right).intValue() ;
			}
		} 
	}
	
	public static Object multiply(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return null ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return null ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() * ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() * ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() * ((Number) right).intValue() ;
			}
		} 
	}
	
	public static Object divide(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return null ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return null ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() / ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() / ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() / ((Number) right).intValue() ;
			}
		} 
	}
	
	public static Object module(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return null ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return null ;
			}
		}
		
		if (left instanceof Double || left instanceof Float || right instanceof Double || right instanceof Float) {
			return ((Number) left).doubleValue() % ((Number) right).doubleValue() ;
		} else {
			if (left instanceof Long || right instanceof Long) {
				return ((Number) left).longValue() % ((Number) right).longValue() ;
			} else {
				return ((Number) left).intValue() % ((Number) right).intValue() ;
			}
		} 
	}
	
	public static Object and(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return null ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return null ;
			}
		}
		
		if (left instanceof Long || right instanceof Long) {
			return ((Number) left).longValue() & ((Number) right).longValue() ;
		} else {
			return ((Number) left).intValue() & ((Number) right).intValue() ;
		}
	}
	
	public static Object or(Object left, Object right) {
		if (left == null || right == null) {
			return null ;
		}
		if (left.getClass().isArray() || right.getClass().isArray()) {
			return null ;
		}
		if (!(left instanceof Number)) {
			left = convert(left) ;
			if (left == null) {
				return null ;
			}
		}
		if (!(right instanceof Number)) {
			right = convert(right) ;
			if (right == null) {
				return null ;
			}
		}
		
		if (left instanceof Long || right instanceof Long) {
			return ((Number) left).longValue() | ((Number) right).longValue() ;
		} else {
			return ((Number) left).intValue() | ((Number) right).intValue() ;
		}
	}
	
	private static Pattern intPattern = Pattern.compile("[+-]?\\d+") ;
	private static Pattern doublePattern = Pattern.compile("[+-]?\\d+(\\.\\d+)?") ;
	
	public static Number convertAndCheck(Object value) {
		Number number = convert(value) ;
		if (number == null) {
			throw new ConversionException(value.toString() + " can't be converted to number") ;
		}
		return number ;
	}
	
	public static Number convert(Object value) {
		String strValue = value.toString() ;
		
		if (intPattern.matcher(strValue).matches()) {
			long matched = Long.parseLong(strValue) ;
			if (matched >= Integer.MIN_VALUE && matched <= Integer.MAX_VALUE) {
				return (int) matched ;
			}
			return matched ;
		} else if (doublePattern.matcher(strValue).matches()) {
			return Double.parseDouble(strValue);
		}
		
		return null ;
	}
}
