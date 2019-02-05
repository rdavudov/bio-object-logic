package com.linkedlogics.bio.dictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.linkedlogics.bio.dictionary.builder.AnnotationReader;
import com.linkedlogics.bio.dictionary.builder.DictionaryReader;
import com.linkedlogics.bio.dictionary.builder.XmlFileReader;
import com.linkedlogics.bio.dictionary.builder.XmlResourceReader;

public class BioDictionaryBuilder {
	private List<DictionaryReader> readers = new ArrayList<DictionaryReader>();
	private HashSet<String> profiles = new HashSet<String>();
	private boolean isOnlyProfiles ;
	
	public BioDictionaryBuilder addPackage(String packageName) {
		readers.add(new AnnotationReader(packageName)) ;
		return this ;
	}
	
	public BioDictionaryBuilder addFile(String xml) {
		readers.add(new XmlFileReader(xml)) ;
		return this ;
	}
	
	public BioDictionaryBuilder addResource(String resource) {
		readers.add(new XmlResourceReader(resource)) ;
		return this ;
	}
	
	public BioDictionaryBuilder addUrl(String url) {
		readers.add(new XmlResourceReader(url)) ;
		return this ;
	}
	
	public BioDictionaryBuilder addProfile(String profile) {
		profiles.add(profile) ;
		return this ;
	}
	
	public BioDictionaryBuilder setOnlyProfiles(boolean isOnlyProfiles) {
		this.isOnlyProfiles = isOnlyProfiles ;
		return this ;
	}
	
	public BioDictionaryBuilder setDbSource() {
		//TODO: to be implemented later
		return this ;
	}
	
	public HashSet<String> getProfiles() {
		return profiles;
	}

	public boolean isOnlyProfiles() {
		return isOnlyProfiles;
	}

	public void build() {
		for (DictionaryReader reader : readers) {
			reader.read(this); 
		}
	}
}
