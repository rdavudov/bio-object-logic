package com.linkedlogics.bio.dictionary;

import java.util.HashMap;
import java.util.Map;

import com.linkedlogics.bio.utility.DictionaryUtility;

/**
 * 
 * @author rajab
 *
 */
public class BioDictionary {
	private int code ;
	
    /**
     * Map for retrieving BioObj based on object code
     */
    private HashMap<Integer, BioObj> codeMap = new HashMap<Integer, BioObj>();
    /**
     * Map for retrieving BioObj based on object type
     */
    private HashMap<String, BioObj> typeMap = new HashMap<String, BioObj>();
    /**
     * Map for retrieving BioObj based on object name
     */
    private HashMap<String, BioObj> nameMap = new HashMap<String, BioObj>();
    /**
     * Map for retrieving SuperTag based on tag code
     */
    private HashMap<Integer, BioTag> superTagCodeMap = new HashMap<Integer, BioTag>();
    /**
     * Map for retrieving SuperTag based on tag name
     */
    private HashMap<String, BioTag> superTagNameMap = new HashMap<String, BioTag>();
    /**
     * Map for retrieving EnumObj based on enum name
     */
    private HashMap<Integer, BioEnumObj> enumCodeMap = new HashMap<Integer, BioEnumObj>();
    /**
     * Map for retrieving EnumObj based on enum code
     */
    private HashMap<String, BioEnumObj> enumTypeMap = new HashMap<String, BioEnumObj>();
    /**
     * Factory class for creating bio objects;
     */
    private BioFactory factory ;
    
    private static Class<? extends Map> mapObjectClass = HashMap.class ;
    
    public BioDictionary() {

    }
    
    public BioDictionary(int code) {
    	this.code = code ;
    }
    
    public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	/**
     * Adds a BioObject definition {@link com.linkedlogics.bio.dictionary.BioObj} instance to codeMap and nameMap
     * throws an exception if same code or same name already exists in either map
     *
     * @param obj
     */
    public void addObj(BioObj obj) {
        BioObj objByCode = codeMap.get(obj.getCode());
        BioObj objByName = typeMap.get(obj.getType());

        if (objByCode == null && objByName == null) {
            codeMap.put(obj.getCode(), obj);
            typeMap.put(obj.getType(), obj);
            nameMap.put(obj.getName(), obj) ;
        } else if (objByCode != objByName) {
            if (objByCode == null) {
                throw new RuntimeException("already existing name " + obj.getType() + " in dictionary with different code " + objByName.getCode());
            } else if (objByName == null) {
                throw new RuntimeException("already existing code " + obj.getCode() + " in dictionary with different name " + objByCode.getType());
            } else if (objByCode.getCode() != obj.getCode()) {
                throw new RuntimeException("already existing name " + obj.getType() + " in dictionary with different code " + objByCode.getCode());
            } else if (!objByName.getType().equals(obj.getType())) {
                throw new RuntimeException("already existing code " + obj.getCode() + " in dictionary with different name " + objByName.getCode());
            }
        } else if (objByCode.getBioClass().isAssignableFrom(obj.getBioClass())) {
            codeMap.put(obj.getCode(), obj);
            typeMap.put(obj.getType(), obj);
            nameMap.put(obj.getName(), obj);
        }
    }
    
    /**
     * Retrieves bio obj by type
     *
     * @param type
     * @return
     */
    public BioObj getObjByType(String type) {
        return typeMap.get(type);
    }
    /**
     * Retrieves bio obj by name
     *
     * @param name
     * @return
     */
    public BioObj getObjByName(String name) {
        return nameMap.get(name);
    }
    /**
     * Retrieves latest bio obj by code
     *
     * @param code
     * @return
     */
    public BioObj getObjByCode(int code) {
        return codeMap.get(code);
    }
    
