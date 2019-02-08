package com.linkedlogics.bio.time;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class WeekDayExpression extends TimeExpression {
	public static final String PERIOD = "wd" ;
	
	public WeekDayExpression(int amount, Shift shift) {
		super(amount, shift) ;
	}
	
	@Override
	public long getNextTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		int weekDay = time.getDayOfWeek().ordinal() + 1;
		int amount = this.amount ;
		if (amount > weekDay) {
			amount = amount - weekDay;
		} else {
			amount = amount - weekDay + 7 ;
		}
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusDays(amount).withHour(0).withMinute(0).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusDays(amount).withHour(23).withMinute(59).withSecond(59).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusDays(amount)) ;
		}
	}

	@Override
	public long getPrevTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		int weekDay = time.getDayOfWeek().ordinal() + 1;
		int amount = this.amount ;
		if (amount > weekDay) {
			amount = -7 + amount - weekDay ;
		} else {
			amount = -(weekDay - amount) ;
		}
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusDays(amount).withHour(0).withMinute(0).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusDays(amount).withHour(23).withMinute(59).withSecond(59).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusDays(amount)) ;
		}
	}
	
	@Override
	public String getPeriod() {
		return PERIOD;
	}
}
