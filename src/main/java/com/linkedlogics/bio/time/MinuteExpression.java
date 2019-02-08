package com.linkedlogics.bio.time;

import java.time.LocalDateTime;

public class MinuteExpression extends TimeExpression {
	public static final String PERIOD = "m" ;
	
	public MinuteExpression(int amount, Shift shift) {
		super(amount, shift) ;
	}
	
	@Override
	public long getNextTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		int amount = this.amount ;
		if (isModule) {
			int module = time.getMinute() % amount ;
			amount = amount - module ;
		}
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusMinutes(amount).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusMinutes(amount).withSecond(59).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusMinutes(amount)) ;
		}
	}

	@Override
	public long getPrevTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		int amount = this.amount ;
		if (isModule) {
			int module = time.getMinute() % amount ;
			amount =  module ;
		}
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusMinutes(-amount).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusMinutes(-amount).withSecond(59).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusMinutes(-amount)) ;
		}
	}
	
	@Override
	public String getPeriod() {
		return PERIOD;
	}
}
