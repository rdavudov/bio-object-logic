package com.linkedlogics.bio.utility;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.BioTime;
import com.linkedlogics.bio.dictionary.BioEnumObj;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.exception.ParserException;
import com.linkedlogics.bio.expression.Conditional;
import com.linkedlogics.bio.expression.Constant;
import com.linkedlogics.bio.expression.Dynamic;

/**
 * It is used to conver string values to actual types based on BioType
 * @author rajab
 *
 */
public class ConversionUtility {
    /**
     * Convert value to list
     * @param value
     * @param type
     * @return
     */
    public static Object convertAsList(BioType target, Object value) {
    	if (value instanceof List) {
    		List list = (List) value ;
    		List convertedList = new ArrayList() ;
    		for (int i = 0; i < list.size(); i++) {
				Object converted = convert(target, list.get(i)) ;
				if (converted != null) {
					convertedList.add(converted) ;
				}
			}
    		return convertedList ;
    	} else if (value instanceof BioObject[]) {
    		return convertArrayToList((BioObject[]) value) ;
    	} else if (target == BioType.BioObject) {
    		String[] strValues = value.toString().split(",,");
    		ArrayList<BioObject> values = new ArrayList<BioObject>();
    		for (int i = 0; i < strValues.length; i++) {
    			values.add((BioObject) convert(target, strValues[i]));
    		}
    		return values;
    	} else {
    		String[] strValues = value.toString().split(",");
    		ArrayList<Object> values = new ArrayList<Object>();
    		for (int i = 0; i < strValues.length; i++) {
    			values.add(convert(target, strValues[i]));
    		}
    		return values;
    	}
    }

    /**
     * Returns bio type based on value
     * @param value
     * @return
     */
    public static BioType getType(Object value) {
        if (value.getClass().isArray()) {
            if (value instanceof String[]) {
            	if (((String[]) value).length > 0) {
            		String[] array = (String[]) value ;
            		for (int i = 0; i < array.length; i++) {
            			if (((String[]) value)[i].length() != ((String[]) value)[i].getBytes().length) {
                			return BioType.UtfString ;
                		}
					}
            	}
                return BioType.String;
            } else if (value instanceof Boolean[]) {
                return BioType.Boolean;
            } else if (value instanceof Long[]) {
                return BioType.Long;
            } else if (value instanceof Short[]) {
                return BioType.Short;
            } else if (value instanceof Float[]) {
                return BioType.Float;
            } else if (value instanceof Integer[]) {
                return BioType.Integer;
            } else if (value instanceof Double[]) {
                return BioType.Double;
            } else if (value instanceof Byte[]) {
                return BioType.Byte;
            } else if (value instanceof BioObject[]) {
                return BioType.BioObject;
            } else if (value instanceof BioEnum[]) {
            	return BioType.BioEnum ;
            } else {
                return BioType.JavaObject;
            }
        } else if (value instanceof List) {
            List list = (List) value;
            if (list.size() == 0) {
                return BioType.Unknown;
            }
            value = list.get(0);
            if (value instanceof String) {
            	if (((String) value).length() != ((String) value).getBytes().length) {
        			return BioType.UtfString ;
        		}
                return BioType.String;
            } else if (value instanceof Boolean) {
                return BioType.Boolean;
            } else if (value instanceof Long) {
                return BioType.Long;
            } else if (value instanceof Short) {
                return BioType.Short;
            } else if (value instanceof Float) {
                return BioType.Float;
            } else if (value instanceof Integer) {
                return BioType.Integer;
            } else if (value instanceof Double) {
                return BioType.Double;
            } else if (value instanceof Byte) {
                return BioType.Byte;
            } else if (value instanceof BioObject) {
                return BioType.BioObject;
            } else if (value instanceof BioEnum) {
                return BioType.BioEnum;
            } else {
                return BioType.JavaObject;
            }
        } else {
            if (value instanceof String) {
            	if (((String) value).length() != ((String) value).getBytes().length) {
        			return BioType.UtfString ;
        		}
                return BioType.String;
            } else if (value instanceof Boolean) {
                return BioType.Boolean;
            } else if (value instanceof Long) {
                return BioType.Long;
            } else if (value instanceof Short) {
                return BioType.Short;
            } else if (value instanceof Float) {
                return BioType.Float;
            } else if (value instanceof Integer) {
                return BioType.Integer;
            } else if (value instanceof Double) {
                return BioType.Double;
            } else if (value instanceof Byte) {
                return BioType.Byte;
            } else if (value instanceof BioObject) {
                return BioType.BioObject;
            } else if (value instanceof BioEnum) {
                return BioType.BioEnum;
            } else if (value instanceof Conditional) {
                return BioType.Conditional;
            } else if (value instanceof Dynamic) {
                return BioType.Dynamic;
            } else {
                return BioType.JavaObject;
            }
        }
    }
    
