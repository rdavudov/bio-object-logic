package com.linkedlogics.bio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining properties of a SuperTag. SuperTag is a tag which can be serialized/deserialized without having any BioObj container
 * It is very useful when you have generate system tags for most of objects and don't want to add them as tag for each of the definition. But without
 * definition they will be ignored by {@link com.linkedlogics.bio.parser.BioObjectBinaryParser} Therefore, once defined as SuperTag parser will encode it anyway.
 * 
 * SuperTag codes are minus values because in order not to be mixed with actual BioObject tags
 * @author rdavudov
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BioSuperTag {
	int code() ;
	String type() ;
	boolean isArray() default false ;
	boolean isList() default false ;
	boolean isMandatory() default false ;
	boolean isEncodable() default true ;
	boolean isExportable() default true ;
	String initial() default "" ;
	String expression() default "" ;
	String[] trimKeys() default "" ;
	String[] inverseTrimKeys() default "" ;
	String useKey() default "" ;
	String sortKey() default "" ;
	Class javaClass() default void.class;
}
