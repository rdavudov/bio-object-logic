package com.linkedlogics.bio.utility;

import com.linkedlogics.bio.object.BioObject;
import com.linkedlogics.bio.parser.BioObjectXmlParser;

public class XMLUtility {
	public static String toXml(BioObject object) {
		return null ;
	}
	
	public static BioObject fromXml(String xml) {
		return new BioObjectXmlParser().parse(xml) ;
	}
}
