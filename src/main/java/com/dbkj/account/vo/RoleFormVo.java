package com.dbkj.account.vo;

public class RoleFormVo {

	private Long id;
	private String roleName;
	private String desc;
	private String operaAuth;
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
	public String getOperaAuth() {
		return operaAuth;
	}
	public void setOperaAuth(String operaAuths) {
		this.operaAuth = operaAuths;
	}
	@Override
	public String toString() {
		return "RoleFormVo [id=" + id + ", roleName=" + roleName + ", desc=" + desc + ", operaAuths=" + operaAuth
				+ "]";
	}
	
	
}
