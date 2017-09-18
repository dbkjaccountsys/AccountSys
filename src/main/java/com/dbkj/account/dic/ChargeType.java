package com.dbkj.account.dic;

public enum ChargeType {
	ALIPAY(0,"支付宝"),
	WECHAT_PAYMENT(1,"微信支付"),
	BACKSTAGE(2,"后台充值");
	
	private ChargeType(int value, String desc) {
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
	
	public static ChargeType valueOf(int value){
		for(ChargeType type:values()){
			if(value==type.value){
				return type;
			}
		}
		return null;
	}
}
