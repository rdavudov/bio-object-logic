package com.linkedlogics.bio.dictionary.builder;

import com.linkedlogics.bio.dictionary.BioDictionaryBuilder;

public class UrlReader implements DictionaryReader {
	private String url ;
	
	public UrlReader(String url) {
		this.url = url ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		
	}
}
