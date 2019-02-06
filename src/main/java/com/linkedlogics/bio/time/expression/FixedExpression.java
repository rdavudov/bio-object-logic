package com.linkedlogics.bio.time.expression;

public class FixedExpression extends TimeExpression {
	public static final String PERIOD = "=" ;
	
	private long fixedDate ;
	
	public FixedExpression(long fixedDate) {
		super(0, Shift.NO_SHIFT) ;
		this.fixedDate = fixedDate ;
	}
	
	@Override
	public long getNextTime(long now) {
		return fixedDate ;
	}

	@Override
	public long getPrevTime(long now) {
		return fixedDate ;
	}

	@Override
	public String getPeriod() {
		return PERIOD;
	}
}
