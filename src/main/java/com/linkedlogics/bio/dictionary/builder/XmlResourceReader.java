package com.linkedlogics.bio.dictionary.builder;

import com.linkedlogics.bio.BioDictionaryBuilder;

public class XmlResourceReader implements DictionaryReader {
	private String xmlResource ;
	
	public XmlResourceReader(String xmlResource) {
		this.xmlResource = xmlResource ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		
	}
}
