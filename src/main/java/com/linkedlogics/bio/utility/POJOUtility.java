package com.linkedlogics.bio.utility;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.annotation.BioPojoIgnore;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.exception.DictionaryException;

/**
 * is a utility class from converting POJOs to bio and BIOs to pojo
 * @author rajab
 *
 */
public class POJOUtility {
	/**
	 * Converts pojo to bio object
	 * @param pojo
	 * @return
	 */
	public static BioObject fromPojo(Object pojo) {
		int code = getCode(pojo.getClass().getName()) ;
		
		BioObj obj = BioDictionary.getDictionary().getObjByCode(code) ;
		if (obj == null) {
			obj = createObj(pojo.getClass()) ;
			BioDictionary.getDictionary().addObj(obj);
		}
		if (obj != null) {
			BioObject object = new BioObject(obj.getCode(), obj.getName()) ;
			for (Entry<String, BioTag> t : obj.getNameMap().entrySet()) {
				Object value = getValue(t.getValue(), pojo) ;
				
				if (value != null) {
					if (t.getValue().getType() == BioType.BioObject) {
						if (t.getValue().isList()) {
							List<BioObject> target = new ArrayList<BioObject>() ;
							List<Object> source = (List<Object>) value ;
							for (int i = 0; i < source.size(); i++) {
								obj = createObj(source.get(i).getClass()) ;
								BioDictionary.getDictionary().addObj(obj);
								t.getValue().setObj(obj);
								target.add(fromPojo(source.get(i))) ;
							}
							object.set(t.getKey(), target) ;
						} else {
							obj = createObj(value.getClass()) ;
							BioDictionary.getDictionary().addObj(obj);
							t.getValue().setObj(obj);
							object.set(t.getKey(), fromPojo(value)) ;
						}
					} else if (t.getValue().getType() == BioType.BioEnum) {
						if (value instanceof Object[]) {
							Object[] array = (Object[]) value ;
							BioEnum[] enumArray = new BioEnum[array.length] ;
							for (int i = 0; i < enumArray.length; i++) {
								enumArray[i] = t.getValue().getEnumObj().getBioEnum(array[i].toString()) ;
							}
							object.set(t.getKey(), enumArray) ;
						} else {
							object.set(t.getKey(), t.getValue().getEnumObj().getBioEnum(value.toString())) ;
						}
					} else {
						if (value.getClass().isArray() && !(value instanceof Object[])) {
							object.set(t.getKey(), getBoxedArray(value)) ;
						} else {
							if (value instanceof Date) {
								object.set(t.getKey(), ((Date) value).getTime()) ;
							} else {
								object.set(t.getKey(), value) ;
							}
						}
					}
				}
			}

			return object ;
		}
		
		return null ;
	}
	
	public static BioObj createObj(Class objectClass) {
		int code = POJOUtility.getCode(objectClass.getName()) ;

		String name = objectClass.getSimpleName().replaceAll("([^_A-Z])([A-Z])", "$1_$2").toLowerCase() ;

		BioObj obj = new BioObj(0, code, objectClass.getSimpleName(), name, 0);
		obj.setBioClass(objectClass);
		

		while (objectClass != null) {
			Field[] fields = objectClass.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				if (!Modifier.isTransient(fields[j].getModifiers()) && !fields[j].isAnnotationPresent(BioPojoIgnore.class)) {
					try {
						BioTag tag = createPojoTag(fields[j]);
						obj.addTag(tag);
					} catch (Throwable e) {
						throw new DictionaryException("error in adding tag for " + objectClass.getName(), e) ;
					}
				}
			}
			
			objectClass = objectClass.getSuperclass();
		}
		
