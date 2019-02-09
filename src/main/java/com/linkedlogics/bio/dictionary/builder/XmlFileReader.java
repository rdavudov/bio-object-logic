package com.linkedlogics.bio.dictionary.builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.linkedlogics.bio.BioDictionaryBuilder;
import com.linkedlogics.bio.exception.DictionaryException;

public class XmlFileReader extends XmlReader implements DictionaryReader {
	private String xmlFile ;
	
	public XmlFileReader(String xmlFile) {
		this.xmlFile = xmlFile ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		try {
			parse(new FileInputStream(xmlFile)) ;
		} catch (FileNotFoundException e) {
			throw new DictionaryException("xml file not found " + xmlFile, e) ;
		}
	}
}
