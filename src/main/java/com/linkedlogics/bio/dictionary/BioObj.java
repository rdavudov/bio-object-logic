package com.linkedlogics.bio.dictionary;

import java.util.ArrayList;
import java.util.HashMap;

import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.exception.DictionaryException;

/**
 * BioObj is a definition a bio object containing all definitions about its possible tags, parent objs
 * @author rdavudov
 *
 */
public class BioObj {
	/**
	 * Parent bio obj instance
	 */
	private BioObj parent ;
	/**
	 * Parent obj type
	 */
	private String type ;
	/**
	 * Parent obj name
	 */
	private String name ;
	/**
	 * Obj bio code used in serialization/deserialization
	 */
	private int code ;
	/**
	 * Obj version
	 */
	private int version ;
	/**
	 * Obj class name used to create instance for objects with this {@link #code}
	 */
	private String className ;
	/**
	 * Obj class instance used to create instance for objects with this {@link #code}
	 */
	private Class bioClass ;
	/**
	 * Map for contained bio {@link com.linkedlogics.bio.dictionary.BioTag}s based on tag code
	 */
	private HashMap<Integer, BioTag> codeMap = new HashMap<Integer, BioTag>() ;
	/**
	 * Map for contained bio {@link com.linkedlogics.bio.dictionary.BioTag}s based on tag name/key
	 */
	private HashMap<String, BioTag> nameMap = new HashMap<String, BioTag>() ;
	/**
	 * List for mandatory bio {@link com.linkedlogics.bio.dictionary.BioTag}s
	 */
	private ArrayList<BioTag> mandatoryTagList = new ArrayList<BioTag>() ;
	/**
	 * isLarge used in serialization/deserialization for encoding lengths of arrays, objects normally it is 2 bytes but if large it will be 4 bytes
	 */
	private boolean isLarge ;
	
	private int dictionary ;
	/**
	 * Creates a bio obj with code and type name and version
	 * @param code
	 * @param name
	 * @param version
	 */
	public BioObj(int code, String type, String name, int version) {
		this(0, code, type, name, version) ;
	}
	
	public BioObj(int dictionary, int code, String type, String name, int version) {
		this.dictionary = dictionary ;
		this.code = code ;
		this.type = type ;
		this.name = name ;
		this.version = version ;
	}
	
	/**
	 * Adds a tag into code map and name map. Throws an exception when duplicate tags exist
	 * @param tag
	 */
	public void addTag(BioTag tag) {
		if (!codeMap.containsKey(tag.getCode()) && !nameMap.containsKey(tag.getName())) {

			nameMap.put(tag.getName(), tag) ;
			codeMap.put(tag.getCode(), tag) ;
//			Logger.log(LoggerLevel.TRACE, "creating bio tag (code=%d, name=%s) in %s", tag.getCode(), tag.getName(), type);
			if (tag.isMandatory()) {
				mandatoryTagList.add(tag) ;
			}	
		} else {
			BioTag existingTag = codeMap.get(tag.getCode()) ;
			if (existingTag != null && (existingTag.getCode() != tag.getCode() || !existingTag.getName().equals(tag.getName()))) {
				throw new DictionaryException(String.format("duplicate tag code %d or name %s with different code %s or name %s at %s", existingTag.getCode(), existingTag.getName(), tag.getCode(), tag.getName(), getBioClass())) ;
			}
			existingTag = nameMap.get(tag.getName()) ;
			if (existingTag != null && (existingTag.getCode() != tag.getCode() || !existingTag.getName().equals(tag.getName()))) {
				throw new DictionaryException(String.format("duplicate tag code %d or name %s with different code %s or name %s at %s", existingTag.getCode(), existingTag.getName(), tag.getCode(), tag.getName(), getBioClass())) ;
			}
			
//			Logger.log(LoggerLevel.WARN, "tag code %d or name %s is overwritten in %s", tag.getCode(), tag.getName(), type);
		}	
	}
	
	public void removeTag(BioTag tag) {
		nameMap.remove(tag.getName()) ;
		codeMap.remove(tag.getCode()) ;
	}
	
	/**
	 * Retrives a tag by tag name/key
	 * @param name
	 * @return
	 */
	public BioTag getTag(String name) {
		BioTag tag = nameMap.get(name) ;
		if (tag == null && parent != null) {
			tag = parent.getTag(name) ;
		}
		if (tag == null) {
			tag = BioDictionary.getDictionary(dictionary).getSuperTag(name) ;
		}
		return tag ; 
	}
	
	/**
	 * Retrives a tag by tag code
	 * @param code
	 * @return
	 */
	public BioTag getTag(int code) {
		BioTag tag = codeMap.get(code) ;
		if (tag == null && parent != null) {
			tag = parent.getTag(code) ;
		}
		return tag ; 
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name ;
	}

	public HashMap<Integer, BioTag> getCodeMap() {
		return codeMap;
	}

	public void setCodeMap(HashMap<Integer, BioTag> codeMap) {
		this.codeMap = codeMap;
	}

	public HashMap<String, BioTag> getNameMap() {
		return nameMap;
	}

	public void setNameMap(HashMap<String, BioTag> nameMap) {
		this.nameMap = nameMap;
	}

	public ArrayList<BioTag> getMandatoryTagList() {
		return mandatoryTagList;
	}

	public void setMandatoryTagList(ArrayList<BioTag> mandatoryTagList) {
		this.mandatoryTagList = mandatoryTagList;
	}
	
	public int getCode() {
		return code;
	}

	public int getVersion() {
		return version;
	}
	
	public int getDictionary() {
		return dictionary;
	}
	
	public void setDictionary(int dictionary) {
		this.dictionary = dictionary;
	}

	public void setBioClass(Class<BioObject> bioClass) {
		this.bioClass = bioClass ;
	}

	public Class<BioObject> getBioClass() {
		return bioClass ;
	}
	
	public boolean isLarge() {
		return isLarge;
	}

	public void setLarge(boolean isLarge) {
		this.isLarge = isLarge;
	}

	public String toString() {
		return type ;
	}
}
