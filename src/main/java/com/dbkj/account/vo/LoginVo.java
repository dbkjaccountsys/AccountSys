package com.dbkj.account.vo;

public class LoginVo {

	private String username;
	private String password;
	private String vertifyCode;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVertifyCode() {
		return vertifyCode;
	}
	public void setVertifyCode(String vertifyCode) {
		this.vertifyCode = vertifyCode;
	}
	@Override
	public String toString() {
		return "LoginVo [username=" + username + ", password=" + password + ", vertifyCode=" + vertifyCode + "]";
	}
	
	
}
