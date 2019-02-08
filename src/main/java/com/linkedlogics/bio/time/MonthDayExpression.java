package com.linkedlogics.bio.time;

import java.time.LocalDateTime;

public class MonthDayExpression extends TimeExpression {
	public static final String PERIOD = "md" ;
	
	public MonthDayExpression(int amount, Shift shift) {
		super(amount, shift) ;
	}
	
	@Override
	public long getNextTime(long now) {
		LocalDateTime time = getLocalTime(now) ;

		int monthDay = time.getDayOfMonth() ;
		if (amount > monthDay) {
			time = time.withDayOfMonth(amount) ;
		} else {
			time = time.plusMonths(1).withDayOfMonth(amount) ;
		}
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.withHour(0).withMinute(0).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.withHour(23).withMinute(59).withSecond(59).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time) ;
		}
	}

	@Override
	public long getPrevTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		int monthDay = time.getDayOfMonth() ;
		if (amount > monthDay) {
			time = time.withDayOfMonth(amount).minusMonths(1) ;
		} else {
			time = time.withDayOfMonth(amount) ;
		}
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.withHour(0).withMinute(0).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.withHour(23).withMinute(59).withSecond(59).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time) ;
		}
	}
	
	@Override
	public String getPeriod() {
		return PERIOD;
	}
}
