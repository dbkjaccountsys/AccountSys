package com.dbkj.account.dto;

import java.io.Serializable;

public class AdminDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 723801212204498572L;
	
	private Long id;
	private String username;
	private String password;
	private String confirmPassword;
	private String name;
	private String phone;
	private String email;
	private Long roleId;
	private String roleName;
	private String createTime;
	private String opera;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
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
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getOpera() {
		return opera;
	}
	public void setOpera(String opera) {
		this.opera = opera;
	}
	@Override
	public String toString() {
		return "AdminDto [id=" + id + ", username=" + username + ", password=" + password + ", confirmPassword="
				+ confirmPassword + ", name=" + name + ", phone=" + phone + ", email=" + email + ", roleId=" + roleId
				+ ", roleName=" + roleName + ", createTime=" + createTime + ", opera=" + opera + "]";
	}
	
	
}
