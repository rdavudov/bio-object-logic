package com.linkedlogics.bio.dictionary;

import java.util.HashSet;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.BioTime;
import com.linkedlogics.bio.expression.BioExpressionParser;
import com.linkedlogics.bio.utility.ConversionUtility;

/**
 * BioTag is a definition a single Key-Value information inside a BioObject
 * @author rdavudov
 *
 */
public class BioTag {
	/**
	 * Key/Name of tag
	 */
	protected String name ;
	/**
	 * Code for tag
	 */
	protected int code ;
	/**
	 * Type of tag
	 */
	protected BioType type ;
	/**
	 * If its an Enum then java class for the Enum
	 */
	protected Class javaClass ;
	/**
	 * Indicates whether this tag is mandatory for bio object instance
	 */
	protected boolean isMandatory ;
	/**
	 * Indicates whether this tag is encodable
	 */
	protected boolean isEncodable ;
	/**
	 * Indicates whether this tag is xml exportable
	 */
	protected boolean isExportable ;
	/**
	 * Indicates whether this tag is an array
	 */
	protected boolean isArray ;
	/**
	 * Indicates whether this tag is a list
	 */
	protected boolean isList ;
	/**
	 * Indicates whether this tag is trimmable
	 */
	protected boolean isTrim ;
	/**
	 * Indicates {@link com.linkedlogics.bio.dictionary.BioObj} instance in dictionary if {@link #type} is BioObject or any other class extending BioObject
	 */
	protected BioObj obj ;
	/**
	 * Indicates {@link com.linkedlogics.bio.dictionary.BioObj} instance in dictionary using type name
	 */
	protected String objName ;
	/**
	 * Indicates that this tag's type is BioMap and useKey defines which tag is used as a key
	 */
	protected String useKey ;
	/**
	 * Indicates that this tag's type is BioList and useKey defines which tag is used to sort
	 */
	protected String sortKey ;
	/**
	 * Indicates trimmable keys for the tags
	 */
	protected String[] trimKeys ;
	
	protected HashSet<String> trimKeySet = new HashSet<String>() ;
	
	protected String[] inverseTrimKeys ;
	
	protected HashSet<String> inverseTrimKeySet = new HashSet<String>() ;
	/**
	 * Indicates possible BioEnums for the tag
	 */
	protected BioEnum[] enums ;

	protected BioEnumObj enumObj;
	
	protected boolean hasRelation ;
	
	protected String initial ;
	
	protected String expression ;
	
	public BioTag(int code, String name, BioType type) {
		this(code, name, type, null) ;
	}
	
	public BioTag(int code, String name, BioType type, Class javaClass) {
		this.code = code ;
		this.name = name ;
		this.type = type ;
		this.javaClass = javaClass ;
		if (javaClass == null) {
			javaClass = getJavaClass(getType()) ;
		}
	}
	
	public BioEnumObj getEnumObj() {
		return enumObj;
	}

	public void setEnumObj(BioEnumObj enumObj) {
		this.enumObj = enumObj;
	}

	public void setType(BioType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public int getCode() {
		return code;
	}
	public BioType getType() {
		return type;
	}
	public boolean isMandatory() {
		return isMandatory;
	}
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	public boolean isEncodable() {
		return isEncodable;
	}
	public void setEncodable(boolean isEncodable) {
		this.isEncodable = isEncodable;
	}
	public boolean isExportable() {
		return isExportable;
	}
	public void setExportable(boolean isExportable) {
		this.isExportable = isExportable;
	}
	public boolean isArray() {
		return isArray;
	}
	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}
	public boolean isList() {
		return isList;
	}
	public void setList(boolean isList) {
		this.isList = isList;
	}

	public BioObj getObj() {
		return obj;
	}

