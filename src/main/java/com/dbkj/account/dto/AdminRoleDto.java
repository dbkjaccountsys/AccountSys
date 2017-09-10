package com.dbkj.account.dto;

import java.io.Serializable;

public class AdminRoleDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2849732001221037028L;
	
	private Long id;
	private String roleName;
	private String desc;
	private String opera;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getOpera() {
		return opera;
	}
	public void setOpera(String opera) {
		this.opera = opera;
	}
	@Override
	public String toString() {
		return "AdminRoleDto [id=" + id + ", roleName=" + roleName + ", desc=" + desc + ", opera=" + opera + "]";
	}
	
	
}
