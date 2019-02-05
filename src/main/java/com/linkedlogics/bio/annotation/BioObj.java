package com.linkedlogics.bio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BioObj annotation for indicating class is a BioObject 
 * @author rdavudov
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BioObj {
	int code() ;
	int version() default 0 ;
	boolean isLarge() default false ;
	int dictionary() default 0 ;
}