	public void setObj(BioObj obj) {
		this.obj = obj;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public Class getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(Class javaClass) {
		this.javaClass = javaClass;
	}

	public String getUseKey() {
		return useKey;
	}

	public void setUseKey(String useKey) {
		this.useKey = useKey;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String[] getTrimKeys() {
		return trimKeys;
	}
	
	public boolean isTrimKey(String trimKey) {
		if (trimKey != null) {
			return trimKeySet.contains(trimKey) ;
		}
		return false ;
	}

	public void setTrimKeys(String[] trimKeys) {
		this.trimKeys = trimKeys;
		this.isTrim = trimKeys != null && trimKeys.length > 0 ;
		if (isTrim) {
			for (int i = 0; i < trimKeys.length; i++) {
				trimKeySet.add(trimKeys[i]) ;
			}
		}
	}
	
	public String[] getInverseTrimKeys() {
		return inverseTrimKeys;
	}
	
	public boolean isInverseTrimKey(String inverseTrimKey) {
		if (inverseTrimKey != null) {
			return inverseTrimKeySet.contains(inverseTrimKey) ;
		}
		return false ;
	}

	public void setInverseTrimKeys(String[] inverseTrimKeys) {
		this.inverseTrimKeys = inverseTrimKeys;
		for (int i = 0; i < inverseTrimKeys.length; i++) {
			inverseTrimKeySet.add(inverseTrimKeys[i]) ;
		}
	}

	public boolean isTrim() {
		return isTrim;
	}

	public boolean hasRelation() {
		return hasRelation;
	}

	public void setRelation(boolean hasRelation) {
		this.hasRelation = hasRelation;
	}
	
	public void setInitial(String initial) {
		this.initial = initial ;
	}
	
	public String getInitial() {
		return initial ;
	}
	
	public void setExpression(String expression) {
		this.expression = expression ;
	}
	
	public String getExpression() {
		return expression ;
	}
	
	public Object getInitialtValue(String initial) {
		if (initial == null) {
			return null ;
		}
		
		switch (getType()) {
		case UtfString:
		case String:
			return initial ;
		case Boolean:
			return Boolean.parseBoolean(initial) ;
		case Byte:
			return Byte.parseByte(initial) ;
		case Short:
			return Short.parseShort(initial) ;
		case Float:
			return Float.parseFloat(initial) ;
		case Double:
			return Double.parseDouble(initial) ;
		case Integer:
			return Integer.parseInt(initial) ;
		case BioEnum:
			return enumObj.getBioEnum(initial) ;
		case BioObject:
			return BioDictionary.getDictionary(obj.getDictionary()).getFactory().newBioObject(obj.getCode()) ;
		case Long:
			return Long.parseLong(initial) ;
		case Time:
			return BioTime.getTime(initial) ;
		case Properties:
			return ConversionUtility.convert(initial, BioType.Properties) ;
		}
		
		return null ;
	}
	
	public Class getJavaClass(BioType type) {
		switch (getType()) {
		case UtfString:
		case String:
			return String.class ;
		case Boolean:
			return Boolean.class ;
		case Byte:
			return Byte.class ;
		case Short:
			return Short.class ;
		case Float:
			return Float.class ;
		case Double:
			return Double.class ;
		case Integer:
			return Integer.class ;
		case BioEnum:
			return BioEnum.class ;
		case BioObject:
			return BioObject.class ;
		case Long:
		case Time:
			return Long.class ;
		}
		return null ;
	}
	
	public Class getJavaArrayClass(BioType type) {
		switch (getType()) {
		case UtfString:
		case String:
			return String[].class ;
		case Boolean:
			return Boolean[].class ;
		case Byte:
			return Byte[].class ;
		case Short:
			return Short[].class ;
		case Float:
			return Float[].class ;
		case Double:
			return Double[].class ;
		case Integer:
			return Integer[].class ;
		case BioEnum:
			return BioEnum[].class ;
		case BioObject:
			return BioObject[].class ;
		case Long:
		case Time:
			return Long[].class ;
		}
		return null ;
	}
	
	public Class getPrimitiveJavaClass(BioType type) {
		switch (getType()) {
		case UtfString:
		case String:
			return String.class ;
		case Boolean:
			return boolean.class ;
		case Byte:
			return byte.class ;
		case Short:
			return short.class ;
		case Float:
			return float.class ;
		case Double:
			return double.class ;
		case Integer:
			return int.class ;
		case BioEnum:
			return int.class ;
		case BioObject:
			return BioObject.class ;
		case Long:
		case Time:
			return long.class ;
		}
		return null ;
	}
	
	public Class getPrimitiveJavaArrayClass(BioType type) {
		switch (getType()) {
		case UtfString:
		case String:
			return String[].class ;
		case Boolean:
			return boolean[].class ;
		case Byte:
			return byte[].class ;
		case Short:
			return short[].class ;
		case Float:
			return float[].class ;
		case Double:
			return double[].class ;
		case Integer:
			return int[].class ;
		case BioEnum:
			return int[].class ;
		case BioObject:
			return BioObject[].class ;
		case Long:
		case Time:
			return long[].class ;
		}
		return null ;
	}
}