    /**
     * Converts potential value to bio enum array 
     * @param enumObj
     * @param value
     * @return
     */
    public static Object[] convertAsArray(BioEnumObj enumObj, Object value) {
    	Object[] array = null ;
    	// if it is already bioenum[]
    	if (value instanceof BioEnum[]) {
     		return (Object[]) value ;
     	} else if (value instanceof Object[]) {
    		// if value is an array just cast it an use it
    		array = (Object[]) value ;
    	} else {
    		// rest is an object most probably comma separated values
    		array = value.toString().split(",") ;
    	}
    	
    	// if we have an integer array then we use it
    	if (array instanceof Number[]) {
    		BioEnum[] enumArray = BioDictionary.getDictionary(enumObj.getDictionary()).getFactory().newBioEnumArray(enumObj.getCode(), array.length) ;
    		for (int i = 0; i < array.length; i++) {
				enumArray[i] = enumObj.getBioEnum(((Number) array[i]).intValue()) ;
			}
    		return enumArray ;
    	} else {
    		// here we try to first get enum by name, if no then we assume we have a number as string, so we parse it to integer and check again
    		BioEnum[] enumArray = BioDictionary.getDictionary(enumObj.getDictionary()).getFactory().newBioEnumArray(enumObj.getCode(), array.length) ;
    		for (int i = 0; i < array.length; i++) {
    			if (enumObj.getNameMap().containsKey(array[i].toString())) {
    				enumArray[i] = enumObj.getBioEnum(array[i].toString()) ;
    			} else {
    				try {
						int ordinal = Integer.parseInt(array[i].toString()) ;
						enumArray[i] = enumObj.getBioEnum(ordinal) ;
					} catch (Throwable e1) {
						enumArray[i] = null ;
					}
    			}
			}
    		
    		return enumArray ;
    	}	
    }
    
    /**
     * Converts potential value to bio enum list 
     * @param enumObj
     * @param value
     * @return
     */
    public static Object convertAsList(BioEnumObj enumObj, Object value) {
    	if (value instanceof List) {
    		List list = (List) value ;
    		List convertedList = new ArrayList() ;
    		for (int i = 0; i < list.size(); i++) {
				Object converted = convert(enumObj, list.get(i)) ;
				if (converted != null) {
					convertedList.add(converted) ;
				}
			}
    		return convertedList ;
    	} else if (value instanceof BioEnum[]) {
    		return convertArrayToList((BioEnum[]) value) ;
    	} else if (value instanceof Object[]) {
    		List list = new ArrayList<BioEnum>() ;
    		Object[] array = (Object[]) value ;
    		BioDictionary.getDictionary(enumObj.getDictionary()).getFactory().newBioEnumArray(enumObj.getCode(), array.length) ;
    		for (int i = 0; i < array.length; i++) {
				list.add(convert(enumObj, array[i])) ;
			}
    		return list ;
    	} else {
    		return convertAsList(enumObj, value.toString().split(","));
    	}
    }
    
