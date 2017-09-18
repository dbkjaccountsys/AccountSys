package com.dbkj.account.dic;

public enum UserType {
	USER(0,"æ™®é?šç”¨æˆ?"),
	ADMIN(1,"ç®¡ç†å‘?");
	
	
	private UserType(int value, String desc) {
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
	
	public static UserType valueOf(int value){
		for(UserType type:values()){
			if(type.getValue()==value){
				return type;
			}
		}
		return null;
	}
	
}
