package com.linkedlogics.bio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import com.linkedlogics.bio.dictionary.BioEnumObj;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.dictionary.builder.AnnotationReader;
import com.linkedlogics.bio.dictionary.builder.DictionaryReader;
import com.linkedlogics.bio.dictionary.builder.XmlReader;
import com.linkedlogics.bio.exception.DictionaryException;

/**
 * This is dictionary builder must be called at the beginning of application in order to setup all dictionary information
 * @author rdavudov
 *
 */
public class BioDictionaryBuilder {
	protected List<DictionaryReader> readers = new ArrayList<DictionaryReader>();
	protected HashSet<String> profiles = new HashSet<String>();
	protected boolean isOnlyProfiles ;
	protected BioTagHasher tagHahser = new BioTagHasher() ;
	
	/**
	 * Adding a package name for gathering bio obj info from annotations
	 * @param packageName
	 * @return
	 */
	public BioDictionaryBuilder addPackage(String packageName) {
		readers.add(new AnnotationReader(packageName)) ;
		return this ;
	}
	/**
	 * Adding xml file path for gathering bio obj info from xml file
	 * @param xml
	 * @return
	 */
	public BioDictionaryBuilder addFile(String xmlFile) {
		try {
			readers.add(new XmlReader(new FileInputStream(xmlFile))) ;
			return this ;
		} catch (FileNotFoundException e) {
			throw new DictionaryException(e) ;
		}
	}
	/**
	 * Adding xml resource name for gathering bio obj info from xml
	 * @param resource
	 * @return
	 */
	public BioDictionaryBuilder addResource(String resource) {
		readers.add(new XmlReader(this.getClass().getClassLoader().getResourceAsStream(resource))) ;
		return this ;
	}
	/**
	 * Adding url resource name for gathering bio obj info from xml
	 * @param url
	 * @return
	 */
	public BioDictionaryBuilder addUrl(String url) {
		try {
			readers.add(new XmlReader(new URL(url).openStream())) ;
			return this ;
		} catch (Throwable e) {
			throw new DictionaryException(e) ;
		}
	}
	/**
	 * Adding profile to be considered while parsing all bio obj definitions
	 * @param profile
	 * @return
	 */
	public BioDictionaryBuilder addProfile(String profile) {
		profiles.add(profile) ;
		return this ;
	}
	/**
	 * Setting to parse only and only provided profile bio obj definitions
	 * @param isOnlyProfiles
	 * @return
	 */
	public BioDictionaryBuilder setOnlyProfiles(boolean isOnlyProfiles) {
		this.isOnlyProfiles = isOnlyProfiles ;
		return this ;
	}
	/**
	 * Adding db resource name for gathering bio obj info from tables
	 * @return
	 */
	public BioDictionaryBuilder setDbSource() {
		//TODO: to be implemented later
		return this ;
	}
	/**
	 * Setting compression initializer which should return a BioCompressor instance
	 * @param compressorInitializer
	 * @return
	 */
	public BioDictionaryBuilder setCompressorInitializer(BioInitializer<BioCompressor> compressorInitializer) {
		BioDictionary.setCompressorInitializer(compressorInitializer);
		return this ;
	}
	/**
	 * Setting encryption initializer which should return a BioEncrypter instance
	 * @param encrypterInitializer
	 * @return
	 */
	public BioDictionaryBuilder setEncrypterInitializer(BioInitializer<BioEncrypter> encrypterInitializer) {
		BioDictionary.setEncrypterInitializer(encrypterInitializer);
		return this ;
	}
	
	/**
	 * Sets map object class
	 * @param mapObjectClass
	 * @return
	 */
	public BioDictionaryBuilder setMapObjectClass(Class<? extends Map> mapObjectClass) {
		BioDictionary.setMapObjectClass(mapObjectClass);
		return this ;
	}
	
	/**
	 * This format is used while exporting time values
	 * @param format
	 * @return
	 */
	public BioDictionaryBuilder setBioDateFormat(String format) {
		addSupportDateFormat(format) ;
		BioTime.DATE_FORMAT = format ;
		return this ;
	}
	
	/**
	 * This format is used while exporting time values
	 * @param format
	 * @return
	 */
	public BioDictionaryBuilder setBioDateTimeFormat(String format) {
		addSupportDateFormat(format) ;
		BioTime.DATETIME_FORMAT = format ;
		return this ;
	}
	
