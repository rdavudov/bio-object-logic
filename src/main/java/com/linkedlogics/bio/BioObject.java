package com.linkedlogics.bio;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.exception.DictionaryException;
import com.linkedlogics.bio.exception.ImmutableException;
import com.linkedlogics.bio.utility.ConversionUtility;
import com.linkedlogics.bio.utility.JSONUtility;
import com.linkedlogics.bio.utility.POJOUtility;
import com.linkedlogics.bio.utility.XMLUtility;

/**
 * This class is mother of all map based java objects. It provides basic functionalities such as cloning, trimming, formatting etc.
 * BioObject has three main information. Information about dictionary it belongs, unique object identification code and non-unique 
 * name to be used in bio expressions. Also there is version which is used when newer dictionary changes fields or field types of
 * bio objects and we need some conversion between old and new version data.
 * @author rdavudov
 *
 */
public class BioObject implements Cloneable, BioObjectHolder {
	/**
	 * Bio Dictionary Id 
	 */
	private int dictionary;
	/**
	 * Unique (within same dictionary) object code
	 */
	private int code;
	/**
	 * Object name
	 */
	private String name;
	/**
	 * Object version
	 */
	private int version ; 
	/**
	 * Immutable flag, once is set not puts and removes are permitted 
	 */
	private boolean isImmutable ;
	/**
	 * Actual map which contains all entries
	 */
	private Map<String, Object> map = BioDictionary.createMapObject() ;
	
	public BioObject(int code, String name, int version, int dictionary, BioObject object) {
		this.code = code;
		this.name = name ;
		this.version = version;
		this.dictionary = dictionary ;
		
		if (object != null) {
			putAll(object);
		}
		
		init() ;
	}
	
	public BioObject(int code, String name, int version, int dictionary) {
		this(code, name, version, dictionary, null) ;
	}

	public BioObject(int code, String name, int version) {
		this(code, name, version, 0) ;
	}

	public BioObject(int code, String name) {
		this(code, name, 0, 0) ;
	}
	
	public BioObject(int code, String name, BioObject object) {
		this(code, name, object.getBioVersion(), object.getBioDictionary(), object) ;
	}
	
	public BioObject(int code) {
		this(code, null, 0, 0) ;
	}
	
	public BioObject() {
		this(null) ;
	}
	
	public BioObject(BioObject object) {
		BioObj obj = BioDictionary.findObj(this.getClass()) ;
		if (obj != null) {
			this.code = obj.getCode();
			this.name = obj.getName() ;
			this.version = obj.getVersion();
			this.dictionary = obj.getDictionary() ;
		}
		if (object != null) {
			putAll(object);
		}
		init() ;
	}
	
	public int getBioDictionary() {
		return dictionary;
	}

	public void setBioDictionary(int dictionary) {
		this.dictionary = dictionary;
	}

	public int getBioCode() {
		return code;
	}
	
	public void setBioCode(int code) {
		this.code = code;
	}
	
	public String getBioName() {
		return name;
	}

	public void setBioName(String name) {
		this.name = name;
	}

	public int getBioVersion() {
		return version;
	}

	public void setBioVersion(int version) {
		this.version = version;
	}

	/**
	 * Checks object immutability
	 * @return
	 */
	public boolean isImmutable() {
		return isImmutable;
	}
	
	/**
	 * Makes object immutable no further puts,removes or clear will not work will throw ImmutabilityException
	 */
	public void setImmutable() {
		this.isImmutable = true;
	}
	
	/**
	 * Checks whether key is present.
	 * @param key
	 * @return
	 */
	public boolean has(String key) {
		return map.containsKey(key);
	}
	
	/**
	 * Validates key and object for being null also non primitive array
	 * @param key
	 * @param value
	 */
	protected boolean validateKeyAndObject(String key, Object value) {
		if (isImmutable()) {
			throw new ImmutableException();
		}
		
		// commented because causing lots of trouble
		if (key == null || value == null) {
			return false ;
		}
		
		if (value != null && value.getClass().isArray() && !(value instanceof Object[])) {
			throw new RuntimeException(key + "'s value can't be array of primitive type");
		}
		
		return true ;
	}
	
	/**
	 * Puts key and object
	 */
	public BioObject put(String key, Object object) {
		if (validateKeyAndObject(key, object)) {
			map.put(key, object);
		}
		return this ;
	}
	
	/**
	 * Sets key and object
	 */
	public BioObject set(String key, Object object) {
		put(key, object) ;
		return this ;
	}
	