    /**
     * Converts an object into array
     * @param target
     * @param value
     * @return
     */
    public static Object[] convertAsArray(BioType target, Object value) {
    	Object[] array = null ;
    	if (value instanceof BioObject[]){
     		return (Object[]) value ;
     	} else if (value instanceof Object[]) {
    		// if value is an array just cast it an use it
    		// we still process it because may be we array of doubles but we need array of integers according to target(BioType)
    		array = (Object[]) value ;
    	} else if (value instanceof BioObject){
    		// if value is a bio object we can only return an array with one element no way to convert it
    		array = (Object[]) Array.newInstance(value.getClass(), 1) ;
    		array[0] = value ;
    		return array ;
    	} else {
    		// rest is an object most probably comma separated values
    		array = value.toString().split(",") ;
    	}
    	
    	switch (target) {
		case String:
		case UtfString:
			String[] sArray = new String[array.length] ;
			for (int i = 0; i < array.length; i++) {
				sArray[i] = (String) convert(target, array[i]) ;
			}
			return sArray ;
		case Byte:
			Byte[] bArray = new Byte[array.length] ;
			for (int i = 0; i < array.length; i++) {
				bArray[i] = (Byte) convert(target, array[i]) ;
			}
			return bArray ;
		case Short:
			Short[] shArray = new Short[array.length] ;
			for (int i = 0; i < array.length; i++) {
				shArray[i] = (Short) convert(target, array[i]) ;
			}
			return shArray ;
		case Integer:
			Integer[] iArray = new Integer[array.length] ;
			for (int i = 0; i < array.length; i++) {
				iArray[i] = (Integer) convert(target, array[i]) ;
			}
			return iArray ;
		case Long:
			Long[] lArray = new Long[array.length] ;
			for (int i = 0; i < array.length; i++) {
				lArray[i] = (Long) convert(target, array[i]) ;
			}
			return lArray ;
		case Float:
			Float[] fArray = new Float[array.length] ;
			for (int i = 0; i < array.length; i++) {
				fArray[i] = (Float) convert(target, array[i]) ;
			}
			return fArray ;
		case Double:
			Double[] dArray = new Double[array.length] ;
			for (int i = 0; i < array.length; i++) {
				dArray[i] = (Double) convert(target, array[i]) ;
			}
			return dArray ;
		case Boolean:
			Boolean[] boolArray = new Boolean[array.length] ;
			for (int i = 0; i < array.length; i++) {
				boolArray[i] = (Boolean) convert(target, array[i]) ;
			}
			return boolArray ;
		case Time:
			Long[] tArray = new Long[array.length] ;
			for (int i = 0; i < array.length; i++) {
				tArray[i] = (Long) convert(target, array[i]) ;
			}
			return tArray ;
		case BioObject:
			if (value instanceof String) {
				array = value.toString().split(",,") ;
			}
			
			ArrayList<BioObject> list = new ArrayList<BioObject>() ;
			for (int i = 0; i < array.length; i++) {
				if (array[i] instanceof String) {
					String s = (String) array[i] ;
					if (s.startsWith("{")) {
	            		list.add(BioObject.fromJson(new JSONObject(s))) ;
	            	} else {
	            		String[] propertiesArr = s.split(";");
	            		BioObject properties = new BioObject(0);
	            		for (int j = 0; j < propertiesArr.length; j++) {
	            			String[] property = propertiesArr[j].split("\\|");
	            			boolean isPropertyArray = false;
	            			if (property[1].endsWith("[]")) {
	            				property[1] = property[1].substring(0, property[1].length() - 2);
	            				isPropertyArray = true;
	            			}
	            			if (isPropertyArray) {
	            				properties.put(property[0], convertAsArray(Enum.valueOf(BioType.class, property[1]), property[2].trim()));
	            			} else {
	            				properties.put(property[0], convert(Enum.valueOf(BioType.class, property[1]), property[2].trim()));
	            			}
	            		}
	            		list.add(properties);
	            	}
				} else if (array[i] instanceof BioObject) {
					list.add((BioObject) array[i]) ;
				}
			}
			
			array = new BioObject[list.size()] ;
			list.toArray(array) ;
			
			return array ;
		}
    	return null ;
    }
    
    /**
     * Converts an object to bio enum if it is number we check code, if it is string we check name
     * @param enumObj
     * @param value
     * @return
     */
    public static Object convert(BioEnumObj enumObj, Object value) {
    	if (value instanceof BioEnum) {
    		return value ;
    	} else if (value instanceof String)
    		if (enumObj.getNameMap().containsKey(value.toString())) {
    			return enumObj.getBioEnum(value.toString()) ;
    		} else {
    			try {
    				int ordinal = Integer.parseInt(value.toString()) ;
    				return enumObj.getBioEnum(ordinal) ;
    			} catch (Throwable e1) {
    				return null ;
    			}
    		}
    	else if (value instanceof Number) {
    		return enumObj.getBioEnum(((Number) value).intValue()) ;
    	}

    	return null ;
    } 
    
