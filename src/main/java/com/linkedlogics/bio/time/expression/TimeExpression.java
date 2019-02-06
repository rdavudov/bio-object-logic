package com.linkedlogics.bio.time.expression;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Basis for supported time manipulation expressions
 * @author rajab
 *
 */
public abstract class TimeExpression {
    protected int amount;
    protected Shift shift;
    protected boolean isNegative;
    protected boolean isModule ;

    public TimeExpression(int amount, Shift shift) {
        this.amount = amount;
        this.shift = shift;
    }

    public abstract long getNextTime(long now);

    public abstract long getPrevTime(long now);
    
    protected LocalDateTime getLocalTime(long now) {
    	return Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDateTime() ;
    }
    
    protected long getLocalTime(LocalDateTime time) {
    	return time.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() ;
    }

    public long getTime(long now) {
        if (isNegative) {
            return getPrevTime(now);
        } else {
            return getNextTime(now);
        }
    }

    public enum Shift {
        NO_SHIFT,
        LEFT_SHIFT,
        RIGHT_SHIFT;
    }
    public boolean isNegative() {
        return isNegative;
    }

    public void setNegative(boolean isNegative) {
        this.isNegative = isNegative;
    }
    
    public boolean isModule() {
		return isModule;
	}

	public void setModule(boolean isModule) {
		this.isModule = isModule;
	}

	public abstract String getPeriod();

    public String toString() {
        StringBuilder s = new StringBuilder();
        if (isNegative) {
            s.append("-");
        }
        s.append(amount);
        s.append(getPeriod());
        switch (shift) {
            case LEFT_SHIFT:
                s.append("<");
                break;
            case RIGHT_SHIFT:
                s.append(">");
                break;
            case NO_SHIFT:
            	break;
        }
        return s.toString();
    }
}
