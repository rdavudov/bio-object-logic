package com.linkedlogics.bio.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.BioTime;
import com.linkedlogics.bio.dictionary.BioEnumObj;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.expression.Conditional;
import com.linkedlogics.bio.parser.BioObjectXmlParser;

/**
 * Class for all xml related operations
 * @author rajab
 *
 */
public class XMLUtility {
	/**
	 * Exports bio object to xml
	 * @param object
	 * @return
	 */
	public static String toXml(BioObject object) {
		StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n") ;
		toXml(xml, "", object.getBioName(), object) ;
		return xml.toString() ;
	}
	
	/**
	 * Exportys bio object to xml
	 * @param xml
	 * @param tab
	 * @param tag
	 * @param object
	 */
	private static void toXml(StringBuilder xml, String tab, String tag, BioObject object) {
		if (tag == null) {
			tag = "bio";
		}
		
		ArrayList<String> tags = new ArrayList<String>(object.keys());
		Collections.sort(tags);

		
		BioDictionary dictionary = BioDictionary.getDictionary(object.getBioDictionary());
		if (dictionary.getObjByCode(object.getBioCode()) != null) {
			BioObj obj = dictionary.getObjByCode(object.getBioCode());
			xml.append(tab).append("<")
			.append(tag.replaceAll("_", "-"))
			.append(" type=\"").append(obj.getType())
			.append("\" code=\"").append(object.getBioCode()) ;
			if (object.getBioVersion() != 0) {
				xml.append("\" version=\"").append(object.getBioVersion()) ;
			}
			if (object.getBioDictionary() != 0) {
				xml.append("\" dictionary=\"").append(object.getBioDictionary()) ;
			}
			xml.append("\">\n");
			
			for (int i = 0; i < tags.size(); i++) {
				String key = tags.get(i) ;
				// keys starting with _ will not be exported
				if (!key.startsWith("_")) {
					Object value = object.get(key);
					
					BioTag bioTag = obj.getTag(key) ;
					if (bioTag == null) {
						toXml(xml, tab, key.replaceAll("_", "-"), value, ConversionUtility.getType(value), null);
					} else if (bioTag.isExportable()) {
						toXml(xml, tab, key.replaceAll("_", "-"), value, bioTag.getType(), bioTag);
					}
				}
			}
		} else {
			xml.append(tab).append("<")
			.append(tag.replaceAll("_", "-"))
			.append(" type=\"BioObject")
			.append("\" code=\"").append(object.getBioCode())
			.append("\" version=\"").append(object.getBioVersion())
			.append("\">\n");
			for (int i = 0; i < tags.size(); i++) {
				String key = tags.get(i) ;
				if (!key.startsWith("_")) {
					Object value = object.get(key);
					toXml(xml, tab, key.replaceAll("_", "-"), value, ConversionUtility.getType(value), null);
				}
			}
		}
		
		xml.append(tab).append("</").append(tag.replaceAll("_", "-")).append(">\n");
	}
	
