package com.linkedlogics.bio;

import com.linkedlogics.bio.dictionary.BioEnumObj;

/**
 * BioEnum represents enums. Each enum has ordinal and name. Name is used during presentation but ordinal and code is used as keeping the value and serialization
 * @author rajab
 *
 */
public abstract class BioEnum extends Number {
	private static final long serialVersionUID = 1L;
	/**
	 * Bio Dictionary Id 
	 */
	protected final int dictionary ;
	/**
	 * Unique (within same dictionary) object code
	 */
	protected final int code ;
	/**
	 * Enum name
	 */
	protected final String name;
	/**
	 * Enum ordinal
	 */
	protected final int ordinal;

	/**
	 * It is protected because BioEnum only provides basis it should be extended and added enum options
	 * @param ordinal
	 * @param name
	 * @param code
	 * @param dictionary
	 */
	protected BioEnum(final int ordinal, final String name, final int code, final int dictionary) {
		this.code = code ;
		this.ordinal = ordinal;
		this.name = name;
		this.dictionary = dictionary ;
	}
	
	protected BioEnum(final int ordinal, final String name, final int code) {
		this(ordinal, name, code, 0) ;
	}
	
	protected BioEnum(final int ordinal, final String name) {
		this(ordinal, name, 0, 0) ;
	}
	
	public static <T extends BioEnum> T getBioEnumByCode(int dictionary, int ordinal, Class<T> enumClass) {
		try {
			BioEnumObj enumObj = BioDictionary.getDictionary(dictionary).getBioEnumObj(enumClass.getSimpleName()) ;
			return (T) enumObj.getCodeMap().get(ordinal) ;
		} catch (Exception e) {
			return null ;
		}
	}
	
	public static <T extends BioEnum> T getBioEnumByCode(int ordinal, Class<T> enumClass) {
		return getBioEnumByCode(0, ordinal, enumClass) ;
	}
	
	public static <T extends BioEnum> T getBioEnumByName(int dictionary, String name, Class<T> enumClass) {
		try {
			BioEnumObj enumObj = BioDictionary.getDictionary(dictionary).getBioEnumObj(enumClass.getSimpleName()) ;
			return (T) enumObj.getNameMap().get(name) ;
		} catch (Exception e) {
			return null ;
		}
	}
	
	public static <T extends BioEnum> T getBioEnumByName(String name, Class<T> enumClass) {
		return getBioEnumByName(0, name, enumClass) ;
	}
	
	public int getBioCode() {
		return code;
	}

	public int getBioDictionary() {
		return dictionary;
	}

	public int getOrdinal(){
		return this.ordinal;
	}

	public String getName(){
		return this.name;
	}

	public String toString(){
		return this.name;
	}

	public int intValue() {
		return ordinal;
	}

	public long longValue() {
		return ordinal;
	}

	public float floatValue() {
		return ordinal;
	}

	public double doubleValue() {
		return ordinal;
	}
}
