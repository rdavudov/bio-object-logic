package com.linkedlogics.bio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BioFunc {
	String name() ;
	int version() default 0 ;
	int dictionary() default 0 ;
	boolean isCached() default false ;
}