	/**
	 * Exports a tag to xml
	 * @param xml
	 * @param tab
	 * @param key
	 * @param value
	 * @param type
	 * @param tag
	 */
	public static void toXml(StringBuilder xml, String tab, String key, Object value, BioType type, BioTag tag) {
		if (value instanceof BioObject[]) {
			xml.append(tab).append(TAB).append("<").append(key)
			.append(" type=\"").append(type)
			.append("\" is-array=\"true\">\n");
			BioObject[] array = (BioObject[]) value;
			for (int i = 0; i < array.length; i++) {
				toXml(xml, tab + TAB + TAB, array[i].getBioName(), array[i]);
			}
			xml.append(tab).append(TAB).append("</").append(key).append(">\n");
		} else if (value instanceof BioEnum[] && tag.getEnumObj() != null) {
			BioEnum[] array = (BioEnum[]) value;
			xml.append(tab).append(TAB).append("<").append(key)
			.append(" type=\"").append(tag.getEnumObj().getType())
			.append("\" is-array=\"true\">")
			.append(StringUtility.join(array))
			.append("</").append(key).append(">\n");
		} else if (value instanceof Object[]) {
			Object[] array = (Object[]) value;
			xml.append(tab).append(TAB).append("<").append(key)
			.append(" type=\"").append(type)
			.append("\" is-array=\"true\">")
			.append(StringUtility.join(array))
			.append("</").append(key).append(">\n");
		} else if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			if (tag != null) {
				// If list bio type is another bio object then we can't join them with commas
				// it will same as array but only xml attribute will is-list="true"
				if (tag.getObj() != null) {
					xml.append(tab).append(TAB).append("<").append(key)
					.append(" type=\"").append(tag.getObj().getName())
					.append("\" is-list=\"true\">\n");
					for (int i = 0; i < list.size(); i++) {
						toXml(xml, tab + TAB + TAB, ((BioObject)list.get(i)).getBioName(), (BioObject) list.get(i));
					}
					xml.append(tab).append(TAB).append("</").append(key).append(">\n");
					return ;
				} else if (tag.getEnumObj() != null) {
					xml.append(tab).append(TAB).append("<").append(key)
					.append(" type=\"").append(tag.getEnumObj().getType()) ;
				} else {
					xml.append(tab).append(TAB).append("<").append(key)
					.append(" type=\"").append(tag.getType()) ;
				}
			} else {
				xml.append(tab).append(TAB).append("<").append(key)
				.append(" type=\"").append(type) ;
			}
			
			xml.append("\" is-list=\"true\">")
			.append(StringUtility.join(list))
			.append("</").append(key).append(">\n");
		} else if (value instanceof BioObject) {
			toXml(xml, tab + TAB, key, ((BioObject) value));
		} else if (value instanceof Conditional) {
			Conditional conditional = (Conditional) value;
			xml.append(tab).append(TAB).append("<").append(key).append(" type=\"").append(BioType.Conditional).append("\">\n");
			xml.append(tab).append(TAB).append(TAB).append("<condition><![CDATA[").append(conditional.getCondition()).append("]]></condition>\n");
			if (conditional.getValue() != null) {
				toXml(xml, tab + TAB, "value", conditional.getValue(), ConversionUtility.getType(conditional.getValue()), null);
			}
			if (conditional.getElseValue() != null) {
				toXml(xml, tab + TAB, "else-value", conditional.getElseValue(), ConversionUtility.getType(conditional.getElseValue()), null);
			}
			xml.append(tab).append(TAB).append("</").append(key).append(">\n");
		} else if (value instanceof BioExpression) {
			xml.append(tab).append(TAB).append("<").append(key)
			.append(" type=\"").append(BioType.Dynamic).append("\">")
			.append(value).append("</")
			.append(key).append(">\n");
		} else if (type == BioType.String || type == BioType.UtfString) {
			int bytes = value.toString().getBytes().length;
			if (xmlForbiddenChars.matcher(value.toString()).find() || bytes > value.toString().length() || type == BioType.UtfString) {
				xml.append(tab).append(TAB).append("<").append(key)
				.append(" type=\"").append(type)
				.append("\"><![CDATA[").append(value).append("]]></")
				.append(key).append(">\n");
			} else {
				xml.append(tab).append(TAB).append("<").append(key)
				.append(" type=\"").append(type).append("\">")
				.append(value).append("</")
				.append(key).append(">\n");
			}
		} else if (type == BioType.Time && value instanceof Long) {
			xml.append(tab).append(TAB).append("<").append(key)
			.append(" type=\"").append(type).append("\">")
			.append(BioTime.format((Long) value, BioTime.DATETIME_FORMAT))
			.append("</").append(key).append(">\n");
		} else if (type == BioType.BioEnum || value instanceof BioEnum) {
			BioEnum bioEnum = (BioEnum) value;
			String enumObjName = value.getClass().getSimpleName();
			BioEnumObj bioEnumObj = BioDictionary.getDictionary(bioEnum.getBioDictionary()).getBioEnumObj(enumObjName);
			if (bioEnumObj == null) {
				bioEnumObj = BioDictionary.getDictionary(bioEnum.getBioDictionary()).getBioEnumObj(bioEnum.getBioCode());
			}
			if (bioEnumObj != null) {
				xml.append(tab).append(TAB).append("<").append(key)
				.append(" type=\"").append(bioEnumObj.getType()).append("\">")
				.append(value).append("</")
				.append(key).append(">\n");
			} else {
				xml.append(tab).append(TAB).append("<").append(key)
				.append(" type=\"BioEnum\">")
				.append(value).append("</")
				.append(key).append(">\n");
			}
		} else {
			xml.append(tab).append(TAB).append("<").append(key)
			.append(" type=\"").append(type).append("\">")
			.append(value).append("</")
			.append(key).append(">\n");
		}
	}
	
	/**
	 * Parses bio object from xml
	 * @param xml
	 * @return
	 */
	public static BioObject fromXml(String xml) {
		return new BioObjectXmlParser().parse(xml) ;
	}
	
	public static final String TAB = "    ";
	public static final String SEP = "," ;
	public static final Pattern xmlForbiddenChars = Pattern.compile("[<>&\"']");
}