	public BioObject setAlias(String aliasKey, String key) {
		put(aliasKey, BioExpression.parse(key)) ;
		return this ;
	}
	
	/**
	 * Puts key and object if key is not present
	 */
	public BioObject putIfAbsent(String key, Object object) {
		if (validateKeyAndObject(key, object)) {
			map.putIfAbsent(key, object);
		}
		return this ;
	}
	
	public Set<Entry<String, Object>> entries() {
		return map.entrySet() ;
	}
	
	public Set<String> keys() {
		return map.keySet() ;
	}
	
	/**
	 * Puts key and object if key is not present. If there is a single object then creates a List and appends new object.
	 * If there is already a list then only appends
	 */
	public BioObject putOrAppend(String key, Object object) {
		if (validateKeyAndObject(key, object)) {
			if (!has(key)) {
				map.put(key, object);
			} else if (get(key) instanceof List) {
				((List) get(key)).add(object) ;
			} else {
				List<Object> list = new ArrayList<Object>() ;
				list.add(get(key)) ;
				list.add(object) ;
				map.put(key, list);
			}
		}
		return this ;
	}
	
	public BioObject putAll(Map<? extends String, ? extends Object> map) {
		if (isImmutable()) {
			throw new ImmutableException();
		}
		if (map != null) {
			this.map.putAll(map);
		}
		return this ;
	}

