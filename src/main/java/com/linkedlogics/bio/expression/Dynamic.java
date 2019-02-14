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
			
			
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null && params[i].getBioName().equals(key)) {
					return params[i] ;
				}
			}
			
			// sometimes object itself is given and expression tries to get a tag
			// for example
			// we have bio object A with tags t1, t2, t3 and an expression "t2"
			// in fact expression should be "a.t2" but sometimes we don't know bio name
			// but want to refer to "this" so by expression "t2" Dynamic tries to check from keys
			// when upper side of this code couldn't find anything
			
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null && params[i].has(key)) {
					return params[i].get(key) ;
				}
			}
			
			return null ;
		} else {
			return ((BioObject) source).get(key) ;
		}
	}
}
