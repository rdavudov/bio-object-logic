package com.linkedlogics.bio.expression;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;

public class Filter extends Expression {
	private BioExpression expression ;
	
	public Filter(String condition) {
		this.expression = BioExpression.parse(condition) ;
	}

	@Override
	public Object getValue(Object source, BioObject... params) {
		Stream<BioObject> stream = null ;
		if (source instanceof BioObject[]) {
			stream = Arrays.stream((BioObject[]) source) ;
		} else if (source instanceof List) {
			stream = ((List) source).stream() ;
		} else {
			return null ;
		}
		
		return stream.filter(o -> {
			return expression.getBooleanValue(o) ;
		}).collect(Collectors.toList()) ;
	}
}
