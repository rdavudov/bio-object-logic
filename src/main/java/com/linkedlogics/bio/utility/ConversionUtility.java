package com.linkedlogics.bio.utility;

import java.util.List;

import org.json.JSONObject;

import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.object.BioEnum;
import com.linkedlogics.bio.object.BioObject;
import com.linkedlogics.bio.time.BioTime;

/**
 * It is used to conver string values to actual types based on BioType
 * @author rajab
 *
 */
public class ConversionUtility {
    /**
     * Convert value to given bio type
     * @param value
     * @param type
     * @param isArray
     * @return
     */
    public static Object convert(String value, BioType type) {
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
            			if (!isPropertyArray) {
            				properties.put(property[0], convert(property[2].trim(), Enum.valueOf(BioType.class, property[1])));
            			} else {
            				properties.put(property[0], convertAsArray(property[2].trim(), Enum.valueOf(BioType.class, property[1])));
            			}
            			
            		}
            		return properties;
            	}
            case String:
                return value;
            case Time:
				try {
					return BioTime.parseString(value);
				} catch (Throwable e) {
					return Long.parseLong(value);
				}
            case UtfString:
                return value;
        }
        return null;
    }

    /**
     * Convert value to given bio type array
     * @param value
     * @param type
     * @return
     */
    public static Object[] convertAsArray(String value, BioType type) {
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
                    propValues[i] = (BioObject) convert(propStrValues[i], type);
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

    /**
     * Returns bio type based on value
     * @param value
     * @return
     */
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
