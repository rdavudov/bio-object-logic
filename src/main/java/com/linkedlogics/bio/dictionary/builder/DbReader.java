package com.linkedlogics.bio.dictionary.builder;

import com.linkedlogics.bio.BioDictionaryBuilder;

public class DbReader implements DictionaryReader {
	private String jdbc ;
	
	public DbReader(String jdbc) {
		this.jdbc = jdbc ;
	}

	@Override
	public void read(BioDictionaryBuilder builder) {
		
	}
}
