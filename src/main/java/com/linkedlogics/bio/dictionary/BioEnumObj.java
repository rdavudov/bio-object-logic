package com.linkedlogics.bio.dictionary;

import java.util.HashMap;

import com.linkedlogics.bio.BioEnum;

/**
 * BioEnumObj is a definition bio enum objects
 * @author rdavudov
 *
 */
public class BioEnumObj {

    private String name ;

    private int code ;
    
    private int version ;
    
    private int dictionary ;

    private String className ;

    private Class bioClass ;

    private HashMap<Integer, BioEnum> codeMap = new HashMap<Integer, BioEnum>() ;

    private HashMap<String, BioEnum> nameMap = new HashMap<String, BioEnum>() ;
    
    private boolean isCodeGenerated ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
    public boolean isCodeGenerated() {
		return isCodeGenerated;
	}

	public void setCodeGenerated(boolean isCodeGenerated) {
		this.isCodeGenerated = isCodeGenerated;
	}

	public int getDictionary() {
		return dictionary;
	}
    
	public void setDictionary(int dictionary) {
		this.dictionary = dictionary;
	}

	public String getClassName() {
        return className;
    }

	public void setClassName(String className) throws ClassNotFoundException {
		this.className = className;
		this.bioClass = Class.forName(className) ;
	}
    
    public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Class getBioClass() {
        return bioClass;
    }

    public void setBioClass(Class bioClass) {
        this.bioClass = bioClass;
    }

    public HashMap<Integer, BioEnum> getCodeMap() {
        return codeMap;
    }

    public void setCodeMap(HashMap<Integer, BioEnum> codeMap) {
        this.codeMap = codeMap;
    }

    public HashMap<String, BioEnum> getNameMap() {
        return nameMap;
    }

    public void setNameMap(HashMap<String, BioEnum> nameMap) {
        this.nameMap = nameMap;
    }
    
    public BioEnumObj() {
    	
    }

    public BioEnumObj(int code, String name){
    	this(0, code, name) ;
    }

    public BioEnumObj(int code){
    	this(0, code, null) ;
    }

    public BioEnumObj(int dictionary, int code, String name) {
    	this.dictionary = dictionary ;
    	this.code = code; 
    	this.name = name;
    }

    public void addValue(BioEnum value) {
        if (!codeMap.containsKey(value.getOrdinal()) && !nameMap.containsKey(value.getName())) {
            nameMap.put(value.getName(), value) ;
            codeMap.put(value.getOrdinal(), value) ;
        }
    }

    public BioEnum getBioEnum(String name) {
        return nameMap.get(name) ;
    }

    public BioEnum getBioEnum(int code) {
        return codeMap.get(code) ;
    }
    
    public String toString() {
    	return name ;
    }
}
