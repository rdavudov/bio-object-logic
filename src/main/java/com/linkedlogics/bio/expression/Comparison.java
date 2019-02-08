package com.linkedlogics.bio.expression;

import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.utility.NumberUtility;
import com.linkedlogics.bio.utility.StringUtility;

/**
 * Comparison checks both sides for logical operation. Cross type checks are supported such as integer-string, long-double etc.
 * @author rajab
 *
 */
public class Comparison extends Expression implements Operands {
	protected Expression leftExpr ;
	protected Expression rightExpr ;
	protected int operation ;
	
	protected Expression middleExpr ;
	protected int middleOperation ;
	
	public Comparison(Expression leftExpr, int operation, Expression rightExpr) {
		this.leftExpr = leftExpr ;
		this.operation = operation ;
		this.rightExpr = rightExpr ;
	}
	
	public Comparison(Expression leftExpr, int middleOperation, Expression middleExpr, int operation, Expression rightExpr) {
		this(leftExpr, operation, rightExpr) ;
		this.middleExpr = middleExpr ;
		this.middleOperation = middleOperation ;
	}
	
	/**
	 * Returns comparison value 
	 */
	@Override
	public Object getValue(Object source, BioObject... params) {
		if (middleExpr == null) {
			return compare(leftExpr, operation, rightExpr, params) ;
		}
		return compare(leftExpr, middleOperation, middleExpr, params) && compare(middleExpr, operation, rightExpr, params);
	}
	
