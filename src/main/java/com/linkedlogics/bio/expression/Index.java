package com.linkedlogics.bio.expression;

import java.util.List;

import com.linkedlogics.bio.BioObject;

public class Index extends Expression {
	private Expression index ;
	
	public Index(Expression index) {
		this.index = index ;
	}

	@Override
	public Object getValue(Object source, BioObject... params) {
		int i = ((Number) index.getValue(params)).intValue() ;
		if (source instanceof Object[]) {
			Object[] array = (Object[]) source ;
			if (array.length > i) {
				return array[i] ;
			}
		} else if (source instanceof List) {
			List<Object> list = (List<Object>) source ;
			if (list.size() > i) {
				return list.get(i) ;
			}
		}
		return null;
	}
}
