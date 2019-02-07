package com.linkedlogics.bio.expression;

import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;

public abstract class Expression implements BioExpression {
	protected boolean isExists ;
	protected boolean isNegative ;
	protected String text ;
	protected Expression next ;
	
	/**
	 * Evaluates expression
	 */
	public Object getValue(BioObject... params) {
		return getValueWithNext(null, params) ;
	}
	
	/**
	 * Evaluates and checks next expression such as (person.brother.name.upperCase()
	 * @param source
	 * @param params
	 * @return
	 */
	protected Object getValueWithNext(Object source, BioObject... params) {
		Object value = getValue(source, params) ;
		
		if (next != null && value != null) {
			value = next.getValueWithNext(value, params) ;
		}
		
		// we check existence flag and use it 
		if (isExists) {
			value = value != null ;
		}
		
		// we check negation flag
		if (isNegative) {
			if (value instanceof Boolean) {
				return !((Boolean) value).booleanValue() ;
			} else {
				return !Boolean.parseBoolean(value.toString()) ;
			}
		}
		
		return value ;
	}
	
	/**
	 * Evalutes current expression
	 * @param source
	 * @param params
	 * @return
	 */
	protected abstract Object getValue(Object source, BioObject... params) ;
	
	/**
	 * Swaps negative flag
	 */
	public void setNegative() {
		isNegative = !isNegative ;
	}
	/**
	 * Checks negative flag
	 * @return
	 */
	public boolean isNegative() {
		return isNegative;
	}
	/**
	 * Sets negative flag
	 * @param isNegative
	 */
	public void setNegative(boolean isNegative) {
		this.isNegative = isNegative;
	}
	/**
	 * Checks exists flag
	 * @return
	 */
	public boolean isExists() {
		return isExists;
	}
	/**
	 * Sets exists flag
	 */
	public void setExists() {
		this.isExists = true;
	}
	/**
	 * Gets next expression
	 * @return
	 */
	public Expression getNext() {
		return next;
	}
	/**
	 * Sets next expression
	 * @param next
	 */
	public void setNext(Expression next) {
		this.next = next;
	}
	/**
	 * Gets text representation of expression
	 * @return
	 */
	public String getText() {
		return text;
	}
	/**
	 * Sets text representation of expression
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * Returns text representation of expression with flags
	 */
	public String toString() {
		return text ;
	}
}