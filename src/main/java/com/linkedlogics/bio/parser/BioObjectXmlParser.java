package com.linkedlogics.bio.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.linkedlogics.bio.exception.ParserException;
import com.linkedlogics.bio.object.BioObject;


public class BioObjectXmlParser {
    public BioObjectXmlParser() {
    	
    }

    public BioObject parse(String xml) {
        try {
			return parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new ParserException(e) ;
		}
    }

    public BioObject parse(InputStream in) {
       return null ;
    }
}
