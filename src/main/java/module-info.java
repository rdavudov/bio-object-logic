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
	exports com.linkedlogics.bio.dictionary;
	exports com.linkedlogics.bio.expression to com.linkedlogics.bio.sql ;
	exports com.linkedlogics.bio.dictionary.builder to com.linkedlogics.bio.sql ;
	exports com.linkedlogics.bio.stream to com.linkedlogics ;
	
	provides com.linkedlogics.bio.BioCompressor with com.linkedlogics.bio.compression.BioLZ4Compressor;
	provides com.linkedlogics.bio.BioEncrypter with com.linkedlogics.bio.encryption.AESEncrypter, com.linkedlogics.bio.encryption.RC4Encrypter;
	
	requires transitive java.xml;
	requires transitive org.json;
	requires transitive io.github.classgraph ;
	requires transitive lz4;
}