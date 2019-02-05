package com.linkedlogics.bio.utility;

import java.lang.reflect.Array;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.linkedlogics.bio.object.BioObject;

public class JSONUtility {
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
	
	public static String toJson(BioObject object) {
		return null ;
	}
}
