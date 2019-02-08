package com.linkedlogics.bio.expression;

import java.util.List;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.dictionary.BioFunc;
import com.linkedlogics.bio.exception.ExpressionException;
import com.linkedlogics.bio.exception.ValidationException;
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
		BioFunc func = null ;
		if (source != null && source instanceof BioObject) {
			func = BioDictionary.getDictionary(((BioObject) source).getBioDictionary()).getFunc(function) ;
		} else {
			func = BioDictionary.getDictionary().getFunc(function) ;
		}
		
		if (func != null) {
			try {
				BioFunction f = func.getFunction() ;
				if (this.parameters != null && this.parameters.size() > 0) {
					Object[] parameters = new Object[this.parameters.size()] ;
					for (int i = 0; i < parameters.length; i++) {
						parameters[i] = this.parameters.get(i).getValue(params) ;
					}
					
					if (f.validate(source, parameters)) {
						return f.getValue(source, parameters);
					}
				} else {
					if (f.validate(source, parameters)) {
						return f.getValue(source);
					}
				}
			} catch (ValidationException e) {
				throw e ;
			}
			
			return null ;
		} else {
			throw new ExpressionException("missing function " + function) ;
		}
	}
}
