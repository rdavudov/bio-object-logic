package com.linkedlogics.bio.dictionary.builder;

import com.linkedlogics.bio.dictionary.BioDictionaryBuilder;

public class XmlFileReader implements DictionaryReader {
	private String xmlFile ;
	
	public XmlFileReader(String xmlFile) {
		this.xmlFile = xmlFile ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		
	}
}
