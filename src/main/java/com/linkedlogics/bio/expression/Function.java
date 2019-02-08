package com.linkedlogics.bio.expression;

import java.util.List;

import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.exception.ValidationException;
import com.linkedlogics.bio.expression.parser.BioFunctionManager;
/**
 * Function expression executes BioFunction instance
 * @author rajab
 *
 */
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
		BioFunction func = BioFunctionManager.getFunction(function) ;
		
		try {
			if (this.parameters != null && this.parameters.size() > 0) {
				Object[] parameters = new Object[this.parameters.size()] ;
				for (int i = 0; i < parameters.length; i++) {
					parameters[i] = this.parameters.get(i).getValue(params) ;
				}
				
				if (func.validate(source, parameters)) {
					return func.getValue(source, parameters);
				}
			} else {
				if (func.validate(source, parameters)) {
					return func.getValue(source);
				}
			}
		} catch (ValidationException e) {
			throw e ;
		}
		
		return null ;
	}
}
