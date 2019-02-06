package com.linkedlogics.bio.dictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.linkedlogics.bio.compression.BioCompressor;
import com.linkedlogics.bio.compression.BioLZ4Compressor;
import com.linkedlogics.bio.encryption.BioEncrypter;
import com.linkedlogics.bio.exception.DictionaryException;
import com.linkedlogics.bio.object.Initializer;

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
    
    private static Initializer<BioCompressor> compressorInitializer = new Initializer<BioCompressor>() {
		@Override
		public BioCompressor initialize() {
			return new BioLZ4Compressor() ;
		}
	};
    private static Initializer<BioEncrypter> encrypterInitializer = new Initializer<BioEncrypter>() {
		@Override
		public BioEncrypter initialize() {
			return null;
		}
	};
	
	private static Set<String> supportedDateFormats = new HashSet<String>() ;
    
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
                throw new DictionaryException("already existing name " + obj.getType() + " in dictionary with different code " + objByName.getCode());
            } else if (objByName == null) {
                throw new DictionaryException("already existing code " + obj.getCode() + " in dictionary with different name " + objByCode.getType());
            } else if (objByCode.getCode() != obj.getCode()) {
                throw new DictionaryException("already existing name " + obj.getType() + " in dictionary with different code " + objByCode.getCode());
            } else if (!objByName.getType().equals(obj.getType())) {
                throw new DictionaryException("already existing code " + obj.getCode() + " in dictionary with different name " + objByName.getCode());
            }
        } else if (objByCode.getBioClass().isAssignableFrom(obj.getBioClass()) || objByCode.getBioClass() == obj.getBioClass()) {
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
                throw new DictionaryException("already existing name " + enumObj.getName() + " in dictionary with different code " + enumByName.getCode());
            } else if (enumByName == null) {
                throw new DictionaryException("already existing code " + enumObj.getCode() + " in dictionary with different name " + enumByCode.getCode());
            } else if (enumByCode.getCode() != enumObj.getCode()) {
                throw new DictionaryException("already existing name " + enumObj.getName() + " in dictionary with different code " + enumByCode.getCode());
            } else if (!enumByName.getName().equals(enumObj.getName())) {
                throw new DictionaryException("already existing code " + enumObj.getCode() + " in dictionary with different name " + enumByName.getCode());
            }
        } else if (enumByCode.getBioClass().isAssignableFrom(enumObj.getBioClass()) || enumByCode.getBioClass() == enumObj.getBioClass()) {
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
    	BioTag tagByCode = superTagCodeMap.get(tag.getCode());
    	BioTag tagByName = superTagNameMap.get(tag.getName());
    	if (tagByCode == null && tagByName == null) {
    		superTagCodeMap.put(tag.getCode(), tag);
    		superTagNameMap.put(tag.getName(), tag);
    	} else if (tagByCode != tagByName) {
    		if (tagByCode == null) {
    			throw new DictionaryException("already existing super tag name " + tag.getName() + " in dictionary with different code " + tagByName.getCode());
    		} else if (tagByName == null) {
    			throw new DictionaryException("already existing super tagcode " + tag.getCode() + " in dictionary with different name " + tagByCode.getCode());
    		} else if (tagByCode.getCode() != tag.getCode()) {
    			throw new DictionaryException("already existing super tagname " + tag.getName() + " in dictionary with different code " + tagByCode.getCode());
    		} else if (!tagByName.getName().equals(tag.getName())) {
    			throw new DictionaryException("already existing super tagcode " + tag.getCode() + " in dictionary with different name " + tagByName.getCode());
    		}
    	}
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
    /**
     * Returns super tags code map
     * @return
     */
    public HashMap<Integer, BioTag> getSuperTagCodeMap() {
		return superTagCodeMap;
	}
    /**
     * Returns super tags name map
     * @return
     */
	public HashMap<String, BioTag> getSuperTagNameMap() {
		return superTagNameMap;
	}
	
	/**
	 * Creates an empty map object for bio objects
	 * @return
	 */
	public static Map<String, Object> createMapObject() {
    	try {
			return (Map<String, Object>) mapObjectClass.getConstructor().newInstance() ;
		} catch (Throwable e) {
			throw new RuntimeException(e) ;
		}
    }
	
	/**
	 * Returns object creation factory for bio objects
	 * @return
	 */
    public BioFactory getFactory() {
    	if (factory == null) {
    		factory = new BioFactory(this) ;
    	}
    	return factory ;
    }
    
    /**
     * Returns new bio compressor
     * @return
     */
    public static BioCompressor getCompressor() {
    	return compressorInitializer.initialize() ;
    }
    /**
     * Returns new bio encrypter
     * @return
     */
    public static BioEncrypter getEncrypter() {
    	return encrypterInitializer.initialize() ;
    }
	
	private static HashMap<Integer, BioDictionary> dictionaryMap = new HashMap<Integer, BioDictionary>() ;
	
    private static BioDictionary dictionary ;
 
    /**
     * Returns all dictionaries as map
     * @return
     */
    static HashMap<Integer, BioDictionary> getDictionaryMap() {
		return dictionaryMap;
	}

    /**
     * Returns default dictionary
     * @return
     */
	public static BioDictionary getDictionary() {
        return dictionary ;
    }
    
	/**
	 * Returns dictionary and creates an empty one if it is not found
	 * @param dictionary
	 * @return
	 */
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
    
    /**
     * Returns dictionary by id
     * @param dictionary
     * @return
     */
    public static BioDictionary getDictionary(int dictionary) {
    	BioDictionary dict = dictionaryMap.get(dictionary) ;
    	if (dict == null) {
    		throw new DictionaryException(dictionary + " dictionary is not found") ;
    	}
    	return dict ;
    }
    
    /**
     * Sets compressor initializer
     * @param compressorInitializer
     */
    static void setCompressorInitializer(Initializer<BioCompressor> compressorInitializer) {
    	BioDictionary.compressorInitializer = compressorInitializer ;
    }
    /**
     * Sets encrypter initializer
     * @param encrypterInitializer
     */
    static void setEncrypterInitializer(Initializer<BioEncrypter> encrypterInitializer) {
    	BioDictionary.encrypterInitializer = encrypterInitializer ;
    }
    /**
     * Sets map object class
     * @param mapObjectClass
     */
	static void setMapObjectClass(Class<? extends Map> mapObjectClass) {
		BioDictionary.mapObjectClass = mapObjectClass;
	}
	
	/**
	 * Add supported date format
	 * @param format
	 */
	static void addSupportedDateFormat(String format) {
		supportedDateFormats.add(format) ;
	}
	
	/**
	 * Return supported date formats
	 * @return
	 */
	public static List<String> getSupportedDateFormats() {
		return supportedDateFormats.stream().collect(Collectors.toList()) ;
	}
}
