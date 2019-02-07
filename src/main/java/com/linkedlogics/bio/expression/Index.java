package com.linkedlogics.bio.expression;

import java.util.List;

import com.linkedlogics.bio.BioObject;

public class Index extends Expression {
	private int index ;
	
	public Index(int index) {
		this.index = index ;
	}

	@Override
	public Object getValue(Object source, BioObject... params) {
		if (source instanceof Object[]) {
			Object[] array = (Object[]) source ;
			if (array.length > index) {
				return array[index] ;
			}
		} else if (source instanceof List) {
			List<Object> list = (List<Object>) source ;
			if (list.size() > index) {
				return list.get(index) ;
			}
		}
		return null;
	}
}