	/**
	 * Adds supported date format used in parsing date strings
	 * @param format
	 * @return
	 */
	public BioDictionaryBuilder addSupportDateFormat(String format) {
		try {
			new SimpleDateFormat(format) ;
			BioDictionary.addSupportedDateFormat(format) ;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e) ;
		}
		return this ;
	}
	
	/**
	 * This format is used while exporting time values
	 * @param timeZone
	 * @return
	 */
	public BioDictionaryBuilder setBioTimeZone(String timeZone) {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone)) ;
		return this ;
	}
	
	public BioTagHasher getTagHahser() {
		return tagHahser;
	}
	public void setTagHahser(BioTagHasher tagHahser) {
		this.tagHahser = tagHahser;
	}
	/**
	 * Returns profiles
	 * @return
	 */
	public HashSet<String> getProfiles() {
		return profiles;
	}

	/**
	 * Returns is only profiles flag
	 * @return
	 */
	public boolean isOnlyProfiles() {
		return isOnlyProfiles;
	}
	
	/**
	 * Must be called at first because it constructs all dictionary can be found in class path, URL path etc.
	 */
	public void build() {
		BioDictionary.getOrCreateDictionary(0) ;
		
		if (readers.size() == 0) {
			readers.add(new AnnotationReader()) ;
		}
		
		for (DictionaryReader reader : readers) {
			reader.read(this); 
		}
		
		for (Entry<Integer, BioDictionary> d : BioDictionary.getDictionaryMap().entrySet()) {
			validate(d.getValue());
		}
	}
	
	/**
	 * Validates all dictionary and sets references to bio objs or bio enum objs in tags
	 * @param dictionary
	 */
    private void validate(BioDictionary dictionary) {
        // Checking dependency types and validating
        for (Entry<Integer, BioObj> objEntry : dictionary.getCodeMap().entrySet()) {
            BioObj obj = objEntry.getValue();
            // if we have a tag with type but Obj with that type doesn't exist in dictionary we remove those tags
            ArrayList<BioTag> missingObjectTags = new ArrayList<BioTag>() ;
            for (Entry<Integer, BioTag> entry : obj.getCodeMap().entrySet()) {
                BioTag tag = entry.getValue();
                if (tag.getType() == BioType.BioObject && tag.getObjName() != null && !tag.getObjName().equals(BioType.BioObject.toString())) {
                    BioObj bioObj = null;
                    String objName = tag.getObjName();
                    if (objName != null) {
                        bioObj = dictionary.getObjByType(objName);
                        if (bioObj == null) {
                            BioEnumObj enumObj;
                            enumObj = dictionary.getBioEnumObj(objName);
                            if (enumObj == null) {
//                            	Logger.log(LoggerLevel.WARN, "bio object %s is not found in dictionary belonging to %s for tag %s", objName, obj.getType(), tag.getName()) ;
                            	missingObjectTags.add(tag) ;
                            } else {
                                tag.setType(BioType.BioEnum);
                                tag.setEnumObj(enumObj);
                            }
                        } else {
                            tag.setObj(bioObj);
                        }
                    }
                }
            }
            
            for (BioTag bioTag : missingObjectTags) {
				obj.removeTag(bioTag);
			}
        }
        // if we have a super tag with type but Obj with that type doesn't exist in dictionary we remove those tags
        ArrayList<BioTag> missingObjectSuperTags = new ArrayList<BioTag>() ;
        for (BioTag superTag : dictionary.getSuperTagCodeMap().values()) {
            if (superTag.getType() == BioType.BioObject && superTag.getObjName() != null && !superTag.getObjName().equals(BioType.BioObject.toString())) {
                BioObj bioObj = null;
                String objName = superTag.getObjName();
                if (objName != null) {
                    bioObj = dictionary.getObjByType(objName);
                    if (bioObj == null) {
                    	missingObjectSuperTags.add(superTag) ;
                    } else {
                        superTag.setObj(bioObj);
                    }
                }
            }
        }
        
        for (BioTag bioTag : missingObjectSuperTags) {
        	dictionary.removeSuperTag(bioTag);
		}
    }
}
