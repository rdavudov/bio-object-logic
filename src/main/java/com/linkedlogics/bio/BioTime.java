package com.linkedlogics.bio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.linkedlogics.bio.exception.ParserException;
import com.linkedlogics.bio.time.DayExpression;
import com.linkedlogics.bio.time.FixedExpression;
import com.linkedlogics.bio.time.HourExpression;
import com.linkedlogics.bio.time.MinuteExpression;
import com.linkedlogics.bio.time.MonthDayExpression;
import com.linkedlogics.bio.time.MonthExpression;
import com.linkedlogics.bio.time.MultipleExpression;
import com.linkedlogics.bio.time.SecondExpression;
import com.linkedlogics.bio.time.TimeExpression;
import com.linkedlogics.bio.time.WeekDayExpression;
import com.linkedlogics.bio.time.YearExpression;

/**
 * TimeUtility is a flexible static class to easily manipulate with times. For example:
 * 1d - adds 1 day or 24 hours
 * 1d< - adds 1 day and shifts time to beginning of day
 * 1d> - adds 1 day and shifts time to end of day
 * -1d - subtracts 1 day or 24 hours
 * 
 * All possible keywords:
 * s - for seconds
 * m - for minutes
 * h - for hours
 * d - for days
 * md - for calendar month days [
 * wd - for calendar week days [1..7]
 * M - for months
 * y - for years
 * 
 * You can also use multiple expressions. For example:
 * 1md + 3wd = next month first calendar weekday wednesday
 * 0d + 3h = today at 03:00 AM
 *  
 * You can also use fixed dates
 * =31.12.2015 =  
 *  
 * @author rdavudov
 *
 */
public class BioTime {
	public static String DATE_FORMAT = "dd/MM/yyyy" ;
	public static String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS" ;
	
	private static HashMap<String, MultipleExpression> cache = new HashMap<String, MultipleExpression>() ;
	public static final long SECOND = 1000 ;
	public static final long MINUTE = 60 * SECOND ;
	public static final long HOUR = 60 * MINUTE ;
	public static final long DAY = 24 * HOUR ;
	
	/**
	 * provides you new time based on expression
	 * @param expr
	 * @return
	 */
	public static long getTime(String expr) {
		return getTime(expr, BioTime.getCurrentTime()) ;
	}
	
	/**
	 * provides you new time based on expression
	 * @param expr
	 * @param now
	 * @return
	 */
	public static long getTime(String expr, long now) {
		try {
			MultipleExpression time = null ;
			if (cache.containsKey(expr)) {
				time = cache.get(expr) ;
			} else {
				time = parse(expr) ;
				if (time != null) {
					cache.put(expr, time) ;
				} else {
					throw new RuntimeException("unable to parse time expression " + expr) ;
				}
			}
			
			return time.getTime(now) ;
		} catch (Exception e) {
			throw new ParserException("unable to parse time expression " + expr, e) ;
		}
	}
	
	/**
	 * Provides diff count of intervals
	 * @param start
	 * @param end
	 * @param expr
	 * @return
	 */
	public static int getDiff(long start, long end, String expr) {
		int diffs = 0 ;
		start = getTime(expr, start) ;
		while (start <= end) {
			diffs++ ;
			start = getTime(expr, start) ;
		}
		return diffs ;
	}
	
	/**
	 * Parsers several bio time expressions
	 * @param expr
	 * @return
	 */
	private static MultipleExpression parse(String expr) {
		MultipleExpression multiple = new MultipleExpression() ;
		String[] times = expr.split("\\+") ;
		for (int i = 0; i < times.length; i++) {
			if (times[i].length() == 0) {
				// if time starts with +
				continue ;
			}
			String[] minTimes = times[i].split("\\-") ;
			if (minTimes.length == 1) {
				multiple.addExpression(parseExpression(times[i].trim())) ;
			} else {
				if (minTimes[0].length() != 0) {
					multiple.addExpression(parseExpression(minTimes[0].trim())) ;
				}
				for (int j = 1; j < minTimes.length; j++) {
					TimeExpression e = parseExpression(minTimes[j].trim()) ;
					e.setNegative(true) ;
					multiple.addExpression(e) ;
				}
			}
		}
		return multiple ;
	}
	
