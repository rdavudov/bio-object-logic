package com.linkedlogics.bio;

public class BioTagHasher {
	public int hash(String tag) {
		return Math.abs(tag.hashCode()) % 32767 ;
	}
}
