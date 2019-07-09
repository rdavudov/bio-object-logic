package com.linkedlogics.bio.dictionary.builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioDictionaryBuilder;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioFunction;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.dictionary.BioEnumObj;
import com.linkedlogics.bio.dictionary.BioFunc;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.dictionary.MergeType;
import com.linkedlogics.bio.exception.DictionaryException;
import com.linkedlogics.bio.exception.ParserException;
import com.linkedlogics.bio.expression.GenericFunction;

public class XmlReader implements DictionaryReader {
	private InputStream in ;
	private BioDictionaryBuilder builder ;
	
	public XmlReader(InputStream in) {
		this.in = in ;
	}
	
	@Override
	public void read(BioDictionaryBuilder builder) {
		this.builder = builder ;
		parse(in) ;
	}

	/**
	 * Parses bio from xml string
	 * @param xml
	 * @return
	 */
    public BioDictionary parse(String xml) {
        try {
			return parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new ParserException(e) ;
		}
    }

    /**
     * Parses bio from input stream
     * @param in
     * @return
     */
    public BioDictionary parse(InputStream in) {
    	try (in) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(in);
			doc.getDocumentElement().normalize();
			return parseDictionary(doc.getFirstChild()) ;
		} catch (Throwable e) {
			throw new ParserException(e) ;
		}
    }
    
    /**
     * Parses dictionary
     * @param e
     * @return
     */
    protected BioDictionary parseDictionary(Node e) {
    	 NamedNodeMap atts = e.getAttributes();

         int code = 0 ;
         for (int i = 0; i < atts.getLength(); i++) {
 			Node node = atts.item(i);
 			if ("code".contentEquals(node.getNodeName())) {
 				code = Integer.parseInt(node.getNodeValue()) ;
 			} 
         }
         BioDictionary dictionary = BioDictionary.getOrCreateDictionary(code) ;
         
         NodeList nodes = e.getChildNodes() ;
         for (int i = 0; i < nodes.getLength(); i++) {
         	Node node = nodes.item(i);
         	if (node.getNodeType() == Node.ELEMENT_NODE) {
         		if (node.getNodeName().contentEquals("obj")) {
         			BioObj obj = parseObj(node, dictionary.getCode()) ;
         			if (obj != null) {
         				dictionary.addObj(obj);
         				Class bioClass = obj.getBioClass() ;
         				while (bioClass != BioObject.class && bioClass != null) {
         					BioDictionary.setBioObj(bioClass, obj.getDictionary(), obj.getCode());
         					bioClass = bioClass.getSuperclass() ;
         				}
         			}
         		} else if (node.getNodeName().contentEquals("enum")) {
         			BioEnumObj enumObj = parseEnumObj(node, dictionary.getCode()) ;
         			if (enumObj != null) {
         				dictionary.addEnumObj(enumObj);
         				Class bioClass = enumObj.getBioClass() ;
         				while (bioClass != BioObject.class && bioClass != null) {
         					BioDictionary.setEnumObj(bioClass, enumObj.getDictionary(), enumObj.getCode());
         					bioClass = bioClass.getSuperclass() ;
         				}
         			}
         		} else if (node.getNodeName().contentEquals("super-tag")) {
         			BioTag tag = parseTag(node) ;
        			if (tag != null) {
        				dictionary.addSuperTag(tag);
        			}
         		} else if (node.getNodeName().contentEquals("func")) {
         			BioFunc func = parseFunc(node, dictionary.getCode()) ;
         			if (func != null) {
         				dictionary.addFunc(func);
         			}
         		}
         	}
         }
         
         return dictionary ;
    }

    /**
     * Parses bio obj
     * @param e
     * @param dictionary
     * @return
     */
    protected BioObj parseObj(Node e, int dictionary) {
    	NamedNodeMap atts = e.getAttributes();
    	int code = 0 ;
    	int version = 0 ;
    	String name = null ;
    	String type = null ;
    	String bioClass = null ;
    	for (int i = 0; i < atts.getLength(); i++) {
    		Node node = atts.item(i);
    		if ("code".contentEquals(node.getNodeName())) {
    			code = Integer.parseInt(node.getNodeValue()) ;
    		} else if ("version".contentEquals(node.getNodeName())) {
    			version = Integer.parseInt(node.getNodeValue()) ;
    		} else if ("name".contentEquals(node.getNodeName())) {
    			name = node.getNodeValue() ;
    		} else if ("type".contentEquals(node.getNodeName())) {
    			type = node.getNodeValue() ;
    		} else if ("class".contentEquals(node.getNodeName())) {
    			bioClass = node.getNodeValue() ;
    		}  
    	}
    	
    	BioObj obj = new BioObj(dictionary, code, type, name, version) ;
    	if (bioClass != null) {
    		try {
				obj.setBioClass(Class.forName(bioClass));
			} catch (ClassNotFoundException ex) {
				return null ;
			}
    		
    		if (code == 0) {
        		obj.setCode(builder.getTagHasher().hash(obj.getBioClass().getName()));
        		obj.setCodeGenerated(true);
        	}
    		
    		if (type == null) {
    			obj.setType(obj.getBioClass().getSimpleName());
    		}
    		
    		if (name == null) {
    			obj.setName(obj.getBioClass().getSimpleName().replaceAll("([^_A-Z])([A-Z])", "$1_$2").toLowerCase());
    		}
    	}
    	
    	NodeList nodes = e.getChildNodes() ;
        for (int i = 0; i < nodes.getLength(); i++) {
        	Node node = nodes.item(i);
        	if (node.getNodeType() == Node.ELEMENT_NODE) {
        		if (node.getNodeName().contentEquals("tag")) {
        			BioTag tag = parseTag(node) ;
        			if (tag != null) {
        				obj.addTag(tag);
        			}
        		}
        	}
        }
        
        return obj ;
    }
    
    /**
     * Parses enum obj
     * @param e
     * @param dictionary
     * @return
     */
    protected BioEnumObj parseEnumObj(Node e, int dictionary) {
    	NamedNodeMap atts = e.getAttributes();
    	int code = 0 ;
    	String type = null ;
    	String bioClass = null ;
    	for (int i = 0; i < atts.getLength(); i++) {
    		Node node = atts.item(i);
    		if ("code".contentEquals(node.getNodeName())) {
    			code = Integer.parseInt(node.getNodeValue()) ;
    		} else if ("type".contentEquals(node.getNodeName())) {
    			type = node.getNodeValue() ;
    		} else if ("class".contentEquals(node.getNodeName())) {
    			bioClass = node.getNodeValue() ;
    		}  
    	}
    	
    	BioEnumObj enumObj = new BioEnumObj(dictionary, code, type) ;
    	if (bioClass != null) {
    		try {
    			enumObj.setBioClass(Class.forName(bioClass));
			} catch (ClassNotFoundException ex) {
				return null ;
			}
    		
    		if (code == 0) {
    			enumObj.setCode(builder.getTagHasher().hash(enumObj.getBioClass().getName()));
    			enumObj.setCodeGenerated(true);
        	}
    		
    		if (type == null) {
    			enumObj.setType(enumObj.getBioClass().getSimpleName());
    		}
    	}
    	NodeList nodes = e.getChildNodes() ;
        for (int i = 0; i < nodes.getLength(); i++) {
        	Node node = nodes.item(i);
        	if (node.getNodeType() == Node.ELEMENT_NODE) {
        		if (node.getNodeName().contentEquals("value")) {
        			BioEnum value = parseEnumValue(node, enumObj.getBioClass()) ;
        			if (value != null) {
        				enumObj.addValue(value);
        			}
        		}
        	}
        }
        
        return enumObj ;
    }
    
    /**
     * Parses enum value
     * @param e
     * @param enumClass
     * @return
     */
    public BioEnum parseEnumValue(Node e, Class enumClass) {
    	NamedNodeMap atts = e.getAttributes();
    	int code = 0 ;
    	String name = null ;
    	
    	for (int i = 0; i < atts.getLength(); i++) {
    		Node node = atts.item(i);
    		if ("code".contentEquals(node.getNodeName())) {
    			code = Integer.parseInt(node.getNodeValue()) ;
    		} else if ("name".contentEquals(node.getNodeName())) {
    			name = node.getNodeValue() ;
    		}  
    	}
    	
    	if (BioEnum.class.isAssignableFrom(enumClass)) {
    		try {
				return (BioEnum) enumClass.getConstructor(int.class, String.class).newInstance(code, name) ;
			} catch (Throwable ex) {
				throw new DictionaryException(ex) ;
			}
    	} else {
    		return new BioEnum(code, name) ;
    	}
    }
    
    /**
     * Parses bio tag
     * @param e
     * @return
     */
    public BioTag parseTag(Node e) {
    	NamedNodeMap atts = e.getAttributes();
    	int code = 0 ;
    	String name = null ;
    	String type = null ;
    	boolean isArray = false ;
    	boolean isList = false ;
    	boolean isMandatory = false ;
    	boolean isEncodable = true ;
    	boolean isExportable = true ;
    	boolean isInheritable = false ;
    	boolean isProtected = false ;
    	String initial = null ;
    	String expression = null ;
    	String trimKeys = null ;
    	String inverseTrimKeys = null ;
    	String useKey = null ;
    	String sortKey = null ;
    	MergeType mergeType = MergeType.Replace ;
    	
    	for (int i = 0; i < atts.getLength(); i++) {
    		Node node = atts.item(i);
    		if ("code".contentEquals(node.getNodeName())) {
    			code = Integer.parseInt(node.getNodeValue()) ;
    		} else if ("type".contentEquals(node.getNodeName())) {
    			type = node.getNodeValue() ;
    		} else if ("name".contentEquals(node.getNodeName())) {
    			name = node.getNodeValue() ;
    		} else if ("is-array".contentEquals(node.getNodeName())) {
    			isArray = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("is-list".contentEquals(node.getNodeName())) {
    			isList = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("is-mandatory".contentEquals(node.getNodeName())) {
    			isMandatory = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("is-encodable".contentEquals(node.getNodeName())) {
    			isEncodable = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("is-exportable".contentEquals(node.getNodeName())) {
    			isExportable = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("is-inheritable".contentEquals(node.getNodeName())) {
    			isInheritable = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("is-protected".contentEquals(node.getNodeName())) {
    			isProtected = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("initial".contentEquals(node.getNodeName())) {
    			initial = node.getNodeValue() ;
    		} else if ("expression".contentEquals(node.getNodeName())) {
    			expression = node.getNodeValue() ;
    		} else if ("trim-keys".contentEquals(node.getNodeName())) {
    			trimKeys = node.getNodeValue() ;
    		} else if ("inverse-trim-keys".contentEquals(node.getNodeName())) {
    			inverseTrimKeys = node.getNodeValue() ;
    		} else if ("use-key".contentEquals(node.getNodeName())) {
    			useKey = node.getNodeValue() ;
    		} else if ("sort-key".contentEquals(node.getNodeName())) {
    			sortKey = node.getNodeValue() ;
    		} else if ("merge-by".contentEquals(node.getNodeName())) {
    			mergeType = Enum.valueOf(MergeType.class, node.getNodeValue()) ;
    		}  
    	}
    	
    	BioTag tag = null ;
    	try {
			tag = new BioTag(code, name, Enum.valueOf(BioType.class, type)) ;
        } catch (IllegalArgumentException e1) {
        	tag = new BioTag(code, name, BioType.BioObject) ;
        	tag.setObjName(type);
        }

    	if (code == 0) {
    		tag.setCode(builder.getTagHasher().hash(tag.getName()));
    		tag.setCodeGenerated(true);
    	}
    	
    	tag.setArray(isArray);
    	tag.setList(isList);
    	tag.setMandatory(isMandatory);
    	tag.setEncodable(isEncodable);
    	tag.setExportable(isExportable);
    	tag.setInitial(initial);
    	tag.setExpression(expression);
    	tag.setUseKey(useKey);
    	tag.setSortKey(sortKey);
    	if (trimKeys != null) {
    		tag.setTrimKeys(trimKeys.split(","));
    	}
    	if (inverseTrimKeys != null) {
    		tag.setInverseTrimKeys(inverseTrimKeys.split(","));
    	}
    	
    	return tag ;
    }
    
    /**
     * Parses bio function
     * @param e
     * @param dictionary
     * @return
     */
    public BioFunc parseFunc(Node e, int dictionary) {
    	NamedNodeMap atts = e.getAttributes();
    	int version = 0 ;
    	String name = null ;
    	String funcClass = null ;
    	String expression = null ;
    	String parameters = null ;
    	boolean isCached = false ;
    	for (int i = 0; i < atts.getLength(); i++) {
    		Node node = atts.item(i);
    		if ("version".contentEquals(node.getNodeName())) {
    			version = Integer.parseInt(node.getNodeValue()) ;
    		} else if ("name".contentEquals(node.getNodeName())) {
    			name = node.getNodeValue() ;
    		} else if ("class".contentEquals(node.getNodeName())) {
    			funcClass = node.getNodeValue() ;
    		} else if ("is-cached".contentEquals(node.getNodeName())) {
    			isCached = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("parameters".contentEquals(node.getNodeName())) {
    			parameters = node.getNodeValue() ;
    		}
    	}
    	
    	expression = e.getTextContent().trim() ;

    	try {
    		if (funcClass != null) {
    			return new BioFunc(name, (Class<? extends BioFunction>) Class.forName(funcClass), isCached, dictionary, version) ;
    		} else if (expression != null) {
    			BioFunc f = new BioFunc(name, GenericFunction.class, isCached, dictionary, version) ;
    			f.setCached(new GenericFunction(expression, parameters.split(",")));
    			f.setCached(true);
    			return f ;
    		} else {
    			throw new ParserException("missing class/expression in func definition") ;
    		}
    	} catch (ClassNotFoundException ex) {

		}
    	return null ;
    }
}