	/**
	 * Parses time expression definition
	 * @param expr
	 * @return
	 */
	private static TimeExpression parseExpression(String expr) {
		TimeExpression expression = null ;
		
		if (expr.startsWith("=")) {
			return new FixedExpression(parseString(expr.substring(1))) ;
		} 
		
		StringBuilder amount = new StringBuilder() ;
		StringBuilder period = new StringBuilder() ;
		StringBuilder shift = new StringBuilder() ;
		StringBuilder module = new StringBuilder() ;
		for (int i = 0; i < expr.length(); i++) {
			char c = expr.charAt(i) ;
			if (Character.isDigit(c)) {
				amount.append(c) ;
			} else if (Character.isAlphabetic(c)) {
				period.append(c) ;
			} else if (c == '*') {
				module.append(c) ;
			} else if (c == '<' || c == '>') {
				shift.append(c) ;
			}
		}
		
		int a = Integer.parseInt(amount.toString()) ;
		TimeExpression.Shift s = TimeExpression.Shift.NO_SHIFT ;
		
		String shiftStr = shift.toString() ;
		if (shiftStr.equals("<")) {
			s = TimeExpression.Shift.LEFT_SHIFT ;
		} else if (shiftStr.equals(">")) {
			s = TimeExpression.Shift.RIGHT_SHIFT ;
		}
		
		String periodStr = period.toString() ;
		if (periodStr.equalsIgnoreCase(SecondExpression.PERIOD)) {
			expression = new SecondExpression(a, s) ;
		} else if (periodStr.equals(MinuteExpression.PERIOD)) {
			expression = new MinuteExpression(a, s) ;
		} else if (periodStr.equalsIgnoreCase(HourExpression.PERIOD)) {
			expression = new HourExpression(a, s) ;
		} else if (periodStr.equalsIgnoreCase(DayExpression.PERIOD)) {
			expression = new DayExpression(a, s) ;
		} else if (periodStr.equalsIgnoreCase(MonthDayExpression.PERIOD)) {
			expression = new MonthDayExpression(a, s) ;
		} else if (periodStr.equalsIgnoreCase(WeekDayExpression.PERIOD)) {
			expression = new WeekDayExpression(a, s) ;
		} else if (periodStr.equals(MonthExpression.PERIOD)) {
			expression = new MonthExpression(a, s) ;
		} else if (periodStr.equals(YearExpression.PERIOD)) {
			expression = new YearExpression(a, s) ;
		} else {
			throw new RuntimeException("unable to parse time expression " + expr) ;
		}
		
		expression.setModule(module.length() > 0);
		
		return expression ;
	}
	
	/**
	 * Formats provided time
	 * @param time
	 * @return
	 */
	public static final String format(long time) {
		return new SimpleDateFormat(DATETIME_FORMAT).format(new Date(time)) ;
	}
	
	/**
	 * Formats given time
	 * @param time
	 * @param format
	 * @return
	 */
	public static final String format(long time, String format) {
		return new SimpleDateFormat(format).format(new Date(time)) ;
	}
	
	/**
	 * Formats system time
	 * @param format
	 * @return
	 */
	public static final String format(String format) {
		return new SimpleDateFormat(format).format(new Date(getCurrentTime())) ;
	}
	
	/**
	 * Parses string to time using available formats
	 * @param time
	 * @return
	 */
	public static final long parseString(String time) {
		List<String> supportedFormats = BioDictionary.getSupportedDateFormats() ;
		supportedFormats.add(DATE_FORMAT) ;
		supportedFormats.add(DATETIME_FORMAT) ;
		
		for (int i = 0; i < supportedFormats.size(); i++) {
			SimpleDateFormat formatter = new SimpleDateFormat(supportedFormats.get(i)) ;
			try {
				return formatter.parse(time).getTime() ;
			} catch (ParseException e) {}
		}
		
		throw new ParserException("date time format couldn't found for " + time) ;
	}
	
	/**
	 * Returns current time
	 * @return
	 */
	public static long getCurrentTime() {
		return System.currentTimeMillis() ;
	}
}
