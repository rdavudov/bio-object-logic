package com.linkedlogics.bio.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.object.BioEnum;
import com.linkedlogics.bio.object.BioObject;
import com.linkedlogics.bio.time.BioTime;

public class ConversionUtility {
    public static Object convert(String value, BioType type) {
        return convert(value, type, false);
    }

    public static Object convert(String value, BioType type, boolean isArray) {
        if (isArray) {
            return convertAsArray(value, type);
        }
        value = value.trim();
        switch (type) {
            case Byte:
            	if (value.indexOf('.') > -1) {
            		value = value.substring(0, value.indexOf('.')) ;
            	}
            	return Byte.parseByte(value);
            case Short:
            	if (value.indexOf('.') > -1) {
            		value = value.substring(0, value.indexOf('.')) ;
            	}
                return Short.parseShort(value);
            case Boolean:
                return Boolean.parseBoolean(value);
            case Double:
                return Double.parseDouble(value);
            case Float:
                return Float.parseFloat(value);
            case Integer:
            	if (value.indexOf('.') > -1) {
            		value = value.substring(0, value.indexOf('.')) ;
            	}
                return Integer.parseInt(value);
            case JavaObject:
                return null;
            case JavaEnum:
                return value;
            case BioEnum:
                return value;
            case Long:
            	if (value.indexOf('.') > -1) {
            		value = value.substring(0, value.indexOf('.')) ;
            	}
                return Long.parseLong(value);
            case BioObject:
            case Properties:
            	if (value.startsWith("{")) {
            		return BioObject.fromJson(new JSONObject(value)) ;
            	} else {
            		String[] propertiesArr = value.split(";");
            		BioObject properties = new BioObject(0);
            		for (int i = 0; i < propertiesArr.length; i++) {
            			String[] property = propertiesArr[i].split("\\|");
            			boolean isPropertyArray = false;
            			if (property[1].endsWith("[]")) {
            				property[1] = property[1].substring(0, property[1].length() - 2);
            				isPropertyArray = true;
            			}
            			properties.put(property[0], convert(property[2].trim(), Enum.valueOf(BioType.class, property[1]), isPropertyArray));
            		}
            		return properties;
            	}
            case String:
                return value;
            case Time:
				try {
					return BioTime.parse(value, BioTime.DATETIME_FORMAT);
				} catch (Throwable e) {
					return Long.parseLong(value);
				}
            case UtfString:
                return value;
        }
        return null;
    }

    public static String convertPropertiesToString(BioObject object) {
        final StringBuilder s = new StringBuilder();

        if (object.size() == 0) {
            return s.toString();
        }
        
        object.stream().forEach(e -> {
        	 s.append(";").append(e.getKey()).append("|");
             s.append(getType(e.getValue()));
             if (e.getValue().getClass().isArray()) {
                 s.append("[]|");
                 String arrayStr = Arrays.toString((Object[]) e.getValue()).substring(1);
                 arrayStr = arrayStr.substring(0, arrayStr.length() - 1);
                 s.append(arrayStr);
             } else {
                 s.append("|").append(e.getValue().toString());
             }
        });

        return s.toString().substring(1);
    }

    public static Object convertAsArray(String value, BioType type) {
        String[] strValues = value.split(",");
        switch (type) {
            case Byte:
                Byte[] byteValues = new Byte[strValues.length];
                for (int i = 0; i < byteValues.length; i++) {
                    byteValues[i] = (Byte) convert(strValues[i], type);
                }
                return byteValues;
            case Short:
            	Short[] shortValues = new Short[strValues.length];
                for (int i = 0; i < shortValues.length; i++) {
                	shortValues[i] = (Short) convert(strValues[i], type);
                }
                return shortValues;
            case Boolean:
                Boolean[] booleanValues = new Boolean[strValues.length];
                for (int i = 0; i < booleanValues.length; i++) {
                    booleanValues[i] = (Boolean) convert(strValues[i], type);
                }
                return booleanValues;
            case Double:
                Double[] doubleValues = new Double[strValues.length];
                for (int i = 0; i < doubleValues.length; i++) {
                    doubleValues[i] = (Double) convert(strValues[i], type);
                }
                return doubleValues;
            case Float:
            	Float[] floatValues = new Float[strValues.length];
                for (int i = 0; i < floatValues.length; i++) {
                	floatValues[i] = (Float) convert(strValues[i], type);
                }
                return floatValues;
            case Integer:
                Integer[] intValues = new Integer[strValues.length];
                for (int i = 0; i < intValues.length; i++) {
                    intValues[i] = (Integer) convert(strValues[i], type);
                }
                return intValues;
            case JavaObject:
                Object[] objectValues = new Object[strValues.length];
                for (int i = 0; i < objectValues.length; i++) {
                    objectValues[i] = convert(strValues[i], type);
                }
                return objectValues;
            case Long:
                Long[] longValues = new Long[strValues.length];
                for (int i = 0; i < longValues.length; i++) {
                    longValues[i] = (Long) convert(strValues[i], type);
                }
                return longValues;
            case BioObject:
            case Properties:
                String[] propStrValues = value.split(",,");
                BioObject[] propValues = new BioObject[propStrValues.length];
                for (int i = 0; i < propStrValues.length; i++) {
                    propValues[i] = (BioObject) convert(propStrValues[i], type, false);
                }
                return propValues;
            case String:
                return strValues;
            case JavaEnum:
                return strValues;
            case BioEnum:
                return strValues;
            case Time:
                Long[] timeValues = new Long[strValues.length];
                for (int i = 0; i < timeValues.length; i++) {
                    timeValues[i] = (Long) convert(strValues[i], type);
                }
                return timeValues;
            case UtfString:
                return strValues;
        }
        return null;
    }

