package com.linkedlogics.bio.expression;

import java.util.List;

import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.BioObject;

public class Function extends Expression {
	private String function ;
	private List<Expression> parameters ;
	
	public Function(String function) {
		this.function = function ;
	}
	
	public Function(String function, List<Expression> parameters) {
		this.function = function ;
		this.parameters = parameters ;
	}
	
	@Override
	public Object getValue(Object source, BioObject... params) {
		BioFunction func = null ;
		
		Object[] parameters = new Object[this.parameters.size()] ;
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = this.parameters.get(i).getValue(params) ;
		}
		
		return func.getValue(source, parameters);
	}
	
}
