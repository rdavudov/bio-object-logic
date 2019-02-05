package com.linkedlogics.bio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
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
@Repeatable(value=BioTagCompatibilityList.class)
public @interface BioTagCompatibility {
	String type() ;
	boolean isArray() default false ;
	boolean isList() default false ;
	Class javaClass() default void.class;
	int version() ;
}
