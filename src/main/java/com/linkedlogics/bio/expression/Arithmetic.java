package com.linkedlogics.bio.expression;

import java.lang.reflect.Array;

import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.utility.NumberUtility;
import com.linkedlogics.bio.utility.StringUtility;

/**
 * Arithmetic performs arithmetic operation
 * @author rajab
 *
 */
public class Arithmetic extends Expression implements Operands {
	private Expression leftExpr ;
	private Expression rightExpr ;
	private int operation ;
	
	public Arithmetic(Expression leftExpr, int operation, Expression rightExpr) {
		this.leftExpr = leftExpr ;
		this.operation = operation ;
		this.rightExpr = rightExpr ;
	}

	@Override
	public Object getValue(Object source, BioObject... params) {
		try {
			switch (operation) {
			case PLUS:
				return plus(leftExpr, rightExpr, params) ;
			case MINUS:
				return minus(leftExpr, rightExpr, params) ;
			case MULTIPLY:
				return multiply(leftExpr, rightExpr, params) ;
			case DIVIDE:
				return divide(leftExpr, rightExpr, params) ;
			case MODULE:
				return module(leftExpr, rightExpr, params) ;
			}
		} catch(Throwable e) {
			e.printStackTrace();
			//TODO: log this exception
		}
		
		return null ;
	}
	
