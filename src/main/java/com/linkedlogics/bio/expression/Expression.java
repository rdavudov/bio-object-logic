package com.linkedlogics.bio.expression;

import java.util.LinkedList;
import java.util.List;

import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;

public abstract class Expression implements BioExpression {
	protected boolean isExists ;
	protected boolean isNegative ;
	protected String text ;
	protected Expression next ;
	
	public Object getValue(BioObject... params) {
		Object value = getValue(null, params) ;
		
		if (next != null && value != null) {
			return next.getValue(value, params) ;
		} else {
			return value ;
		}
	}
	
	public abstract Object getValue(Object source, BioObject... params) ;
	
	public void setNegative() {
		isNegative = !isNegative ;
	}

	public boolean isNegative() {
		return isNegative;
	}

	public void setNegative(boolean isNegative) {
		this.isNegative = isNegative;
	}

	public boolean isExists() {
		return isExists;
	}

	public void setExists() {
		this.isExists = true;
	}

	public Expression getNext() {
		return next;
	}

	public void setNext(Expression next) {
		this.next = next;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setExists(boolean isExists) {
		this.isExists = isExists;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder() ;
		if (isNegative) {
			s.append("!") ;
		}
		if (isExists) {
			s.append("?") ;
		}
		s.append(text) ;
		if (next != null) {
			s.append(".").append(next.toString()) ;
		}
		return s.toString() ;
	}
}