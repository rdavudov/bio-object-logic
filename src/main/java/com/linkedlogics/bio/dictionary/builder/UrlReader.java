package com.linkedlogics.bio.dictionary.builder;

import java.net.URL;

import com.linkedlogics.bio.BioDictionaryBuilder;
import com.linkedlogics.bio.exception.DictionaryException;

public class UrlReader extends XmlReader implements DictionaryReader {
	private String url ;
	
	public UrlReader(String url) {
		this.url = url ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		try {
			parse(new URL(url).openStream()) ;
		} catch (Throwable e) {
			throw new DictionaryException("invalid URL " + url, e) ;
		}
	}
}
