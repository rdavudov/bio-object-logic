package com.linkedlogics.bio;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.json.JSONObject;

import com.linkedlogics.bio.exception.ImmutableException;
import com.linkedlogics.bio.object.BioObjectHolder;
import com.linkedlogics.bio.utility.JSONUtility;
import com.linkedlogics.bio.utility.XMLUtility;

/**
 * This class is mother of all map based java objects. It provides basic functionalities such as cloning, trimming, formatting etc.
 * BioObject has three main information. Information about dictionary it belongs, unique object identification code and non-unique 
 * name to be used in bio expressions. Also there is version which is used when newer dictionary changes fields or field types of
 * bio objects and we need some conversion between old and new version data.
 * @author rdavudov
 *
 */
public class BioObject implements BioObjectHolder {
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
	
	public BioObject(BioObject object) {
		this(object.getBioCode(), object.getBioName(), object.getBioVersion(), object.getBioDictionary(), object) ;
	}
	
	public BioObject(int code) {
		this(code, null, 0, 0) ;
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
	protected void validateKeyAndObject(String key, Object value) {
		if (isImmutable()) {
			throw new ImmutableException();
		}
		
		if (key == null) {
			throw new RuntimeException("key can't be null");
		} else if (value == null) {
			throw new RuntimeException("value can't be null");
		}
		
		if (value != null && value.getClass().isArray() && !(value instanceof Object[])) {
			throw new RuntimeException(key + "'s value can't be array of primitive type");
		}
	}
	
	/**
	 * Puts key and object
	 */
	public BioObject put(String key, Object object) {
		validateKeyAndObject(key, object) ;
		map.put(key, object);
		return this ;
	}
	
	/**
	 * Sets key and object
	 */
	public BioObject set(String key, Object object) {
		validateKeyAndObject(key, object) ;
		map.put(key, object);
		return this ;
	}
	
	/**
	 * Puts key and object if key is not present
	 */
	public BioObject putIfAbsent(String key, Object object) {
		validateKeyAndObject(key, object) ;
		map.putIfAbsent(key, object);
		return this ;
	}
	
	public Stream<Entry<String, Object>> stream() {
		return map.entrySet().stream() ;
	}
	
	/**
	 * Puts key and object if key is not present. If there is a single object then creates a List and appends new object.
	 * If there is already a list then only appends
	 */
	public BioObject putOrAppend(String key, Object object) {
		validateKeyAndObject(key, object) ;
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
			if (objects[i] != null)
				objects[i].stream().forEach(e -> {
					putIfAbsent(e.getKey(), e.getValue()) ;
				});
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
	
	public BioObject putAllByTags(BioObject object) {
		//TODO: use dictionary and check tags
		return this ;
	}
	
	public void merge() {
		
	}
	
	public void mergeDynamic() {
		
	}
	
	public Object get(String key) {
		return map.get(key) ;
	}
	
	public Object getOrDefault(String key, Object defaultValue) {
		Object value = get(key) ;
		if (value == null) {
			return defaultValue ;
		}
		return value ;
	}
	
	//*****//
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
		return (BioObject) getOrDefault(key, defaultValue);
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
		return (String) get(key);
	}

	public String getString(String key, String defaultValue) {
		return (String) getOrDefault(key, defaultValue) ;
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
			
			Iterator<Entry<String, Object>> iterator = stream().iterator() ;
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
	
	public void init() {
		
	}
	
	public void fill() {
		
	}
	
	public void format() {
		
	}
	
	public void trim() {
		
	}
	
	public void trim(String trimKey) {
		
	}
	
	public void inverseTrim() {
		
	}
	
	public void inverseTrim(String trimKey) {
		
	}
	
	protected Map<String, Object> getMap() {
		return map ;
	}
	
	@Override
	public BioObject getBioObject() {
		return this ;
	}

	public BioObject clone() {
		return null ;
	}
	
	public String toString() {
		return toXml() ;
	}
	
	public JSONObject toJson() {
		return JSONUtility.toJson(this) ;
	}
	
	public String toXml() {
		return XMLUtility.toXml(this) ;
	}
	
	public static BioObject fromJson(JSONObject json) {
		return JSONUtility.fromJson(json);
	}
	
	public static BioObject fromMap(Map<String, Object> map) {
		BioObject object = new BioObject(0) ;
		object.putAll(map);
		return object ;
	}
	
	public static BioObject fromXml(String xml) {
		return XMLUtility.fromXml(xml);
	}
}