    /**
     * Converts value to bio tag value
     * @param target
     * @param value
     * @return
     */
    public static Object convert(BioType target, Object value) {
    	if (value instanceof String)
    		value = ((String) value).trim();
    	
    	switch (target) {
		case String:
		case UtfString:
			return value.toString() ;
		case Byte:
			if (value instanceof String) {
				String s = (String) value ;
				if (s.indexOf('.') > -1) {
            		s = s.substring(0, s.indexOf('.')) ;
            	}
				return Byte.parseByte(s) ;
			} else if (value instanceof Number) {
				return ((Number) value).byteValue() ;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? (byte)1 : (byte)0 ;
			}
			return null;
		case Short:
			if (value instanceof String) {
				String s = (String) value ;
				if (s.indexOf('.') > -1) {
            		s = s.substring(0, s.indexOf('.')) ;
            	}
				return Short.parseShort(s) ;
			} else if (value instanceof Number) {
				return ((Number) value).shortValue() ;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? (short)1 : (short)0 ;
			}
			return null;
		case Integer:
			if (value instanceof String) {
				String s = (String) value ;
				if (s.indexOf('.') > -1) {
            		s = s.substring(0, s.indexOf('.')) ;
            	}
				return Integer.parseInt(s) ;
			} else if (value instanceof Number) {
				return ((Number) value).intValue() ;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? 1 : 0 ;
			}
			return null;			
		case Long:
			if (value instanceof String) {
				String s = (String) value ;
				if (s.indexOf('.') > -1) {
            		s = s.substring(0, s.indexOf('.')) ;
            	}
				return Long.parseLong(s) ;
			} else if (value instanceof Number) {
				return ((Number) value).longValue() ;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? (long)1 : (long)0 ;
			}
			return null;	
		case Float:
			if (value instanceof String) {
				return Float.parseFloat(value.toString()) ;
			} else if (value instanceof Number) {
				return ((Number) value).floatValue() ;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? (float)1.0 : (float)0.0 ;
			}
			return null;			
		case Double:
			if (value instanceof String) {
				return Double.parseDouble(value.toString()) ;
			} else if (value instanceof Number) {
				return ((Number) value).doubleValue() ;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? 1.0 : 0.0 ;
			}
			return null;	
		case Boolean:
			if (value instanceof String) {
				return Boolean.parseBoolean(value.toString()) ;
			} else if (value instanceof Number) {
				return ((Number) value).intValue() > 0 ;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ;
			}
			return null;			
		case Time:
			if (value instanceof String) {
				try {
					return BioTime.parseString(value.toString()) ;
				} catch (ParserException e) {
					return Long.parseLong(value.toString()) ;
				}
			} else if (value instanceof Number) {
				return ((Number) value).longValue() ;
			} else if (value instanceof Date) {
				return ((Date) value).getTime() ;
			} 
			return null;	
		case BioObject:
			if (value instanceof String) {
				String s = (String) value ;
				if (s.startsWith("{")) {
            		return BioObject.fromJson(new JSONObject(s)) ;
            	} else {
            		String[] propertiesArr = s.split(";");
            		BioObject properties = new BioObject(0);
            		for (int i = 0; i < propertiesArr.length; i++) {
            			String[] property = propertiesArr[i].split("\\|");
            			boolean isPropertyArray = false;
            			if (property[1].endsWith("[]")) {
            				property[1] = property[1].substring(0, property[1].length() - 2);
            				isPropertyArray = true;
            			}
            			if (isPropertyArray) {
            				properties.put(property[0], convertAsArray(Enum.valueOf(BioType.class, property[1]), property[2].trim()));
            			} else {
            				properties.put(property[0], convert(Enum.valueOf(BioType.class, property[1]), property[2].trim()));
            			}
            		}
            		return properties;
            	}
			} else if (value instanceof BioObject) {
				return value ;
			}
			return null ;
		}
    	return null ;
    }
    
    private static List<Object> convertArrayToList(Object[] array) {
    	ArrayList<Object> list = new ArrayList<Object>() ;
    	for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				list.add(array[i]) ;
			}
		}
    	return list ;
    }
    
    public static void main(String[] args) {
    	Object source = new Integer[] {1, 1, 0} ;
    	Object result = ConversionUtility.convertAsArray(BioType.Boolean, source) ;
    	System.out.println(result);
    	if (result instanceof Object[]) {
    		System.out.println(Arrays.toString((Object[]) result));
    	}
    }
}
