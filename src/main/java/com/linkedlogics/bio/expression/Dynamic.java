package com.linkedlogics.bio.expression;

import java.util.Arrays;
import java.util.Optional;

import com.linkedlogics.bio.BioObject;

public class Dynamic extends Expression {
	private String key ;
	
	public Dynamic(String key) {
		this.key = key ;
	}
	
	@Override
	public Object getValue(Object source, BioObject... params) {
		if (source == null) {
			Optional<BioObject> object = Arrays.stream(params).filter(o -> {
				return o != null && key.equals(o.getBioName()) ;
			}).findFirst() ;
			
			if (object.isPresent()) {
				return object.get() ;
			}
			
			Optional<Object> value = Arrays.stream(params).filter(o -> {
				return o != null && o.has(key) ;
			}).map(o -> {
				return o.get(key) ;
			}).findFirst() ;
			
			if (value.isPresent()) {
				return value.get() ;
			}
			
			return null ;
		} else {
			return ((BioObject) source).get(key) ;
		}
	}
}
