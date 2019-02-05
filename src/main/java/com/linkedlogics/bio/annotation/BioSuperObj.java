package com.linkedlogics.bio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate classes which contains {@link com.linkedlogics.bio.annotation.BioSuperTag}
 * @author rdavudov
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BioSuperObj {
	int dictionary() default 0 ;
}
