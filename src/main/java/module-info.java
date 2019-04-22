/**
 * @author rajab
 *
 */
module com.linkedlogics.bio {
	exports com.linkedlogics.bio;
	exports com.linkedlogics.bio.exception;
	exports com.linkedlogics.bio.annotation;
	exports com.linkedlogics.bio.parser;
	exports com.linkedlogics.bio.utility;
	exports com.linkedlogics.bio.dictionary to com.linkedlogics.bio.sql ;
	exports com.linkedlogics.bio.expression to com.linkedlogics.bio.sql ;
	exports com.linkedlogics.bio.dictionary.builder to com.linkedlogics.bio.sql ;
	exports com.linkedlogics.bio.stream to com.linkedlogics.processor, com.linkedlogics ;
	
	requires transitive java.xml;
	requires transitive org.json;
	requires transitive io.github.classgraph ;
	requires transitive lz4;
}