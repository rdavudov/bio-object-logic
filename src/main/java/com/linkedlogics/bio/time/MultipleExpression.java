package com.linkedlogics.bio.time;

import java.util.ArrayList;

public class MultipleExpression {
	private ArrayList<TimeExpression> expressionList = new ArrayList<TimeExpression>() ;
	
	public void addExpression(TimeExpression expression) {
		expressionList.add(expression) ;
	}
	
	public long getTime(long now) {
		long time = now ;
		for (int i = 0; i < expressionList.size() ; i++) {
			time = expressionList.get(i).getTime(time) ;
		}
		return time;
	}

}
