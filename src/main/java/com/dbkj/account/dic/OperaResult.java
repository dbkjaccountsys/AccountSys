package com.dbkj.account.dic;

public enum OperaResult {
	FAIL(0,"失败"),
	SUCCESS(1,"成功"),
	EXCEPTION(2,"异常");
	
	private OperaResult(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	private int value;
	private String desc;
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
