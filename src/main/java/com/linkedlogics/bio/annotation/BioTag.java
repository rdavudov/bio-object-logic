package com.linkedlogics.bio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining properties of a Tag and all needed information during serialization/deserialization
 *
 * @author rdavudov
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BioTag {
	int code() default 0 ;
	String type() ;
	boolean isArray() default false ;
	boolean isList() default false ;
	boolean isMandatory() default false ;
	boolean isEncodable() default true ;
	boolean isExportable() default true ;
	boolean isInheritable() default true ;
	boolean isProtected() default false ;
	String initial() default "" ;
	String expression() default "" ;
	String[] trimKeys() default "" ;
	String[] inverseTrimKeys() default "" ;
	String useKey() default "" ;
	String sortKey() default "" ;
	Class javaClass() default void.class;
}
