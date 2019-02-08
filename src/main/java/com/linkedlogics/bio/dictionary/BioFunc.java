package com.linkedlogics.bio.dictionary;

import java.lang.reflect.InvocationTargetException;

import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.exception.ExpressionException;

/**
 * Represents a bio function
 * @author rajab
 *
 */
public class BioFunc {
	/**
	 * Name of functions
	 */
	private String name ;
	/**
	 * Bio function class
	 */
	private Class<? extends BioFunction> funcClass ;
	/**
	 * Function version
	 */
	private int version ;
	/**
	 * Function dictionary code
	 */
	private int dictionary ;
	/**
	 * To cache flag
	 */
	private boolean isCached ;
	/**
	 * Function cached instance
	 */
	private BioFunction cached ;
	
	public BioFunc(String name, Class<? extends BioFunction> funcClass, boolean isCached, int dictionary, int version) {
		this.name = name ;
		this.funcClass = funcClass ;
		this.isCached = isCached ;
		this.dictionary = dictionary ;
		this.version = version ;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<? extends BioFunction> getFuncClass() {
		return funcClass;
	}

	public void setFuncClass(Class<? extends BioFunction> funcClass) {
		this.funcClass = funcClass;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDictionary() {
		return dictionary;
	}

	public void setDictionary(int dictionary) {
		this.dictionary = dictionary;
	}

	public boolean isCached() {
		return isCached;
	}

	public void setCached(boolean isCached) {
		this.isCached = isCached;
	}

	public BioFunction getCached() {
		return cached;
	}

	public void setCached(BioFunction cached) {
		this.cached = cached;
	}
	
	public BioFunction getFunction() {
		if (cached != null) {
			return cached ;
		}
		
		try {
			return funcClass.getConstructor().newInstance() ;
		} catch (Throwable e) {
			throw new ExpressionException(e) ;
		}
	}
}