    public static Object convertAsList(String value, BioType type) {
        if (type == BioType.BioObject || type == BioType.Properties) {
            String[] strValues = value.split(",,");
            ArrayList<BioObject> values = new ArrayList<BioObject>();
            for (int i = 0; i < strValues.length; i++) {
                values.add((BioObject) convert(strValues[i], type));
            }
            return values;
        } else {
            String[] strValues = value.split(",");
            ArrayList<Object> values = new ArrayList<Object>();
            for (int i = 0; i < strValues.length; i++) {
                values.add(convert(strValues[i], type));
            }
            return values;
        }
    }
    
    public static String convertAsString(List list) {
    	StringBuilder s = new StringBuilder() ;
    	String sep = "" ;
    	for (int i = 0; i < list.size(); i++) {
			s.append(sep).append(list.get(i)) ;
			sep = "," ;
		}
    	return s.toString() ;
    }

//    public static String toString(Object value) {
//        if (value.getClass().isArray()) {
//            Object[] array = (Object[]) value;
//            StringBuilder builder = new StringBuilder();
//            String sep = "";
//            for (int i = 0; i < array.length; i++) {
//                builder.append(sep).append(array[i].toString());
//                sep = ParameterUtility.SEP;
//            }
//            return builder.toString();
//        } else {
//            return value.toString();
//        }
//    }

    public static void main(String[] args) {
//		BioObject[] value = (BioObject[]) convert("account_type|Integer|0;amount|Long|300;valid_from_str|String|0d;valid_to_str|String|=01/01/2099", BioType.Properties, true) ;
//		System.out.printf("%s %s", value, value.getClass().getName());
//		for (int i = 0; i < value.length; i++) {
//			System.out.println(value[i]);
//		}

//		BioObject params = new BioObject() ;
//		params.put("age", 32) ;
//		params.put("city", "Baku") ;
//		params.put("numbers", new Integer[] {1, 2, 3, 4, 5}) ;
//		params.put("friends", new String[] {"Samir", "Vugar", "Azer"}) ;
//		
//		System.out.println(convertPropertiesToString(params));

        BioObject params = (BioObject) ConversionUtility.convert("city|String|Baku;age|Integer|32;friends|String[]|Samir, Vugar, Azer;numbers|Integer[]|1, 2, 3, 4, 5", BioType.Properties);
        System.out.println(params);

    }

    public static boolean isArray(Object value) {
        return value.getClass().isArray();
    }

    public static BioType getType(Object value) {
        if (value.getClass().isArray()) {
            if (value instanceof String[]) {
            	if (((String[]) value).length > 0) {
            		if (((String[]) value)[0].length() != ((String[]) value)[0].getBytes().length) {
            			return BioType.UtfString ;
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
            	if (((BioObject[]) value).length > 0) {
            		value = ((BioObject[]) value)[0] ;
            		if (((BioObject) value).getBioCode() == 0 && ((BioObject) value).getBioVersion() == 0) {
                		return BioType.Properties;
                	}
            	}
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
            	if (((BioObject) value).getBioCode() == 0 && ((BioObject) value).getBioVersion() == 0) {
            		return BioType.Properties;
            	}
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
            	if (((BioObject) value).getBioCode() == 0 && ((BioObject) value).getBioVersion() == 0) {
            		return BioType.Properties;
            	}
                return BioType.BioObject;
            } else if (value instanceof BioEnum) {
                return BioType.BioEnum;
            } else {
                return BioType.JavaObject;
            }
        }
    }
}
