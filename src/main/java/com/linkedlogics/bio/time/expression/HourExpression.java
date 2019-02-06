package com.linkedlogics.bio.time.expression;

import java.time.LocalDateTime;

public class HourExpression extends TimeExpression {
	public static final String PERIOD = "h" ;

	public HourExpression(int amount, Shift shift) {
		super(amount, shift) ;
	}

	@Override
	public long getNextTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		int amount = this.amount ;
		if (isModule) {
			int module = time.getHour() % amount ;
			amount = amount - module ;
		}

		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusHours(amount).withMinute(0).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusHours(amount).withMinute(59).withSecond(59).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusHours(amount)) ;
		}
	}

	@Override
	public long getPrevTime(long now) {
		LocalDateTime time = getLocalTime(now) ;
		int amount = this.amount ;
		if (isModule) {
			int module = time.getHour() % amount ;
			amount = module ;
		}

		switch (shift) {
		case LEFT_SHIFT:
			return getLocalTime(time.plusHours(-amount).withMinute(0).withSecond(0).withNano(0)) ;
		case RIGHT_SHIFT:
			return getLocalTime(time.plusHours(-amount).withMinute(59).withSecond(59).withNano(999000000)) ;
		case NO_SHIFT:
		default:
			return getLocalTime(time.plusHours(-amount)) ;
		}
	}

	@Override
	public String getPeriod() {
		return PERIOD;
	}
}
