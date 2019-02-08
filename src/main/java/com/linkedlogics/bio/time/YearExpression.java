package com.linkedlogics.bio.time;

import java.time.LocalDateTime;
import java.util.Calendar;

public class YearExpression extends TimeExpression {
	public static final String PERIOD = "y" ;
	
	public YearExpression(int amount, Shift shift) {
		super(amount, shift) ;
	}
	
	@Override
	public long getNextTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusYears(amount).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusYears(amount).withMonth(12).withDayOfMonth(31).withHour(0).withMinute(0).withSecond(0).plusDays(1).minusSeconds(1).plusMonths(1).minusSeconds(1).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusMonths(amount)) ;
		}
	}

	@Override
	public long getPrevTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusYears(-amount).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusYears(-amount).withMonth(12).withDayOfMonth(31).withHour(0).withMinute(0).withSecond(0).plusDays(1).minusSeconds(1).plusMonths(1).minusSeconds(1).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusMonths(-amount)) ;
		}
	}
	
	@Override
	public String getPeriod() {
		return PERIOD;
	}
}
