/**
 * @author rajab
 *
 */
module bio.object {
	exports com.linkedlogics.bio.object;
	exports com.linkedlogics.bio.exception;
	exports com.linkedlogics.bio.annotation;
	exports com.linkedlogics.bio.dictionary;
	exports com.linkedlogics.bio.parser;
	
	requires transitive java.xml;
	requires transitive org.json;
	requires io.github.classgraph ;
	requires lz4;
}