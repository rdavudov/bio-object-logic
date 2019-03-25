package com.linkedlogics.bio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bio Enum obj defines bio enums
 * @author rdavudov
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BioEnumObj {
    int code() default 0;
    String type() default "" ; 
    int version() default 0 ;
	int dictionary() default 0 ;
}
