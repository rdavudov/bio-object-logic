package com.linkedlogics.bio.time;

import java.time.LocalDateTime;

public class SecondExpression extends TimeExpression {
	public static final String PERIOD = "s" ;
	
	public SecondExpression(int amount, Shift shift) {
		super(amount, shift) ;
	}
	
	@Override
	public long getNextTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		int amount = this.amount ;
		if (isModule) {
			int module = time.getSecond() % amount ;
			amount = amount - module ;
		}
		
		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusSeconds(amount).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusSeconds(amount).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusSeconds(amount)) ;
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
			return getLocalTime(time.plusSeconds(-amount).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusSeconds(-amount).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusSeconds(-amount)) ;
		}
	}
	
	@Override
	public String getPeriod() {
		return PERIOD;
	}
}
