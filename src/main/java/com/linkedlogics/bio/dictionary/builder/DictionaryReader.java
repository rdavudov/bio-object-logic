package com.linkedlogics.bio.dictionary.builder;

import java.util.HashSet;

import com.linkedlogics.bio.annotation.BioProfile;
import com.linkedlogics.bio.annotation.BioProfiles;
import com.linkedlogics.bio.dictionary.BioDictionaryBuilder;

public interface DictionaryReader {
	public void read(BioDictionaryBuilder builder) ;
	
	default boolean checkProfile(String className, HashSet<String> profiles, boolean isOnlyProfile) {
		try {
			Class c = Class.forName(className) ;
			if (c.isAnnotationPresent(BioProfile.class)) {
				BioProfile annotation = (BioProfile) c.getAnnotation(BioProfile.class) ;
				return profiles.contains(annotation.profile()) ;
			} else if (c.isAnnotationPresent(BioProfiles.class)) {
				BioProfile[] annotations = (BioProfile[]) c.getAnnotationsByType(BioProfile.class) ;
				for (int i = 0; i < annotations.length; i++) {
					if (profiles.contains(annotations[i].profile())) {
						return true ;
					}
				}
				return false ;
			} else if (isOnlyProfile) {
				return false ;
			}
			return true ;
		} catch (ClassNotFoundException|NoClassDefFoundError e) {
			throw new RuntimeException(e) ;
		}
	}
}
