package com.linkedlogics.bio.time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BioTime {
	public static String DATE_FORMAT = "dd/MM/yyyy" ;
	public static final String NATIVE_DATE_FORMAT = "yyyyMMdd" ;
	public static String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS" ;
	public static final String NATIVE_DATETIME_FORMAT = "yyyyMMddHHmmss" ;
	
	public static long getTime(String expr) {
		return 0 ;
	}
	
	public static final long parse(String time, String format) {
		return 0 ;
	}
	
	public static final String format(long time) {
		return new SimpleDateFormat(DATETIME_FORMAT).format(new Date(time)) ;
	}
	
	public static final String format(long time, String format) {
		return new SimpleDateFormat(format).format(new Date(time)) ;
	}
}
