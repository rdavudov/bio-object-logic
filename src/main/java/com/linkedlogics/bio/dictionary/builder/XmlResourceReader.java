package com.linkedlogics.bio.dictionary.builder;

import com.linkedlogics.bio.BioDictionaryBuilder;

public class XmlResourceReader extends XmlReader implements DictionaryReader {
	private String xmlResource ;
	
	public XmlResourceReader(String xmlResource) {
		this.xmlResource = xmlResource ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		parse(this.getClass().getClassLoader().getResourceAsStream(xmlResource)) ;
	}
}
