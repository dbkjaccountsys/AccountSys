package com.dbkj.account.vo;

public class ForgetPasswordVo {
	
	//手机号码
	private String mobile;
	//图片验证码
	private String picCode;
	//短信验证码
	private String msgCode;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPicCode() {
		return picCode;
	}
	public void setPicCode(String picCode) {
		this.picCode = picCode;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	
}
