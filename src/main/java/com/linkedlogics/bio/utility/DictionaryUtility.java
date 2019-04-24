package com.linkedlogics.bio.utility;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.expression.GenericFunction;
import com.linkedlogics.bio.stream.BiFastStream;
import com.linkedlogics.bio.stream.BoFastStream;

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
		});
		
		dictionary.getEnumCodeMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioEnumObj::getCode)).collect(Collectors.toList()).forEach(e -> {
			enumToXml(e, xml);
		});
		
		dictionary.getSuperTagCodeMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioTag::getCode)).collect(Collectors.toList()).forEach(t -> {
			tagToXml(t, xml, "\t<super-tag");
		});

		dictionary.getFuncNameMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioFunc::getName)).collect(Collectors.toList()).forEach(f -> {
			funcToXml(f, xml);
		});
		
		xml.append("</dictionary>") ;
		return xml.toString() ;
	}

	/**
	 * Exports obj definition
	 * @param obj
	 * @param xml
	 */
	private static void objToXml(BioObj obj, StringBuilder xml) {
		xml.append("\t<obj type=\"").append(obj.getType()).append("\"") ;
		if (obj.getName() != null) {
			xml.append(" name=\"").append(obj.getName()).append("\"") ;
		}
//		if (!obj.isCodeGenerated()) {
			xml.append(" code=\"").append(obj.getCode()).append("\"");
//		}
		xml.append(" version=\"").append(obj.getVersion()).append("\"") ;
		if (obj.getBioClass() != null) {
			xml.append(" class=\"").append(obj.getBioClass().getName()).append("\"") ;
		}
		xml.append(">\n") ;
		obj.getCodeMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioTag::getCode)).forEach(t -> {
			tagToXml(t, xml, "\t\t<tag");
		});

		xml.append("\t</obj>\n") ;
	}
	/**
	 * Exports enum definition
	 * @param enumObj
	 * @param xml
	 */
	private static void enumToXml(BioEnumObj enumObj, StringBuilder xml) {
    	xml.append("\t<enum type=\"").append(enumObj.getType()).append("\"") ;
//    	if (!enumObj.isCodeGenerated()) {
			xml.append(" code=\"").append(enumObj.getCode()).append("\"");
//		}
    	xml.append(" version=\"").append(enumObj.getVersion()).append("\"") ;
    	if (enumObj.getBioClass() != null) {
			xml.append(" class=\"").append(enumObj.getBioClass().getName()).append("\"") ;
		}
		xml.append(">\n") ;
		enumObj.getCodeMap().entrySet().stream().map(e -> {
			return e.getValue() ;
		}).sorted(Comparator.comparing(BioEnum::getOrdinal)).forEach(e -> {
			xml.append("\t\t<value code=\"").append(e.getOrdinal()).append("\" name=\"").append(e.getName()).append("\"/>\n") ;
		});
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
    	xml.append(" name=\"").append(tag.getName()).append("\"") ;
//    	if (!tag.isCodeGenerated()) {
			xml.append(" code=\"").append(tag.getCode()).append("\"");
//		}
		if (tag.getObj() != null) {
			xml.append(" type=\"").append(tag.getObj().getType()).append("\"") ;
		} else if (tag.getEnumObj() != null) {
			xml.append(" type=\"").append(tag.getEnumObj().getType()).append("\"") ;
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
			xml.append(" is-exportable=\"false\"") ;
		}
		if (tag.getTrimKeys() != null && tag.getTrimKeys().length > 0) {
			xml.append(" trim-keys=\"") ;
			xml.append(StringUtility.join(tag.getTrimKeys())) ;
			xml.append("\"") ;
		}
		if (tag.getInverseTrimKeys() != null && tag.getInverseTrimKeys().length > 0) {
			xml.append(" inverse-trim-keys=\"") ;
			xml.append(StringUtility.join(tag.getInverseTrimKeys())) ;
			xml.append("\"") ;
		}
		if (tag.getUseKey() != null) {
			xml.append(" use-key=\"").append(tag.getUseKey()).append("\"") ;
		}
		if (tag.getSortKey() != null) {
			xml.append(" sort-key=\"").append(tag.getSortKey()).append("\"") ;
		}
		if (tag.getInitial() != null) {
			xml.append(" initial=\"").append(tag.getInitial()).append("\"") ;
		}
		if (tag.getExpression() != null) {
			xml.append(" expression=\"").append(tag.getExpression()).append("\"") ;
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
		if (func.getFunction() instanceof GenericFunction) {
			GenericFunction generic = (GenericFunction) func.getFunction() ;
			if (generic.getParameterNames() != null) {
				xml.append(" parameters=\"").append(Arrays.stream(generic.getParameterNames()).collect(Collectors.joining(","))).append("\"") ;
			}
			xml.append(">\n") ;
			xml.append("\t\t").append(generic.getExpression()).append("\n") ;
			xml.append("\t</func>\n") ;
		} else {
			if (func.getFuncClass() != null) {
				xml.append(" class=\"").append(func.getFuncClass().getName()).append("\"") ;
			}
			if (func.isCached()) {
				xml.append(" is-cached=\"true\"") ;
			}
			xml.append("/>\n") ;
		}
	}
    
    public static byte[] toBytes(BioDictionary dictionary) throws IOException {
    	BoFastStream out = new BoFastStream() ;
    	out.writeShort(dictionary.getCode());
    	out.writeShort(dictionary.getCodeMap().size());
    	
    	for (Entry<Integer, BioObj> obj : dictionary.getCodeMap().entrySet()) {
    		out.writeShort(obj.getValue().getCode());
			out.writeAsciiString(obj.getValue().getName());
			out.writeAsciiString(obj.getValue().getType());
			out.writeShort(obj.getValue().getVersion());
			if (obj.getValue().getBioClass() != null) {
				out.writeAsciiString(obj.getValue().getBioClass().getName());
			} else {
				out.writeAsciiString(null);
			}
			
			out.writeShort(obj.getValue().getCodeMap().size());
			for (Entry<Integer, BioTag> tag : obj.getValue().getCodeMap().entrySet()) {
				out.writeShort(tag.getValue().getCode()) ;
				out.writeAsciiString(tag.getValue().getName());
				out.writeByte(tag.getValue().getType().value()) ;
				out.writeBoolean(tag.getValue().isMandatory());
				out.writeBoolean(tag.getValue().isEncodable());
				out.writeBoolean(tag.getValue().isExportable());
				out.writeBoolean(tag.getValue().isArray());
				out.writeBoolean(tag.getValue().isList());
				if (tag.getValue().getType() == BioType.BioObject || tag.getValue().getType() == BioType.BioEnum) {
					out.writeAsciiString(tag.getValue().getObjName());
				}
				out.writeAsciiString(tag.getValue().getInitial());
				out.writeAsciiString(tag.getValue().getExpression());
			}
		}
    	
    	out.writeShort(dictionary.getEnumCodeMap().size());
    	for (Entry<Integer, BioEnumObj> obj : dictionary.getEnumCodeMap().entrySet()) {
    		out.writeShort(obj.getValue().getCode());
			out.writeAsciiString(obj.getValue().getType());
			out.writeShort(obj.getValue().getVersion());
			if (obj.getValue().getBioClass() != null) {
				out.writeAsciiString(obj.getValue().getBioClass().getName());
			} else {
				out.writeAsciiString(null);
			}
			out.writeShort(obj.getValue().getCodeMap().size());
			for (Entry<Integer, BioEnum> e : obj.getValue().getCodeMap().entrySet()) {
				out.writeInt(e.getValue().getOrdinal()) ;
				out.writeAsciiString(e.getValue().getName());
			}
    	}
    	
    	
    	
    	out.flush() ;
    	out.close();
    	return out.getBytes() ;
    }
    
    public static BioDictionary fromBytes(byte[] bytes) {
		BiFastStream in = new BiFastStream(bytes) ;
		BioDictionary dictionary = new BioDictionary(in.readShort()) ;
		int objCount = in.readShort() ;
		for (int i = 0; i < objCount; i++) {
			BioObj obj = new BioObj() ;
			obj.setCode(in.readShort());
			obj.setName(in.readAsciiString());
			obj.setType(in.readAsciiString());
			obj.setVersion(in.readShort());
			try {
				obj.setClassName(in.readAsciiString());
			} catch (ClassNotFoundException e) { }
			int tagCount = in.readShort() ;
			for (int j = 0; j < tagCount; j++) {
				BioTag tag = new BioTag() ;
				tag.setCode(in.readShort());
				tag.setName(in.readAsciiString());
				tag.setType(BioType.getType(in.readByte()));
				tag.setMandatory(in.readBoolean());
				tag.setEncodable(in.readBoolean());
				tag.setExportable(in.readBoolean());
				tag.setArray(in.readBoolean());
				tag.setList(in.readBoolean());
				if (tag.getType() == BioType.BioObject || tag.getType() == BioType.BioEnum) {
					tag.setObjName(in.readAsciiString());
				}
				tag.setInitial(in.readAsciiString());
				tag.setExpression(in.readAsciiString());
				
				obj.addTag(tag);
			}
			
			dictionary.addObj(obj);
		}
		
		int enumCount = in.readShort() ;
		for (int i = 0; i < enumCount; i++) {
			BioEnumObj enumObj = new BioEnumObj() ;
			enumObj.setCode(in.readShort());
			enumObj.setType(in.readAsciiString());
			enumObj.setVersion(in.readShort());
			try {
				enumObj.setClassName(in.readAsciiString());
			} catch (ClassNotFoundException e) { }
			int valueCount = in.readShort() ;
			for (int j = 0; j < valueCount; j++) {
				int ordinal = in.readInt() ;
				String name = in.readAsciiString() ;
				try {
					BioEnum value = (BioEnum) enumObj.getBioClass().getConstructor(int.class, String.class).newInstance(ordinal, name) ;
					enumObj.addValue(value);
				} catch (Throwable e) { e.printStackTrace();}
			}
			
			dictionary.addEnumObj(enumObj);
		}
		
		in.close();
//		validate(dictionary);
		return dictionary ;
	}
    
   
}