	/**
	 * Performs add operation. If one side is an array the other side is added. If one side is string then other side is concatenated
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private Object plus(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return null ;
		}
		
		// bio enums can be used in expressions by their string names like normal strings
		// one side of equation is bio enum, we try to convert other side also to same bio enum
		if (leftValue instanceof BioEnum && rightValue instanceof String) {
			rightValue = BioEnum.getBioEnumByName((String) rightValue, (Class<? extends BioEnum>) leftValue.getClass()) ;
		} else if (rightValue instanceof BioEnum && leftValue instanceof String) {
			leftValue = BioEnum.getBioEnumByName((String) leftValue, (Class<? extends BioEnum>) rightValue.getClass()) ;
		}
		
		 // First lets check whether any of them is an array
		boolean leftIsArray = leftValue instanceof Object[] ;
		boolean rightIsArray = rightValue instanceof Object[] ;
		if (leftIsArray || rightIsArray) {
			if (!leftIsArray && rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(rightValue.getClass().getComponentType(), ((Object[]) rightValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object rightValueObject = ((Object[]) rightValue)[i] ;
					if (leftValue instanceof Number && rightValueObject instanceof Number) {
						result[i] = NumberUtility.plus(leftValue, rightValueObject) ;
					} else {
						result[i] = StringUtility.plus(leftValue != null ? leftValue : "", rightValueObject != null ? rightValueObject : "") ;
					}
				}
				return result ;
			} else if (leftIsArray && !rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(leftValue.getClass().getComponentType(), ((Object[]) leftValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object leftValueObject = ((Object[]) leftValue)[i] ;
					if (rightValue instanceof Number && leftValueObject instanceof Number) {
						result[i] = NumberUtility.plus(leftValueObject, rightValue) ;
					} else {
						result[i] = StringUtility.plus(leftValueObject  != null ? leftValueObject : "", rightValue != null ? rightValue : "") ;
					}
				}
				return result ;
			}
			return null ;
		} else {
			if (leftValue instanceof Number && rightValue instanceof Number) {
				return NumberUtility.plus(leftValue, rightValue) ;
			} else {
				return StringUtility.plus(leftValue != null ? leftValue : "", rightValue != null ? rightValue : "") ;
			}
		}
	}
	
	/**
	 * Performs minux operation. If one side is an array the other side is subtracted.
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private Object minus(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return null ;
		}
		
		// bio enums can be used in expressions by their string names like normal strings
		// one side of equation is bio enum, we try to convert other side also to same bio enum
		if (leftValue instanceof BioEnum && rightValue instanceof String) {
			rightValue = BioEnum.getBioEnumByName((String) rightValue, (Class<? extends BioEnum>) leftValue.getClass()) ;
		} else if (rightValue instanceof BioEnum && leftValue instanceof String) {
			leftValue = BioEnum.getBioEnumByName((String) leftValue, (Class<? extends BioEnum>) rightValue.getClass()) ;
		}
		
		 // First lets check whether any of them is an array
		boolean leftIsArray = leftValue instanceof Object[] ;
		boolean rightIsArray = rightValue instanceof Object[] ;
		if (leftIsArray || rightIsArray) {
			if (!leftIsArray && rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(rightValue.getClass().getComponentType(), ((Object[]) rightValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object rightValueObject = ((Object[]) rightValue)[i] ;
					if (leftValue instanceof Number && rightValueObject instanceof Number) {
						result[i] = NumberUtility.minus(leftValue, rightValueObject) ;
					}
				}
				return result ;
			} else if (leftIsArray && !rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(leftValue.getClass().getComponentType(), ((Object[]) leftValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object leftValueObject = ((Object[]) leftValue)[i] ;
					if (rightValue instanceof Number && leftValueObject instanceof Number) {
						result[i] = NumberUtility.minus(leftValueObject, rightValue) ;
					}
				}
				return result ;
			}
			return null ;
		} else {
			if (leftValue instanceof Number && rightValue instanceof Number) {
				return NumberUtility.minus(leftValue, rightValue) ;
			}
			return null ;
		}
	}
	
	/**
	 * Performs add operation. If one side is an array the other side is multiplied. If one side is string then other side is multiply concatenated
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private Object multiply(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return null ;
		}
		
		// bio enums can be used in expressions by their string names like normal strings
		// one side of equation is bio enum, we try to convert other side also to same bio enum
		if (leftValue instanceof BioEnum && rightValue instanceof String) {
			rightValue = BioEnum.getBioEnumByName((String) rightValue, (Class<? extends BioEnum>) leftValue.getClass()) ;
		} else if (rightValue instanceof BioEnum && leftValue instanceof String) {
			leftValue = BioEnum.getBioEnumByName((String) leftValue, (Class<? extends BioEnum>) rightValue.getClass()) ;
		}
		
		 // First lets check whether any of them is an array
		boolean leftIsArray = leftValue instanceof Object[] ;
		boolean rightIsArray = rightValue instanceof Object[] ;
		if (leftIsArray || rightIsArray) {
			if (!leftIsArray && rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(rightValue.getClass().getComponentType(), ((Object[]) rightValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object rightValueObject = ((Object[]) rightValue)[i] ;
					if (leftValue instanceof Number && rightValueObject instanceof Number) {
						result[i] = NumberUtility.multiply(leftValue, rightValueObject) ;
					} else {
						result[i] = StringUtility.multiply(leftValue != null ? leftValue : "", rightValueObject != null ? rightValueObject : "") ;
					}
				}
				return result ;
			} else if (leftIsArray && !rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(leftValue.getClass().getComponentType(), ((Object[]) leftValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object leftValueObject = ((Object[]) leftValue)[i] ;
					if (rightValue instanceof Number && leftValueObject instanceof Number) {
						result[i] = NumberUtility.multiply(leftValueObject, rightValue) ;
					} else {
						result[i] = StringUtility.multiply(leftValueObject  != null ? leftValueObject : "", rightValue != null ? rightValue : "") ;
					}
				}
				return result ;
			}
			return null ;
		} else {
			if (leftValue instanceof Number && rightValue instanceof Number) {
				return NumberUtility.multiply(leftValue, rightValue) ;
			} else {
				return StringUtility.multiply(leftValue != null ? leftValue : "", rightValue != null ? rightValue : "") ;
			}
		}
	}
	
	/**
	 * Performs minux operation. If one side is an array the other side is divided.
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private Object divide(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return null ;
		}
		
		// bio enums can be used in expressions by their string names like normal strings
		// one side of equation is bio enum, we try to convert other side also to same bio enum
		if (leftValue instanceof BioEnum && rightValue instanceof String) {
			rightValue = BioEnum.getBioEnumByName((String) rightValue, (Class<? extends BioEnum>) leftValue.getClass()) ;
		} else if (rightValue instanceof BioEnum && leftValue instanceof String) {
			leftValue = BioEnum.getBioEnumByName((String) leftValue, (Class<? extends BioEnum>) rightValue.getClass()) ;
		}
		
		 // First lets check whether any of them is an array
		boolean leftIsArray = leftValue instanceof Object[] ;
		boolean rightIsArray = rightValue instanceof Object[] ;
		if (leftIsArray || rightIsArray) {
			if (!leftIsArray && rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(rightValue.getClass().getComponentType(), ((Object[]) rightValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object rightValueObject = ((Object[]) rightValue)[i] ;
					if (leftValue instanceof Number && rightValueObject instanceof Number) {
						result[i] = NumberUtility.divide(leftValue, rightValueObject) ;
					}
				}
				return result ;
			} else if (leftIsArray && !rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(leftValue.getClass().getComponentType(), ((Object[]) leftValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object leftValueObject = ((Object[]) leftValue)[i] ;
					if (rightValue instanceof Number && leftValueObject instanceof Number) {
						result[i] = NumberUtility.divide(leftValueObject, rightValue) ;
					}
				}
				return result ;
			}
			return null ;
		} else {
			if (leftValue instanceof Number && rightValue instanceof Number) {
				return NumberUtility.divide(leftValue, rightValue) ;
			}
			return null ;
		}
	}
	
	/**
	 * Performs minux operation. If one side is an array the other side is moduled.
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private Object module(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return null ;
		}
		
		// bio enums can be used in expressions by their string names like normal strings
		// one side of equation is bio enum, we try to convert other side also to same bio enum
		if (leftValue instanceof BioEnum && rightValue instanceof String) {
			rightValue = BioEnum.getBioEnumByName((String) rightValue, (Class<? extends BioEnum>) leftValue.getClass()) ;
		} else if (rightValue instanceof BioEnum && leftValue instanceof String) {
			leftValue = BioEnum.getBioEnumByName((String) leftValue, (Class<? extends BioEnum>) rightValue.getClass()) ;
		}
		
		 // First lets check whether any of them is an array
		boolean leftIsArray = leftValue instanceof Object[] ;
		boolean rightIsArray = rightValue instanceof Object[] ;
		if (leftIsArray || rightIsArray) {
			if (!leftIsArray && rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(rightValue.getClass().getComponentType(), ((Object[]) rightValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object rightValueObject = ((Object[]) rightValue)[i] ;
					if (leftValue instanceof Number && rightValueObject instanceof Number) {
						result[i] = NumberUtility.module(leftValue, rightValueObject) ;
					}
				}
				return result ;
			} else if (leftIsArray && !rightIsArray) {
				Object[] result = (Object[]) Array.newInstance(leftValue.getClass().getComponentType(), ((Object[]) leftValue).length) ;
				for (int i = 0; i < result.length; i++) {
					Object leftValueObject = ((Object[]) leftValue)[i] ;
					if (rightValue instanceof Number && leftValueObject instanceof Number) {
						result[i] = NumberUtility.module(leftValueObject, rightValue) ;
					}
				}
				return result ;
			}
			return null ;
		} else {
			if (leftValue instanceof Number && rightValue instanceof Number) {
				return NumberUtility.module(leftValue, rightValue) ;
			}
			return null ;
		}
	}
}
