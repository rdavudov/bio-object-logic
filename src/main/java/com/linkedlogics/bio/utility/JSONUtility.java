package com.linkedlogics.bio.utility;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.BioTime;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;

/**
 * Class for all JSON related operations
 * @author rajab
 *
 */
public class JSONUtility {
	/**
	 * Parses a JSON to bio object
	 * @param object
	 * @return
	 */
	public static BioObject fromJson(JSONObject object) {
		BioObject bio = new BioObject(0);
		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = object.get(key);
			if (value instanceof JSONObject) {
				bio.put(key, fromJson((JSONObject) value));
			} else if (value instanceof JSONArray) {
				bio.put(key, fromJson((JSONArray) value));
			} else {
				bio.put(key, value);
			}
		}
		return bio;
	}
	
	/**
	 * Parses a JSON array to bio object array
	 * @param array
	 * @return
	 */
	public static Object[] fromJson(JSONArray array) {
		Object[] objects = new Object[array.length()];
		for (int i = 0; i < array.length(); i++) {
			if (array.get(i) instanceof JSONObject) {
				objects[i] = fromJson((JSONObject) array.get(i));
			} else if (array.get(i) instanceof JSONArray) {
				objects[i] = fromJson((JSONArray) array.get(i));
			} else {
				objects[i] = array.get(i);
			}
		}

		Object[] bioArray = null;
		if (objects.length > 0) {
			bioArray = (Object[]) Array.newInstance(objects[0].getClass(), objects.length);
			for (int i = 0; i < bioArray.length; i++) {
				bioArray[i] = objects[i];
			}
		}

		return bioArray;
	}
	
	/**
	 * Exports bio object to json
	 * @param object
	 * @return
	 */
	public static JSONObject toJson(BioObject object) {
		final JSONObject json = new JSONObject();
		final BioObj obj = BioDictionary.getDictionary(object.getBioDictionary()).getObjByCode(object.getBioCode());
		
		for(Entry<String, Object> e : object.entries()) {
			if (!e.getKey().startsWith("_")) {
				if (obj != null) {
					BioTag tag = obj.getTag(e.getKey());
					if (tag != null && !tag.isExportable()) {
						continue ;
					}
				}

				if (e.getValue() instanceof BioObject[]) {
					JSONArray jsonArray = new JSONArray();
					BioObject[] array = (BioObject[]) e.getValue();
					for (int i = 0; i < array.length; i++) {
						jsonArray.put(array[i].toJson());
					}
					json.put(e.getKey(), jsonArray);
				} else if (e.getValue() instanceof BioEnum[]) {
					JSONArray jsonArray = new JSONArray();
					BioEnum[] array = (BioEnum[]) e.getValue();
					for (int i = 0; i < array.length; i++) {
						jsonArray.put(array[i].getName());
					}
					json.put(e.getKey(), jsonArray);
				} else if (e.getValue() instanceof Object[]) {
					json.put(e.getKey(), new JSONArray(e.getValue()));
				} else if (e.getValue() instanceof List) {
					JSONArray jsonArray = new JSONArray();
					List list = (List) e.getValue();
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i) instanceof BioObject) {
							jsonArray.put(((BioObject) list.get(i)).toJson());
						} else if (list.get(i) instanceof BioEnum) {
							jsonArray.put(((BioEnum) list.get(i)).getName());
						} else {
							jsonArray.put(list.get(i));
						}
					}
					json.put(e.getKey(), jsonArray);
				} else if (e.getValue() instanceof BioObject) {
					json.put(e.getKey(), ((BioObject) e.getValue()).toJson());
				} else if (e.getValue() instanceof BioEnum) {
					json.put(e.getKey(), ((BioEnum) e.getValue()).getName());
				} else if (e.getValue() instanceof BioExpression) {

				} else if (obj != null && obj.getTag(e.getKey()) != null && obj.getTag(e.getKey()).getType() == BioType.Time) {
					json.put(e.getKey(), BioTime.format((Long) e.getValue(), BioTime.DATETIME_FORMAT));
				} else {
					json.put(e.getKey(), e.getValue());
				}
			}
		}
		
		return json ;
	}
}