	/**
	 * Puts all all bio objects 
	 */
	public BioObject putAll(BioObject... objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null)
				map.putAll(objects[i].getMap());
		}
		return this ;
	}
	
	/**
	 * Puts all if abssent all bio objects 
	 */
	public BioObject putAllIfAbsent(BioObject... objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				for(Entry<String, Object> e : objects[i].entries()) {
					putIfAbsent(e.getKey(), e.getValue()) ;
				}
			}
		}

		return this ;
	}
	
	public List<Object> append(String key, Object object) {
		if (isImmutable()) {
			throw new ImmutableException();
		}
		List<Object> list = (List<Object>) get(key);
		if (list == null) {
			list = new ArrayList<Object>();
			put(key, list);
		}
		list.add(object);
		return list ;
	}
	
	/**
	 * Puts only tagged values into bio object selected from source
	 * @param object
	 * @return
	 */
	public BioObject putAllByTags(BioObject object) {
		BioObj obj = BioDictionary.getDictionary(dictionary).getObjByCode(code);
		if (obj != null) {
			for(Entry<String, Object> e : object.entries()) {
				if (obj.getTag(e.getKey()) != null) {
					set(e.getKey(), e.getValue()) ;
				}
			}
		} else {
			putAll(object) ;
		}
		return this ;
	}
	
	/**
	 * Merges values inside bio object recursively
	 * @param object
	 */
	public BioObject merge(BioObject object) {
		for(Entry<String, Object> e : object.entries()) {
			if (e.getValue() instanceof BioObject && has(e.getKey()) && get(e.getKey()) instanceof BioObject) {
				((BioObject) get(e.getKey())).merge((BioObject) e.getValue());
			} else {
				set(e.getKey(), e.getValue()) ;
			}
		}
		
		return this ;
	}
	
	/* Getter methods with castings */
	
	public Object get(String key) {
		Object object = map.get(key) ;
		if (object instanceof BioExpression) {
			return ((BioExpression) object).getValue(this) ;
		}
		return object ;
	}
	
	public Object getOrDefault(String key, Object defaultValue) {
		Object value = get(key) ;
		if (value == null) {
			return defaultValue ;
		}
		return value ;
	}
	
	public Object getObject(String key) {
		return get(key);
	}

	public Object getObject(String key, Object defaultValue) {
		return getOrDefault(key, defaultValue);
	}
	
	public BioObject getBioObject(String key) {
		return (BioObject) get(key);
	}

	public BioObject getBioObject(String key, BioObject defaultValue) {
		BioObject value = (BioObject) get(key) ;
		// if we only return defaultValue and any value is put, still we will not have defaultValue inside bio object
		// therefore, we also need to put default value into bio object
		if (value == null) {
			set(key, defaultValue) ;
			return defaultValue ;
		}
		return value ;
	}
	
	public Byte getByte(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof Byte) {
			return (Byte) object ;
		} else if (object instanceof Number) {
			return ((Number) object).byteValue() ;
		} else {
			return Byte.parseByte(object.toString());
		}
	}
	
	public Byte getByte(String key, byte defaultValue) {
		Byte object = getByte(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	public Short getShort(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof Short) {
			return (Short) object ;
		} else if (object instanceof Number) {
			return ((Number) object).shortValue() ;
		} else {
			return Short.parseShort(object.toString());
		}
	}

	public Short getShort(String key, short defaultValue) {
		Short object = getShort(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	public Integer getInt(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof Integer) {
			return (Integer) object ;
		} else if (object instanceof Number) {
			return ((Number) object).intValue() ;
		} else {
			return Integer.parseInt(object.toString()) ;
		}
	}

	public Integer getInt(String key, int defaultValue) {
		Integer object = getInt(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	public Long getLong(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof Long) {
			return (Long) object ;
		} else if (object instanceof Number) {
			return ((Number) object).longValue() ;
		} else {
			return Long.parseLong(object.toString());
		}
	}

	public Long getLong(String key, long defaultValue) {
		Long object = getLong(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	public Float getFloat(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof Float) {
			return (Float) object ;
		} else if (object instanceof Number) {
			return ((Number) object).floatValue() ;
		} else {
			return Float.parseFloat(object.toString());
		}
	}

	public Float getFloat(String key, float defaultValue) {
		Float object = getFloat(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	public Double getDouble(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof Double) {
			return (Double) object ;
		} else if (object instanceof Number) {
			return ((Number) object).doubleValue() ;
		} else {
			return Double.parseDouble(object.toString());
		}
	}

	public Double getDouble(String key, double defaultValue) {
		Double object = getDouble(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	public Date getDate(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof Long) {
			return new Date((Long) object);
		} else if (object instanceof Number) {
			return new Date(((Number) object).longValue()) ;
		} else {
			return new Date(Long.parseLong(object.toString()));
		}
	}

	public Date getDate(String key, Date defaultValue) {
		Date object = getDate(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	public Boolean getBoolean(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof Boolean) {
			return (Boolean) object ;
		} else {
			return Boolean.parseBoolean(object.toString());
		}
	}

	public Boolean getBoolean(String key, boolean defaultValue) {
		Boolean object = getBoolean(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	public String getString(String key) {
		Object object = get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof String) {
			return (String) object ;
		} else {
			return object.toString();
		}
	}

	public String getString(String key, String defaultValue) {
		String object = getString(key);
		if (object == null) {
			return defaultValue;
		}
		return object ;
	}
	
	/**
	 * Removes key from map
	 */
	public Object remove(Object key) {
		if (isImmutable()) {
			throw new ImmutableException();
		}
		return map.remove(key);
	}
	
	/**
	 * Clears contents of map
	 */
	public void clear() {
		if (isImmutable()) {
			throw new ImmutableException();
		}
		map.clear();
	}
	
	/**
	 * Returns size of map
	 */
	public int size() {
		return map.size() ;
	}

	/**
	 * Checks equality by validating bio dict and code values. Then also checks all entries equality. 
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof BioObject) {
			BioObject bioObject = (BioObject) object ;
			
			if (bioObject.getBioDictionary() != this.dictionary 
					|| bioObject.getBioCode() != this.code) {
				return false ;
			}
			AtomicBoolean isEquals = new AtomicBoolean(true) ;
			
			Iterator<Entry<String, Object>> iterator = entries().iterator() ;
			while (iterator.hasNext()) {
				Entry<String, Object> e = iterator.next() ;
				if (!bioObject.has(e.getKey())) {
					isEquals.set(false);
					break;
				} else if (e.getValue() instanceof Object[] && bioObject.get(e.getKey()) instanceof Object[]) {
					Object[] thisArray = (Object[]) e.getValue() ;
					Object[] objectArray = (Object[]) bioObject.get(e.getKey()) ;
					if (thisArray.length != objectArray.length) {
						isEquals.set(false);
						break;
					} else {
						for (int i = 0; i < thisArray.length; i++) {
							if (!thisArray[i].equals(objectArray[i])) {
								isEquals.set(false);
								break;
							}
						}
					}
				} else if (e.getValue() instanceof List && bioObject.get(e.getKey()) instanceof List) {
					List<Object> thisList = (List<Object>) e.getValue() ;
					List<Object> objectList = (List<Object>) bioObject.get(e.getKey()) ;
					if (thisList.size() != objectList.size()) {
						isEquals.set(false);
						break;
					} else {
						for (int i = 0; i < thisList.size(); i++) {
							if (!thisList.get(i).equals(objectList.get(i))) {
								isEquals.set(false);
								break;
							}
						}
					}
				} else if (!e.getValue().equals(bioObject.get(e.getKey()))) {
					isEquals.set(false);
					break;
				}
			}
			
			return isEquals.get() ;
		}
		return false ;
	}

	/**
	 * Clones bio object by creating totally new instance of it
	 */
	public BioObject clone() {
		try {
			final BioObject clone = this.getClass() == BioObject.class ? new BioObject(0) : (BioObject) this.getClass().getConstructor().newInstance();
			clone.setBioCode(code);
			clone.setBioDictionary(dictionary);
			clone.setBioVersion(version);
			clone.setBioName(name);
			clone.isImmutable = false ;
			
			for(Entry<String, Object> e : entries()) {
				if (e.getValue() instanceof BioObject) {
					clone.put(e.getKey(), ((BioObject) e.getValue()).clone());
				} else if (e.getValue() instanceof List) {
					List list = (List) e.getValue() ;
					List cloneList = new ArrayList() ;
					for (Object object : list) {
						if (object instanceof BioObject) {
							cloneList.add(((BioObject) object).clone()) ;
						} else {
							cloneList.add(object) ;
						}
					}
					clone.put(e.getKey(), cloneList);
				} else if (e.getValue() instanceof Object[]) {
					Object[] array = (Object[]) e.getValue();
					Object[] cloneArray = (Object[]) Array.newInstance(array.getClass().getComponentType(), array.length) ;
					for (int i = 0; i < array.length; i++) {
						cloneArray[i] = array[i] ;
					}
					clone.put(e.getKey(), cloneArray);
				} else {
					clone.put(e.getKey(), e.getValue());
				}
			};
			
			return clone ;
		} catch (NoSuchMethodException e) {
			throw new DictionaryException("unable to clone because class " + this.getClass().getName() + " has no default constructor") ;
		} catch (Throwable e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * After creating bio object with added source bio object values provided at constructor, init() method checks whether they are empty keys which has initial or expression value
	 * and tries to generate values for them and add to the map
	 */
	public BioObject init() {
		if (code != 0) {
			BioObj obj = BioDictionary.getDictionary(dictionary).getObjByCode(code) ;
			if (obj != null) {
				// first let's populate initial values, they can also be used in expression initial values
				for(Entry<String, BioTag> e : obj.getNameMap().entrySet()) {
					// if key doesn't exist then
					if (!has(e.getKey())) {
						BioTag tag = e.getValue() ;
						// if tag has an initial value
						if (tag.getInitial() != null) {
							if (tag.isArray()) {
								String[] initials = e.getValue().getInitial().split(",") ;
								Object[] array = (Object[]) Array.newInstance(tag.getJavaClass(), initials.length) ;
								for (int i = 0; i < initials.length; i++) {
									array[i] = tag.getInitialValue(initials[i]) ;
								}
								put(e.getKey(), array) ;
							} else if (tag.isList()) {
								String[] initials = tag.getInitial().split(",") ;
								List<Object> list = new ArrayList<Object>() ;
								for (int i = 0; i < initials.length; i++) {
									list.add(tag.getInitialValue(initials[i])) ;
								}
								put(e.getKey(), list) ;
							} else {
								Object initialValue = tag.getInitialValue(tag.getInitial()) ;
								put(e.getKey(), initialValue) ;
							}
						}
					}
				};
				
				// lets populate expression based initial values
				for(Entry<String, BioTag> e : obj.getNameMap().entrySet()) {
					if (!has(e.getKey())) {
						BioTag tag = e.getValue() ;
						// if tag has an expression initial value
						if (tag.getExpression() != null) {
							Object value = BioExpression.parse(tag.getExpression()).getValue(this) ;
							if (value != null) {
								set(e.getKey(), value) ;
							}
						}
					}
				};
			}
		}
		
		return this ;
	}

	public BioObject fill(BioObject... params) {
		final ArrayList<String> filledKeys = new ArrayList<String>();
		for(Entry<String, Object> e : entries()) {
			if (e.getValue() instanceof BioExpression) {
				filledKeys.add(e.getKey()) ;
			} else if (e.getValue() instanceof BioObject) {
				((BioObject) e.getValue()).fill(params) ;
			} else if (e.getValue() instanceof BioObject[]) {
				BioObject[] array = (BioObject[]) e.getValue();
				for (int i = 0; i < array.length; i++) {
					array[i].fill(params);
				}
			} else if (e.getValue() instanceof List) {
				List list = (List) e.getValue() ;
				for (Object object : list) {
					if (object instanceof BioObject) {
						((BioObject) object).fill(params) ;
					}
				}
			}
		};
		
		for (int i = 0; i < filledKeys.size(); i++) {
			String k = filledKeys.get(i) ;
			BioExpression expression = (BioExpression) get(k) ;
			Object value = expression.getValue(params) ;
			if (value != null) {
				// we got the actual value and lets set it
				set(k, value) ;
			} else {
				// since we couldn't evaluate expression and got NULL so we remove that key because we can't put NULL values into bio
				remove(k) ;
			}
		};
		
		return this ;
	}
	
	public BioObject format() {
		if (code != 0) {
			BioObj obj = BioDictionary.getDictionary(dictionary).getObjByCode(code);
			if (obj != null) {
				final ArrayList<String> formattedKeys = new ArrayList<String>();
				for (Entry<String, Object> e : entries()) {
					BioTag tag = obj.getTag(e.getKey());
					if (tag != null) {
						formattedKeys.add(e.getKey());
					}
				}

				for (int i = 0; i < formattedKeys.size(); i++) {
					String k = formattedKeys.get(i) ;
					BioTag tag = obj.getTag(k) ;
					Object value = get(k) ;

					if (value instanceof BioExpression) {
						continue ;
					} else if (tag.isArray()) {
						if (tag.getType() != BioType.BioEnum) {
							value = ConversionUtility.convertAsArray(tag.getType(), value) ;
						} else {
							value = ConversionUtility.convertAsArray(tag.getEnumObj(), value) ;
						}
					} else if (tag.isList()) {
						if (tag.getType() != BioType.BioEnum) {
							value = ConversionUtility.convertAsList(tag.getType(), value) ;
						} else {
							value = ConversionUtility.convertAsList(tag.getEnumObj(), value) ;
						}
					} else if (value instanceof BioObject) {
						((BioObject) value).format(); 
						continue ;
					} else if (tag.getType() != ConversionUtility.getType(value)) {
						if (tag.getType() != BioType.BioEnum) {
							value = ConversionUtility.convert(tag.getType(), value) ;
						} else {
							value = ConversionUtility.convert(tag.getEnumObj(), value) ;
						}
					}

					if (value != null) {
						put(tag.getName(), value) ;
					} else {
						remove(tag.getName()) ;
					}
				}
			}
		}
		return this ;
	}
	
	/**
	 * Trims keys which are not found in the dictionary on other first level, does not go deeper
	 */
	public BioObject trim() {
		BioObject trimmed = new BioObject(0) ;
		BioObj obj = BioDictionary.getDictionary(dictionary).getObjByCode(code);
		if (obj != null) {
			final ArrayList<String> trimmedKeys = new ArrayList<String>();
			
			for (Entry<String, Object> e : entries()) {
				BioTag tag = obj.getTag(e.getKey());
				if (tag == null) {
					trimmedKeys.add(e.getKey());
				}
			}
			
			for (int i = 0; i < trimmedKeys.size(); i++) {
				String k = trimmedKeys.get(i) ;
				trimmed.put(k, remove(k)) ;
			}
		}
		
		return this ;
	}
	
	/**
	 * Trims keys which are not found in the dictionary and recursively goes deeper
	 */
	public BioObject trimAll() {
		BioObject trimmed = new BioObject(0) ;
		BioObj obj = BioDictionary.getDictionary(dictionary).getObjByCode(code);
		if (obj != null) {
			final ArrayList<String> trimmedKeys = new ArrayList<String>();
			for(Entry<String, Object> e : entries()) {
				BioTag tag = obj.getTag(e.getKey());
				if (tag == null) {
					trimmedKeys.add(e.getKey());
				} else {
					if (e.getValue() instanceof BioObject) {
						((BioObject) e.getValue()).trimAll() ;
					} else if (e.getValue() instanceof BioObject[]) {
						BioObject[] array = (BioObject[]) e.getValue();
						for (int i = 0; i < array.length; i++) {
							array[i].trimAll();
						}
					} else if (e.getValue() instanceof List) {
						List list = (List)  e.getValue();
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) instanceof BioObject) {
								((BioObject) list.get(i)).trimAll() ;
							}
						}
					}
				}
			}
			
			for (int i = 0; i < trimmedKeys.size(); i++) {
				String k = trimmedKeys.get(i) ;
				trimmed.put(k, remove(k)) ;
			}
		}
		
		return this ;
	}
	
	/**
	 * Trims keys which are defined in the dictionary by the trim key, works recursively
	 * @param trimKey
	 */
	public BioObject trim(String trimKey) {
		BioObject trimmed = new BioObject(0) ;
		BioObj obj = BioDictionary.getDictionary(dictionary).getObjByCode(code);
		if (obj != null) {
			final ArrayList<String> trimmedKeys = new ArrayList<String>();
			for(Entry<String, Object> e : entries()) {
				BioTag tag = obj.getTag(e.getKey());
				if (tag != null && tag.isTrimKey(trimKey)) {
					trimmedKeys.add(e.getKey());
				} else {
					if (e.getValue() instanceof BioObject) {
						((BioObject) e.getValue()).trim(trimKey) ;
					} else if (e.getValue() instanceof BioObject[]) {
						BioObject[] array = (BioObject[]) e.getValue();
						for (int i = 0; i < array.length; i++) {
							array[i].trim(trimKey) ;
						}
					} else if (e.getValue() instanceof List) {
						List list = (List)  e.getValue();
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) instanceof BioObject) {
								((BioObject) list.get(i)).trim(trimKey) ;
							}
						}
					}
				}
			};
			
			for (int i = 0; i < trimmedKeys.size(); i++) {
				String k = trimmedKeys.get(i) ;
				trimmed.put(k, remove(k)) ;
			}
		}
		
		return trimmed ;
	}
	
	/**
	 * Trims inverted keys which are defined in the dictionary by the trim key, works recursively
	 * @param inverseTrimKey
	 */
	public BioObject inverseTrim(String inverseTrimKey) {
		BioObject trimmed = new BioObject(0) ;
		BioObj obj = BioDictionary.getDictionary(dictionary).getObjByCode(code);
		if (obj != null) {
			final ArrayList<String> trimmedKeys = new ArrayList<String>();
			for(Entry<String, Object> e : entries()) {
				BioTag tag = obj.getTag(e.getKey());
				if (tag != null && tag.isInverseTrimKey(inverseTrimKey)) {
					trimmedKeys.add(e.getKey());
				} else {
					if (e.getValue() instanceof BioObject) {
						((BioObject) e.getValue()).inverseTrim(inverseTrimKey) ;
					} else if (e.getValue() instanceof BioObject[]) {
						BioObject[] array = (BioObject[]) e.getValue();
						for (int i = 0; i < array.length; i++) {
							array[i].inverseTrim(inverseTrimKey) ;
						}
					} else if (e.getValue() instanceof List) {
						List list = (List)  e.getValue();
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) instanceof BioObject) {
								((BioObject) list.get(i)).inverseTrim(inverseTrimKey) ;
							}
						}
					}
				}
			};
			
			for (int i = 0; i < trimmedKeys.size(); i++) {
				String k = trimmedKeys.get(i) ;
				trimmed.put(k, remove(k)) ;
			}
		}
		
		return trimmed ;
	}
	
	public BioObject empty() {
		clear();
		return this ;
	}
	
	protected Map<String, Object> getMap() {
		return map ;
	}
	
	public String toString() {
		return toXml() ;
	}
	
	@Override
	public BioObject getBioObject() {
		return this ;
	}
	
	/**
	 * Exports bio object to json
	 * @return
	 */
	public String toJson() {
		return JSONUtility.toJson(this).toString(4) ;
	}
	/**
	 * Exports bio object to xml
	 * @return
	 */
	public String toXml() {
		return XMLUtility.toXml(this) ;
	}
	
	/**
	 * Exports bio object to pojo
	 * @return
	 */
	public Object toPojo() {
		return POJOUtility.toPojo(this) ;
	}
	
	/**
	 * Parses json to bio object
	 * @param json
	 * @return
	 */
	public static BioObject fromJson(JSONObject json) {
		return JSONUtility.fromJson(json);
	}
	/**
	 * Creates bio object using map entries
	 * @param map
	 * @return
	 */
	public static BioObject fromMap(Map<String, Object> map) {
		BioObject object = new BioObject(0) ;
		object.putAll(map);
		return object ;
	}
	/**
	 * Parses xml to bio object
	 * @param xml
	 * @return
	 */
	public static BioObject fromXml(String xml) {
		return XMLUtility.fromXml(xml);
	}
	
	public static BioObject fromPojo(Object pojo) {
		return POJOUtility.fromPojo(pojo) ;
	}
}