	/**
     * Adds a BioEnum definition {@link com.linkedlogics.bio.dictionary.BioEnumObj} instance to codeMap and nameMap
     * throws an exception if same code or same name already exists in either map
     *
     * @param object
     */
    public void addEnumObj(BioEnumObj enumObj) {
        BioEnumObj enumByCode = enumCodeMap.get(enumObj.getCode());
        BioEnumObj enumByName = enumTypeMap.get(enumObj.getName());

        if (enumByCode == null && enumByName == null) {
            enumTypeMap.put(enumObj.getName(), enumObj);
            enumCodeMap.put(enumObj.getCode(), enumObj);
        } else if (enumByCode != enumByName) {
            if (enumByCode == null) {
                throw new RuntimeException("already existing name " + enumObj.getName() + " in dictionary with different code " + enumByName.getCode());
            } else if (enumByName == null) {
                throw new RuntimeException("already existing code " + enumObj.getCode() + " in dictionary with different name " + enumByCode.getCode());
            } else if (enumByCode.getCode() != enumObj.getCode()) {
                throw new RuntimeException("already existing name " + enumObj.getName() + " in dictionary with different code " + enumByCode.getCode());
            } else if (!enumByName.getName().equals(enumObj.getName())) {
                throw new RuntimeException("already existing code " + enumObj.getCode() + " in dictionary with different name " + enumByName.getCode());
            }
        } else if (enumByCode.getBioClass().isAssignableFrom(enumObj.getBioClass())) {
            enumCodeMap.put(enumObj.getCode(), enumObj);
            enumTypeMap.put(enumObj.getName(), enumObj);
        }
    }
    /**
     * Retrieves bio enum obj by code
     * @param code
     * @return
     */
    public BioEnumObj getBioEnumObj(int code) {
        return enumCodeMap.get(code);
    }
    /**
     * Retrieves bio enum obj by type
     * @param code
     * @return
     */
    public BioEnumObj getBioEnumObj(String name) {
        return enumTypeMap.get(name);
    }
   
    /**
     * Adds super tag definition
     * @param tag
     */
    public void addSuperTag(BioTag tag) {
        if (superTagCodeMap.containsKey(tag.getCode()) || superTagNameMap.containsKey(tag.getName())) {
            throw new RuntimeException("already exists in dictionary " + tag.getName());
        }
        superTagCodeMap.put(tag.getCode(), tag);
        superTagNameMap.put(tag.getName(), tag);
    }
    
    /**
     * Removes super tag
     * @param tag
     */
    public void removeSuperTag(BioTag tag) {
    	superTagCodeMap.remove(tag.getCode());
        superTagNameMap.remove(tag.getName());
    }
    
    /**
     * Retrieves a super tag definition based on tag name
     *
     * @param name
     * @return
     */
    public BioTag getSuperTag(String name) {
        return superTagNameMap.get(name);
    }

    /**
     * Retrieves a super tag definition based on tag code
     *
     * @param code
     * @return
     */
    public BioTag getSuperTag(int code) {
        return superTagCodeMap.get(code);
    }

    /**
     * Checks where there is an obj with this type
     * @param type
     * @return
     */
    public boolean isBioObject(String type) {
        return typeMap.containsKey(type);
    }
    
    /**
     * Checks where there is an enum with this type
     * @param type
     * @return
     */
    public boolean isBioEnum(String type) {
        return enumTypeMap.containsKey(type);
    }
    
    /**
     * Returns enum type map
     * @return
     */
    public HashMap<String, BioEnumObj> getEnumTypeMap() {
        return enumTypeMap;
    }
    /**
     * Returns enum code map
     * @return
     */
    public HashMap<Integer, BioEnumObj> getEnumCodeMap() {
        return enumCodeMap;
    }
    /**
     * Returns obj code map
     * @return
     */
    public HashMap<Integer, BioObj> getCodeMap() {
        return codeMap;
    }
    /**
     * Returns obj type map
     * @return
     */
    public HashMap<String, BioObj> getTypeMap() {
        return typeMap;
    }
    /**
     * Returns obj name map
     * @return
     */
    public HashMap<String, BioObj> getNameMap() {
        return nameMap;
    }	
    
    public static Map<String, Object> createMapObject() {
    	try {
			return (Map<String, Object>) mapObjectClass.getConstructor().newInstance() ;
		} catch (Throwable e) {
			throw new RuntimeException(e) ;
		}
    }
    
    public BioFactory getFactory() {
    	if (factory == null) {
    		factory = new BioFactory(this) ;
    	}
    	return factory ;
    }
	
	private static HashMap<Integer, BioDictionary> dictionaryMap = new HashMap<Integer, BioDictionary>() ;
	
    private static BioDictionary dictionary = new BioDictionary() ;
 
    public static BioDictionary getDictionary() {
        return dictionary ;
    }
    
    public static BioDictionary getOrCreateDictionary(int dictionary) {
    	BioDictionary d = dictionaryMap.get(dictionary) ;
    	if (d == null) {
    		synchronized(dictionaryMap) {
    			if (!dictionaryMap.containsKey(dictionary)) {
	    			d = new BioDictionary() ;
	    			d.setCode(dictionary);
	    			dictionaryMap.put(dictionary, d) ;
	    			if (dictionary == 0) {
	    				BioDictionary.dictionary = d ;
	    			}
	    			return d ;
    			} else {
    				return dictionaryMap.get(dictionary) ;
    			}
    		}
    	}
    	return d ;
    }
    
    public static BioDictionary createDictionary() {
        return DictionaryUtility.create() ;
    }
    
    public static BioDictionary getDictionary(int dictionary) {
    	return dictionaryMap.get(dictionary) ;
    }
}
