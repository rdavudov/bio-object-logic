package com.linkedlogics.bio.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.linkedlogics.bio.BioDictionary;
import com.linkedlogics.bio.BioEnum;
import com.linkedlogics.bio.BioExpression;
import com.linkedlogics.bio.BioObject;
import com.linkedlogics.bio.dictionary.BioEnumObj;
import com.linkedlogics.bio.dictionary.BioObj;
import com.linkedlogics.bio.dictionary.BioTag;
import com.linkedlogics.bio.dictionary.BioType;
import com.linkedlogics.bio.exception.ParserException;
import com.linkedlogics.bio.expression.Conditional;
import com.linkedlogics.bio.utility.ConversionUtility;
import com.linkedlogics.bio.utility.XMLUtility;

/**
 * Bio xml parser parses xml into bio object
 * @author rajab
 *
 */
public class BioObjectXmlParser {
	private boolean isValidated ;
	private boolean isImprovised ;
	
	public boolean isValidated() {
		return isValidated;
	}

	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}

	public boolean isImprovised() {
		return isImprovised;
	}

	public void setImprovised(boolean isImprovised) {
		this.isImprovised = isImprovised;
	}

	/**
	 * Parses bio from xml string
	 * @param xml
	 * @return
	 */
    public BioObject parse(String xml) {
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
    public BioObject parse(InputStream in) {
    	try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(in);
			doc.getDocumentElement().normalize();
			return parseBioObject(doc.getFirstChild(), null) ;
		} catch (Throwable e) {
			throw new ParserException(e) ;
		}
    }
    
    /**
     * Parses a bio object and it tags
     * @param e
     * @param bioClass
     * @return
     */
    protected BioObject parseBioObject(Node e, Class<? extends BioObject> bioClass) {
        NamedNodeMap atts = e.getAttributes();

        int dictionary = 0 ;
        int code = 0 ;
        int version = 0 ;
        String type = null ;
        
		for (int i = 0; i < atts.getLength(); i++) {
			Node node = atts.item(i);
			if ("dictionary".contentEquals(node.getNodeName())) {
				dictionary = Integer.parseInt(node.getNodeValue()) ;
			} else if ("code".contentEquals(node.getNodeName())) {
				code = Integer.parseInt(node.getNodeValue()) ;
			} else if ("version".contentEquals(node.getNodeName())) {
				version = Integer.parseInt(node.getNodeValue()) ;
			} else if ("type".contentEquals(node.getNodeName())) {
				type = node.getNodeValue() ;
			} else if ("class".contentEquals(node.getNodeName())) {
				try {
					bioClass = (Class<? extends BioObject>) Class.forName(node.getNodeValue());
				} catch (Throwable ex) {
					if (isValidated) {
						throw new ParserException(ex) ;
					} else {
						return null ;
					}
				}
			}
		}
        
        
        BioObject object = null;
        BioObj obj = null ;
        
        if (bioClass == null) {
        	if (code > 0) {
        		obj = BioDictionary.getDictionary(dictionary).getObjByCode(code) ;
        	} else if (type != null) {
        		obj = BioDictionary.getDictionary(dictionary).getObjByType(type) ;
        	} else {
        		obj = BioDictionary.getDictionary(dictionary).getObjByName(e.getNodeName()) ;
        	}
        	
        	if (obj != null && obj.getBioClass() != null) {
        		object = BioDictionary.getDictionary(dictionary).getFactory().newBioObject(obj.getCode()) ;
        	} else {
        		object = new BioObject(0, e.getNodeName()) ;
        	}
        } else {
        	try {
				object = (BioObject) bioClass.getConstructor().newInstance() ;
				obj = BioDictionary.getDictionary(dictionary).getObjByCode(object.getBioCode());
			} catch (Throwable ex) {
				throw new ParserException(ex) ;
			}
        }
        
        if (version > 0) {
        	 object.setBioVersion(version) ;
        }
        
        // starting to parse tags
        NodeList nodes = e.getChildNodes() ;
        for (int i = 0; i < nodes.getLength(); i++) {
        	Node node = nodes.item(i);
        	if (node.getNodeType() == Node.ELEMENT_NODE) {
        		String tagName = node.getNodeName().replaceAll("-", "_");
        		BioTag tag = null ;
        		if (obj != null) {
        			tag = obj.getTag(tagName);
        		}
        		try {
					Object value = parseTagValue(node, obj, tag) ;
					if (value != null) {
						object.set(tagName, value) ;
					}
				} catch (Exception ex) {
					throw new ParserException("error in parsing " + tagName, ex) ; 
				}
        	}
		}

        return object ;
    }
    
    /**
     * Parses a single tag
     * @param e
     * @param obj
     * @param tag
     * @return
     */
    public Object parseTagValue(Node e, BioObj obj, BioTag tag) {
    	Boolean isArray = null ;
    	Boolean isList = null ;
    	String type = null ;
    	
    	NamedNodeMap atts = e.getAttributes();
    	for (int i = 0; i < atts.getLength(); i++) {
    		Node node = atts.item(i);
    		if ("is-array".contentEquals(node.getNodeName())) {
    			isArray = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("is-list".contentEquals(node.getNodeName())) {
    			isList = Boolean.parseBoolean(node.getNodeValue()) ;
    		} else if ("type".contentEquals(node.getNodeName())) {
    			type = node.getNodeValue() ;
    		}
    	}

    	BioType elementType = null ;
    	if (type != null) {
    		elementType = getType(obj != null ? obj.getDictionary() : 0, type);
    	} else if (tag != null) {
    		elementType = tag.getType() ;
    		if (isArray == null) {
    			isArray = tag.isArray() ;
    		}
    		if (isList == null) {
    			isList = tag.isList() ;
    		}
    	} else if (e.hasAttributes() && e.getAttributes().getNamedItem("code") != null) {
    		elementType = BioType.BioObject ;
    	}
    	
    	if (elementType == null) {
    		if (isImprovised) {
    			if (e.getChildNodes().getLength() == 1) {
    				return e.getTextContent() ;
    			} else {
    				elementType = BioType.BioObject ;
    			}
    		} else 
    			return null ;
    	}
    	
    	// setting to default FALSE if not provided
    	if (isArray == null) {
			isArray = false ;
		}
		if (isList == null) {
			isList = false ;
		}

    	if (elementType == BioType.BioObject || elementType == BioType.Properties) {
    		if (isArray || isList) {
    			if (isList) {
    				return parseList(e, tag != null ? tag.getObj().getBioClass() : null);
    			} else {
    				return parseArray(e, tag != null ? tag.getObj().getBioClass() : null);
    			}
    		} else {
    			BioObject object = parseBioObject(e, tag != null ? tag.getObj().getBioClass() : null);
    			if (tag != null && object.getBioName() == null) {
    				object.setBioName(tag.getName());
    			}
    			return object ;
    		}
    	} else if (elementType == BioType.BioEnum) {
    		BioEnumObj bioEnumObj = null ;

    		if (tag != null) {
    			bioEnumObj = tag.getEnumObj();
    		} else {
    			bioEnumObj = BioDictionary.getDictionary(obj != null ? obj.getDictionary() : 0).getBioEnumObj(type) ;
    		}

    		if (isArray || isList) {
    			BioEnum[] bioEnumArray = null;
    			String[] values = e.getTextContent().trim().split(XMLUtility.SEP);
    			if (bioEnumObj != null && bioEnumObj.getBioClass() != null) {
    				bioEnumArray = (BioEnum[]) Array.newInstance(bioEnumObj.getBioClass(), values.length);
    				for (int i = 0; i < values.length; i++)
    					bioEnumArray[i] = bioEnumObj.getBioEnum(values[i]);
    				if (isList)
    					return Arrays.asList(bioEnumArray);
    				else
    					return bioEnumArray;
    			} else {
    				return null ;
    			}
    		} else if (bioEnumObj != null)
    			return bioEnumObj.getBioEnum(e.getTextContent().trim());
    		
    		return null ;
    	} else if (elementType == BioType.Dynamic) {
    		return BioExpression.parse(e.getTextContent().trim()) ;
    	} else if (elementType == BioType.Formatted) {
    		return BioExpression.parseFormatted(e.getTextContent().trim()) ;
    	} else if (elementType == BioType.Conditional) {
    		Object value = null;
    		Object elseValue = null;
    		BioExpression condition = null ;

    		NodeList nodes = e.getChildNodes() ;
    		for (int i = 0; i < nodes.getLength(); i++) {
    			Node node = nodes.item(i);
    			if (node.getNodeType() == Node.ELEMENT_NODE) {
    				if (node.getNodeName().equals("condition")) {
    					condition = BioExpression.parse(node.getTextContent()) ;
    				} else if (node.getNodeName().equals("value")) {
    					value = parseTagValue(node, null, null) ;
    				} else if (node.getNodeName().equals("else-value")) {
    					elseValue = parseTagValue(node, null, null) ;
    				}
    			}
    		}
    		
    		if (condition != null && value != null) {
    			return new Conditional(condition, value, elseValue) ;
    		}
    		return null ;
    	} else {
    		if (isArray || isList) {
    			Object[] array = ConversionUtility.convertAsArray(elementType, e.getTextContent().trim());
    			if (isList) {
        			ArrayList<Object> list = new ArrayList<Object>();
        			for (int i = 0; i < array.length; i++) {
        				list.add(array[i]);
        			}
        			return list ;
        		} else {
        			return array ;
        		}
    		} else {
    			return ConversionUtility.convert(elementType, e.getTextContent().trim());
    		}
    	}
    }

    protected List<BioObject> parseList(Node e, Class<? extends BioObject> bioClass) {
    	NodeList nodes = e.getChildNodes() ;
    	List<BioObject> list = new ArrayList<BioObject>(nodes.getLength()) ;
    	for (int i = 0; i < nodes.getLength(); i++) {
    		Node node = nodes.item(i);
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			BioObject object = parseBioObject(node, bioClass);
    			if (object != null) {
    				list.add(object) ;
    			}
    		}
    	}
    	return list ;
    }
    
    
    protected BioObject[] parseArray(Node e, Class<? extends BioObject> bioClass) {
    	List<BioObject> list = parseList(e, bioClass);
    	if (list.size() > 0) {
    		if (bioClass == null) {
    			bioClass = list.get(0).getClass() ;
    			while (bioClass != BioObject.class) {
    				boolean isFine = true ;
    				for (int i = 1; i < list.size(); i++) {
    					if (!bioClass.isAssignableFrom(list.get(i).getClass())) {
    						isFine = false ;
    						break ;
    					}
    				}
    				if (isFine) {
    					break ;
    				}
    				bioClass = (Class<? extends BioObject>) bioClass.getSuperclass() ;
    			}
    		}
			
			BioObject[] array = (BioObject[]) Array.newInstance(bioClass, list.size());
			list.toArray(array) ;
			return array;	
		} else {
			return null ;
		}
    }
    
    protected BioType getType(int dictionary, String type) {
        if (type != null) {
            try {
                return Enum.valueOf(BioType.class, type);
            } catch (IllegalArgumentException e1) {
                if (BioDictionary.getDictionary(dictionary).isBioEnum(type)) {
                	return BioType.BioEnum ;
                } else if (BioDictionary.getDictionary(dictionary).isBioObj(type)) {
                	 return BioType.BioObject;
                }
                return null ;
            }
        }
        return null ;
    }
}
