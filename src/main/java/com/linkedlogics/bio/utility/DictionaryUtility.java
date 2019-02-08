package com.linkedlogics.bio.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.dictionary.BioEnumObj;
import com.linkedlogics.bio.dictionary.BioFunc;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;

/**
 * Class for exporting whole dictionary to xml
 * @author rajab
 *
 */
public class DictionaryUtility {
	
	/**
	 * Exports dictionary to xml
	 * @param dictionary
	 * @return
	 */
	public static String toXml(BioDictionary dictionary) {
		StringBuilder xml = new StringBuilder() ;
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n") ;
		xml.append("<dictionary code=\"").append(dictionary.getCode()).append("\">\n") ;
		
		dictionary.getCodeMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioObj::getCode)).collect(Collectors.toList()).forEach(o -> {
			objToXml(o, xml);
		}); ;
		
		dictionary.getEnumCodeMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioEnumObj::getCode)).collect(Collectors.toList()).forEach(e -> {
			enumToXml(e, xml);
		}); ;
		
		dictionary.getSuperTagCodeMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioTag::getCode)).collect(Collectors.toList()).forEach(t -> {
			tagToXml(t, xml, "\t<super-tag");
		}); ;

		dictionary.getFuncNameMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioFunc::getName)).collect(Collectors.toList()).forEach(f -> {
			funcToXml(f, xml);
		}); ;
		
		xml.append("</dictionary>") ;
		return xml.toString() ;
	}

	/**
	 * Exports obj definition
	 * @param obj
	 * @param xml
	 */
	private static void objToXml(BioObj obj, StringBuilder xml) {
		xml.append("\t<obj name=\"").append(obj.getType()).append("\" code=\"").append(obj.getCode()).append("\" version=\"").append(obj.getVersion()).append("\"") ;
		if (obj.getBioClass() != null) {
			xml.append(" class=\"").append(obj.getBioClass().getName()).append("\"") ;
		}
		xml.append(">\n") ;
		List<BioTag> tags = new ArrayList<BioTag>() ;
		for (Entry<Integer, BioTag> entry : obj.getCodeMap().entrySet()) {
			tags.add(entry.getValue()) ;
		}
		Collections.sort(tags, new Comparator<BioTag>() {
			public int compare(BioTag o1, BioTag o2) {
				return o1.getCode() - o2.getCode() ;
			}
		});
		for (BioTag tag : tags) {
			tagToXml(tag, xml, "\t\t<tag");
		}
		xml.append("\t</obj>\n") ;
	}
	/**
	 * Exports enum definition
	 * @param enumObj
	 * @param xml
	 */
	private static void enumToXml(BioEnumObj enumObj, StringBuilder xml) {
    	xml.append("\t<enum name=\"").append(enumObj.getName()).append("\" code=\"").append(enumObj.getCode()).append("\" version=\"").append(enumObj.getVersion()).append("\"") ;
		if (enumObj.getBioClass() != null) {
			xml.append(" class=\"").append(enumObj.getBioClass().getName()).append("\"") ;
		}
		xml.append(">\n") ;
		
		List<BioEnum> values = new ArrayList<BioEnum>() ;
		for (Entry<Integer, BioEnum> entry : enumObj.getCodeMap().entrySet()) {
			values.add(entry.getValue()) ;
		}
		Collections.sort(values, new Comparator<BioEnum>() {
			public int compare(BioEnum o1, BioEnum o2) {
				return o1.getOrdinal() - o2.getOrdinal();
			}
		});
		for (BioEnum bioEnum : values) {
			xml.append("\t\t<value code=\"").append(bioEnum.getOrdinal()).append("\" name=\"").append(bioEnum.getName()).append("\"/>\n") ;
		}
		
		xml.append("\t</enum>\n") ;
    }
    /**
     * Exports tag definition
     * @param tag
     * @param xml
     * @param firstTag
     */
    private static void tagToXml(BioTag tag, StringBuilder xml, String firstTag) {
    	xml.append(firstTag) ;
    	xml.append(" name=\"").append(tag.getName()).append("\" code=\"").append(tag.getCode()).append("\"") ;
		if (tag.getObj() != null) {
			xml.append(" type=\"").append(tag.getObj().getType()).append("\"") ;
		} else if (tag.getEnumObj() != null) {
			xml.append(" type=\"").append(tag.getEnumObj().getName()).append("\"") ;
		} else {
			xml.append(" type=\"").append(tag.getType()).append("\"") ;
		}
		
		if (tag.isArray()) {
			xml.append(" is-array=\"true\"") ;
		} else if (tag.isList()) {
			xml.append(" is-list=\"true\"") ;
		}
		
		if (tag.isMandatory()) {
			xml.append(" is-mandatory=\"true\"") ;
		}
		
		if (!tag.isEncodable()) {
			xml.append(" is-encodable=\"false\"") ;
		}
		
		if (!tag.isExportable()) {
			xml.append(" is-xml-exportable=\"false\"") ;
		}
		
		if (tag.getTrimKeys() != null && tag.getTrimKeys().length > 0) {
			xml.append(" trim-keys=\"") ;
			String sep = "" ;
			for (int i = 0; i < tag.getTrimKeys().length; i++) {
				xml.append(sep).append(tag.getTrimKeys()[i]) ;
				sep = "," ;
			}
			xml.append("\"") ;
		}
		
		if (tag.getInverseTrimKeys() != null && tag.getInverseTrimKeys().length > 0) {
			xml.append(" inverse-trim-keys=\"") ;
			String sep = "" ;
			for (int i = 0; i < tag.getInverseTrimKeys().length; i++) {
				xml.append(sep).append(tag.getInverseTrimKeys()[i]) ;
				sep = "," ;
			}
			xml.append("\"") ;
		}
		
		if (tag.getUseKey() != null) {
			xml.append(" use-key=\"").append(tag.getUseKey()).append("\"") ;
		}
		
		if (tag.getSortKey() != null) {
			xml.append(" sort-key=\"").append(tag.getSortKey()).append("\"") ;
		}
		
		xml.append("/>\n") ;
		
    }
    /**
     * Exports function definition
     * @param func
     * @param xml
     */
    private static void funcToXml(BioFunc func, StringBuilder xml) {
		xml.append("\t<func name=\"").append(func.getName()).append("\" version=\"").append(func.getVersion()).append("\"") ;
		if (func.getFuncClass() != null) {
			xml.append(" class=\"").append(func.getFuncClass().getName()).append("\"") ;
		}
		if (func.isCached()) {
			xml.append(" is-cached=\"true\"") ;
		}
		xml.append("/>\n") ;
	}
}
