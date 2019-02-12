/**
 * @author rajab
 *
 */
module bio.object {
	exports com.linkedlogics.bio;
	exports com.linkedlogics.bio.exception;
	exports com.linkedlogics.bio.annotation;
	exports com.linkedlogics.bio.parser;
	exports com.linkedlogics.bio.dictionary to bio.sql ;
	exports com.linkedlogics.bio.expression to bio.sql ;
	exports com.linkedlogics.bio.dictionary.builder to bio.sql ;
	exports com.linkedlogics.bio.utility to bio.sql ;
	
	requires transitive java.xml;
	requires transitive org.json;
	requires transitive io.github.classgraph ;
	requires transitive lz4;
}