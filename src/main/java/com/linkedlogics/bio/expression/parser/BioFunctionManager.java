package com.linkedlogics.bio.expression.parser;

import java.util.HashMap;

import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.exception.DictionaryException;
import com.linkedlogics.bio.exception.ExpressionException;

/**
 * Singleton function manager class
 * @author rajab
 *
 */
public class BioFunctionManager {
	private static HashMap<String, Class<? extends BioFunction>> functionClassMap = new HashMap<String, Class<? extends BioFunction>>() ;;
	private static HashMap<String, BioFunction> functionCacheMap = new HashMap<String, BioFunction>() ;
	/**
	 * Stores functions class and may be a cached instance in maps
	 * @param functionName
	 * @param functionClass
	 * @param isCached
	 */
	public static void setFunction(String functionName, Class<? extends BioFunction> functionClass, boolean isCached) {
		functionClassMap.put(functionName, functionClass) ;
		if (isCached) {
			try {
				BioFunction function = functionClass.getConstructor().newInstance() ;
				functionCacheMap.put(functionName, function) ;
			} catch (InstantiationException e) {
				throw new DictionaryException("instantiation exception for " + functionClass.getName(), e) ;
			} catch (NoSuchMethodException e) {
				throw new DictionaryException("missing default constructor in " + functionClass.getName(), e) ;
			} catch (Throwable e) {
				throw new DictionaryException(e) ;
			}
		}
	}
	/**
	 * Returns function instance
	 * @param functionName
	 * @return
	 */
	public static BioFunction getFunction(String functionName) {
		if (functionCacheMap.containsKey(functionName)) {
			return functionCacheMap.get(functionName) ;
		}
		Class<? extends BioFunction> functionClass = functionClassMap.get(functionName) ;
		if (functionClass == null) {
			throw new ExpressionException("missing function " + functionName) ;
		}
		try {
			return functionClass.getConstructor().newInstance() ;
		} catch (InstantiationException e) {
			throw new ExpressionException("instantiation exception for " + functionClass.getName(), e) ;
		} catch (NoSuchMethodException e) {
			throw new ExpressionException("missing default constructor in " + functionClass.getName(), e) ;
		} catch (Throwable e) {
			throw new ExpressionException(e) ;
		}
	}
}