		return obj ;
	}

	public static BioTag createPojoTag(Field field) throws Throwable {
		String name = field.getName().replaceAll("([^_A-Z])([A-Z])", "$1_$2").toLowerCase() ;
		int code = POJOUtility.getCode(name) ;

		String typeStr = field.getType().getName() ;
		if (field.getType().isArray()) {
			typeStr = field.getType().getComponentType().getName() ;
		}

		typeStr = typeStr.substring(0, 1).toUpperCase() + typeStr.substring(1) ;
		typeStr = typeStr.split("\\.")[typeStr.split("\\.").length - 1] ;
		if (typeStr.equals("Int")) {
			typeStr = "Integer" ;
		} 

		BioType type = BioType.BioObject;
		try {
			type = Enum.valueOf(BioType.class, typeStr);
		} catch (Exception e) {
			if (typeStr.equals("Date")) {
				type = BioType.Time ;
			}
		}
		BioTag tag = new BioTag(code, name, type);
		if (tag.getType() == BioType.BioObject) {
			tag.setObjName(typeStr);
		}

		if (List.class.isAssignableFrom(field.getType())) {
			tag.setList(true);
		} else {
			tag.setArray(field.getType().isArray());
		}
		tag.setEncodable(true);
		tag.setExportable(true);

		return tag;
	}
	
	/**
	 * Converts bio object to pojo
	 * @param object
	 * @return
	 */
	public static Object toPojo(BioObject object) {
		BioObj obj = BioDictionary.getDictionary().getObjByCode(object.getBioCode()) ;
		if (obj != null) {
			try {
				Object pojo = obj.getBioClass().getConstructor().newInstance() ;
				for(Entry<String, Object> e : object.entries()) {
					BioTag tag = obj.getTag(e.getKey()) ;
					if (tag != null) {
						if (e.getValue() instanceof BioObject) {
							setValue(tag, pojo, toPojo((BioObject) e.getValue()));
						} else if (e.getValue() instanceof BioEnum) {
							Field[] fields = tag.getEnumObj().getBioClass().getDeclaredFields() ;
							try {
								for (int i = 0; i < fields.length; i++) {
									if (((Enum) fields[i].get(null)).name().equals(e.getValue().toString())) {
										setValue(tag, pojo, fields[i].get(null));
									}
								}
							} catch (IllegalArgumentException ex) {
								
							} catch (IllegalAccessException ex) {
								
							}
							
						} else {
							setValue(tag, pojo, e.getValue());
						}
					}
				}
				return pojo ;
			} catch (Throwable e) {
				throw new DictionaryException(e) ;
			}
		}
		
		return null ;
	}
	
	/**
	 * Returns value from pojo
	 * @param tag
	 * @param object
	 * @return
	 */
	private static Object getValue(BioTag tag, Object object) {
		try {
			String key = getMethod(tag.getName()) ;
			String getter = null ;
			if (tag.getType() == BioType.Boolean && !tag.isArray()) {
				getter = "is" + key.substring(0, 1).toUpperCase() + key.substring(1);
			} else {
				getter = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
			}
			return object.getClass().getMethod(getter).invoke(object) ;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Sets value to pojo
	 * @param tag
	 * @param object
	 * @param value
	 */
	private static void setValue(BioTag tag, Object object, Object value) {
		try {
			String key = getMethod(tag.getName()) ;
			String setter = "set" + key.substring(0, 1).toUpperCase() + key.substring(1) ;
			if (tag.getType() == BioType.BioObject) {
				object.getClass().getMethod(setter, tag.getObj().getBioClass()).invoke(object, value) ;
			} else if (tag.getType() == BioType.BioEnum) {
				if (tag.isArray()) {
					BioEnum[] array = (BioEnum[]) value ;
					Object[] enumArray = (Object[]) Array.newInstance(tag.getEnumObj().getBioClass(), array.length) ;
					Field[] fields = tag.getEnumObj().getBioClass().getDeclaredFields() ;
					for (int i = 0; i < enumArray.length; i++) {
						try {
							for (int j = 0; j < fields.length; j++) {
								if (((Enum) fields[j].get(null)).name().equals(array[i].toString())) {
									enumArray[i] = fields[j].get(null) ;
									break;
								}
							}
						} catch (IllegalArgumentException ex) {
							
						} catch (IllegalAccessException ex) {
							
						}
					}
					object.getClass().getMethod(setter, enumArray.getClass()).invoke(object, new Object[] {enumArray}) ;
				} else {
					object.getClass().getMethod(setter, tag.getEnumObj().getBioClass()).invoke(object, value) ;
				}
			} else if (tag.isArray()) {
				try {
					object.getClass().getMethod(setter, tag.getPrimitiveJavaArrayClass(tag.getType())).invoke(object, getPrimitiveArray(value)) ;
				} catch (NoSuchMethodException e) {
					object.getClass().getMethod(setter, tag.getJavaArrayClass(tag.getType())).invoke(object, value) ;
				}
			} else {
				try {
					object.getClass().getMethod(setter, tag.getPrimitiveJavaClass(tag.getType())).invoke(object, value) ;
				} catch (NoSuchMethodException e) {
					object.getClass().getMethod(setter, value.getClass()).invoke(object, value) ;
				}
			}
		} catch (NoSuchMethodException e) {
			
		} catch (Throwable e) {
			throw new DictionaryException(e) ;
		}
	}
	
	/**
	 * Generates code for obj or tag
	 * @param name
	 * @return
	 */
	public static int getCode(String name) {
		return BioDictionary.getTagHasher().hash(name) ;
	}
	
	/**
	 * Converts snake case to camel case
	 * @param tag
	 * @return
	 */
	private static String getMethod(String tag) {
		StringBuilder s = new StringBuilder(tag);
		for (int i = 0; i < s.length(); i++) {
		    if (s.charAt(i) == '_') {
		        s.deleteCharAt(i);
		        s.replace(i, i+1, String.valueOf(Character.toUpperCase(s.charAt(i))));
		    }
		}
		return s.toString() ;
	}
	
	/**
	 * Converts primitive array to boxed object array
	 * @param primitiveArray
	 * @return
	 */
	private static Object[] getBoxedArray(Object primitiveArray) {
		if (primitiveArray instanceof int[]) {
			int[] array = (int[]) primitiveArray ;
			Integer[] boxed = new Integer[array.length] ;
			for (int i = 0; i < boxed.length; i++) {
				boxed[i] = array[i] ;
			}
			return boxed ;
		} else if (primitiveArray instanceof byte[]) {
			byte[] array = (byte[]) primitiveArray ;
			Byte[] boxed = new Byte[array.length] ;
			for (int i = 0; i < boxed.length; i++) {
				boxed[i] = array[i] ;
			}
			return boxed ;
		} else if (primitiveArray instanceof short[]) {
			short[] array = (short[]) primitiveArray ;
			Short[] boxed = new Short[array.length] ;
			for (int i = 0; i < boxed.length; i++) {
				boxed[i] = array[i] ;
			}
			return boxed ;
		} else if (primitiveArray instanceof boolean[]) {
			boolean[] array = (boolean[]) primitiveArray ;
			Boolean[] boxed = new Boolean[array.length] ;
			for (int i = 0; i < boxed.length; i++) {
				boxed[i] = array[i] ;
			}
			return boxed ;
		} else if (primitiveArray instanceof long[]) {
			long[] array = (long[]) primitiveArray ;
			Long[] boxed = new Long[array.length] ;
			for (int i = 0; i < boxed.length; i++) {
				boxed[i] = array[i] ;
			}
			return boxed ;
		} else if (primitiveArray instanceof float[]) {
			float[] array = (float[]) primitiveArray ;
			Float[] boxed = new Float[array.length] ;
			for (int i = 0; i < boxed.length; i++) {
				boxed[i] = array[i] ;
			}
			return boxed ;
		} else if (primitiveArray instanceof double[]) {
			double[] array = (double[]) primitiveArray ;
			Double[] boxed = new Double[array.length] ;
			for (int i = 0; i < boxed.length; i++) {
				boxed[i] = array[i] ;
			}
			return boxed ;
		}
		return null ;
	}
	
	/**
	 * Converts object array to primitive array
	 * @param boxedArray
	 * @return
	 */
	public static Object getPrimitiveArray(Object boxedArray) {
		if (boxedArray instanceof Integer[]) {
			Integer[] boxed = (Integer[]) boxedArray ;
			int[] primitive = new int[boxed.length] ;
			for (int i = 0; i < boxed.length; i++) {
				primitive[i] = boxed[i] ;
			}
			return primitive ;
		} else if (boxedArray instanceof Byte[]) {
			Byte[] boxed = (Byte[]) boxedArray ;
			byte[] primitive = new byte[boxed.length] ;
			for (int i = 0; i < boxed.length; i++) {
				primitive[i] = boxed[i] ;
			}
			return primitive ;
		} else if (boxedArray instanceof Short[]) {
			Short[] boxed = (Short[]) boxedArray ;
			short[] primitive = new short[boxed.length] ;
			for (int i = 0; i < boxed.length; i++) {
				primitive[i] = boxed[i] ;
			}
			return primitive ;
		} else if (boxedArray instanceof Boolean[]) {
			Boolean[] boxed = (Boolean[]) boxedArray ;
			boolean[] primitive = new boolean[boxed.length] ;
			for (int i = 0; i < boxed.length; i++) {
				primitive[i] = boxed[i] ;
			}
			return primitive ;
		} else if (boxedArray instanceof Long[]) {
			Long[] boxed = (Long[]) boxedArray ;
			long[] primitive = new long[boxed.length] ;
			for (int i = 0; i < boxed.length; i++) {
				primitive[i] = boxed[i] ;
			}
			return primitive ;
		} else if (boxedArray instanceof Float[]) {
			Float[] boxed = (Float[]) boxedArray ;
			float[] primitive = new float[boxed.length] ;
			for (int i = 0; i < boxed.length; i++) {
				primitive[i] = boxed[i] ;
			}
			return primitive ;
		} else if (boxedArray instanceof Double[]) {
			Double[] boxed = (Double[]) boxedArray ;
			double[] primitive = new double[boxed.length] ;
			for (int i = 0; i < boxed.length; i++) {
				primitive[i] = boxed[i] ;
			}
			return primitive ;
		}
		return null ;
	}
}
