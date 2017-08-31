package com.dbkj.account.dic;

public enum ActionType {
	GET(1),//
	POST(2),
	BOTH(3);
	
	private ActionType(int value) {
		this.value = value;
	}

	private int value;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