	/**
	 * Compares two expressions
	 * @param leftExpr
	 * @param operation
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	public boolean compare(Expression leftExpr, int operation, Expression rightExpr, BioObject... params) {
		try {
			switch (operation) {
			case EQUAL:
				return equal(leftExpr, rightExpr, params) ;
			case NOT_EQUAL:
				return notEqual(leftExpr, rightExpr, params) ;
			case GREATER:
				return greater(leftExpr, rightExpr, params) ;
			case GREATER_EQUAL:
				return greaterOrEqual(leftExpr, rightExpr, params) ;
			case SMALLER:
				return smaller(leftExpr, rightExpr, params) ;
			case SMALLER_EQUAL:
				return smallerOrEqual(leftExpr, rightExpr, params) ;
			case AND:
				return and(leftExpr, rightExpr, params) ;
			case OR:
				return or(leftExpr, rightExpr, params) ;
			}
		} catch(Throwable e) {
			//TODO: log this exception
		}
		
		return false ;
	}
	
	/**
	 * We check equality of two expressions
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private boolean equal(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return false ;
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
			boolean arrayComparison = false ;
			Object[] leftValues = leftIsArray ? (Object[]) leftValue : new Object[] {leftValue} ;
			Object[] rightValues = rightIsArray ? (Object[]) rightValue : new Object[] {rightValue} ;

			//If both sides are arrays
			if (leftIsArray && rightIsArray) {
				// and lengths are not equal then FALSE
				if (leftValues.length != rightValues.length) {
					return false ;
				}
				// or any value is not equal then FALSE
				for (int i = 0; i < leftValues.length; i++) {
					if (!leftValues[i].equals(rightValues[i])) {
						return false ;
					}
				}
				
				return true ;
			} else {
				// here we check one by one for equality
				for (Object left : leftValues) {
					for (Object right : rightValues) {
						if (left instanceof Number || right instanceof Number) {
							arrayComparison = NumberUtility.equal(left, right) ;
						} else if (left instanceof String || right instanceof String) {
							return StringUtility.notEqual(left, right) ;
						}  else {
							arrayComparison = left.equals(right) ;
						}

						if (arrayComparison) {
							return arrayComparison ;
						}
					}
				}
			}
			
			return false ;
		} else {
			if (leftValue instanceof Number || rightValue instanceof Number) {
				return NumberUtility.equal(leftValue, rightValue) ;
			} else if (leftValue instanceof String || rightValue instanceof String) {
				return StringUtility.equal(leftValue, rightValue) ;
			}
			
			return leftValue.equals(rightValue) ;
		}
	}
	
	/**
	 * We check inequality of two expressions
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private boolean notEqual(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return false ;
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
			boolean arrayComparison = false ;
			Object[] leftValues = leftIsArray ? (Object[]) leftValue : new Object[] {leftValue} ;
			Object[] rightValues = rightIsArray ? (Object[]) rightValue : new Object[] {rightValue} ;
			
			//If both sides are arrays
			if (leftIsArray && rightIsArray) {
				// and lengths are not equal then TRUE
				if (leftValues.length != rightValues.length) {
					return true ;
				}
				// or any value is not equal then TRUE
				for (int i = 0; i < leftValues.length; i++) {
					if (!leftValues[i].equals(rightValues[i])) {
						return true ;
					}
				}
				return false ;
			} else {
				for (Object left : leftValues) {
					for (Object right : rightValues) {
						if (left instanceof Number || right instanceof Number) {
							arrayComparison = NumberUtility.notEqual(left, right) ;
						} else if (left instanceof String || right instanceof String) {
							return StringUtility.notEqual(left, right) ;
						} else {
							arrayComparison = !left.equals(right) ;
						}

						if (arrayComparison) {
							return arrayComparison ;
						}
					}
				}
			}
			
			return false ;
		} else {
			if (leftValue instanceof Number || rightValue instanceof Number) {
				return NumberUtility.notEqual(leftValue, rightValue) ;
			} else if (leftValue instanceof String || rightValue instanceof String) {
				return StringUtility.notEqual(leftValue, rightValue) ;
			}
			
			return !leftValue.equals(rightValue) ;
		}
	}
	
	/**
	 * We get greater of two expressions
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private boolean greater(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return false ;
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
			Object[] leftValues = leftIsArray ? (Object[]) leftValue : new Object[] {leftValue} ;
			Object[] rightValues = rightIsArray ? (Object[]) rightValue : new Object[] {rightValue} ;

			for (Object left : leftValues) {
				for (Object right : rightValues) {
					if (!NumberUtility.greater(left, right)) {
						return false ;
					}
				}
			}
			
			return true ;
		} else {
			return NumberUtility.greater(leftValue, rightValue) ;
		}
	}
	
	/**
	 * We get greater or equal of two expressions
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private boolean greaterOrEqual(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return false ;
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
			Object[] leftValues = leftIsArray ? (Object[]) leftValue : new Object[] {leftValue} ;
			Object[] rightValues = rightIsArray ? (Object[]) rightValue : new Object[] {rightValue} ;

			for (Object left : leftValues) {
				for (Object right : rightValues) {
					if (!NumberUtility.greaterOrEqual(left, right)) {
						return false ;
					}
				}
			}
			
			return true ;
		} else {
			return NumberUtility.greaterOrEqual(leftValue, rightValue) ;
		}
	}
	
	/**
	 * We get smaller of two expressions
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private boolean smaller(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return false ;
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
			Object[] leftValues = leftIsArray ? (Object[]) leftValue : new Object[] {leftValue} ;
			Object[] rightValues = rightIsArray ? (Object[]) rightValue : new Object[] {rightValue} ;

			for (Object left : leftValues) {
				for (Object right : rightValues) {
					if (!NumberUtility.smaller(left, right)) {
						return false ;
					}
				}
			}
			
			return true ;
		} else {
			return NumberUtility.smaller(leftValue, rightValue) ;
		}
	}
	
	/**
	 * We get smaller or equal of two expressions
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private boolean smallerOrEqual(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		Object rightValue = rightExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null || rightValue == null) {
			return false ;
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
			Object[] leftValues = leftIsArray ? (Object[]) leftValue : new Object[] {leftValue} ;
			Object[] rightValues = rightIsArray ? (Object[]) rightValue : new Object[] {rightValue} ;

			for (Object left : leftValues) {
				for (Object right : rightValues) {
					if (!NumberUtility.smallerOrEqual(left, right)) {
						return false ;
					}
				}
			}
			
			return true ;
		} else {
			return NumberUtility.smallerOrEqual(leftValue, rightValue) ;
		}
	}
	
	/**
	 * We get and two expressions
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private boolean and(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null) {
			return false ;
		}

		boolean leftBooleanValue = false ;
		if (leftValue instanceof Boolean) {
			leftBooleanValue = ((Boolean) leftValue).booleanValue() ;
		} else {
			leftBooleanValue = Boolean.parseBoolean(leftValue.toString()) ;
		}
		
		if (leftBooleanValue) {
			Object rightValue = rightExpr.getValue(params) ;
			// If any of sides is null it is not possible to determine result
			if (rightValue == null) {
				return false ;
			}
			
			boolean rightBooleanValue = false ;
			if (leftValue instanceof Boolean) {
				rightBooleanValue = ((Boolean) rightValue).booleanValue() ;
			} else {
				rightBooleanValue = Boolean.parseBoolean(rightValue.toString()) ;
			}
			
			return leftBooleanValue && rightBooleanValue ;
		}
		
		return false ;
	}
	
	/**
	 * We get or of two expressions
	 * @param leftExpr
	 * @param rightExpr
	 * @param params
	 * @return
	 */
	private boolean or(Expression leftExpr, Expression rightExpr, BioObject... params) {
		Object leftValue = leftExpr.getValue(params) ;
		// If any of sides is null it is not possible to determine result
		if (leftValue == null) {
			return false ;
		}

		boolean leftBooleanValue = false ;
		if (leftValue instanceof Boolean) {
			leftBooleanValue = ((Boolean) leftValue).booleanValue() ;
		} else {
			leftBooleanValue = Boolean.parseBoolean(leftValue.toString()) ;
		}
		
		if (!leftBooleanValue) {
			Object rightValue = rightExpr.getValue(params) ;
			// If any of sides is null it is not possible to determine result
			if (rightValue == null) {
				return false ;
			}
			
			boolean rightBooleanValue = false ;
			if (leftValue instanceof Boolean) {
				rightBooleanValue = ((Boolean) rightValue).booleanValue() ;
			} else {
				rightBooleanValue = Boolean.parseBoolean(rightValue.toString()) ;
			}
			
			return leftBooleanValue || rightBooleanValue ;
		}
		
		return true ;
	}
}
