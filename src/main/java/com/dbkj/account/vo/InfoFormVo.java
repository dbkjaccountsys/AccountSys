package com.dbkj.account.vo;

public class InfoFormVo {
	
	private String username;
	private String name;
	private String phone;
	private String email;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "InfoFormVo [username=" + username + ", name=" + name + ", phone=" + phone + ", email=" + email + "]";
	}

}
