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
			
			// sometimes object itself is given and expression tries to get a tag
			// for example
			// we have bio object A with tags t1, t2, t3 and an expression "t2"
			// in fact expression should be "a.t2" but sometimes we don't know bio name
			// but want to refer to "this" so by expression "t2" Dynamic tries to check from keys
			// when upper side of this code couldn't find anything
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
