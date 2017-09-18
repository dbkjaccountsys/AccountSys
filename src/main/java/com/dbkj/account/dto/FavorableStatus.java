package com.dbkj.account.dto;

/**
 * 活动状态
 * @author DELL
 *
 */
public enum FavorableStatus {
	DISABLE(0),
	ENABLE(1);
	
	private FavorableStatus(int value) {
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
