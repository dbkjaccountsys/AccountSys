package com.dbkj.account.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserRoleAuth<M extends BaseUserRoleAuth<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setRoleId(java.lang.Long roleId) {
		set("role_id", roleId);
	}

	public java.lang.Long getRoleId() {
		return getLong("role_id");
	}

	public void setAuthId(java.lang.Long authId) {
		set("auth_id", authId);
	}

	public java.lang.Long getAuthId() {
		return getLong("auth_id");
	}

	public void setAvailable(java.lang.Boolean available) {
		set("available", available);
	}

	public java.lang.Boolean getAvailable() {
		return get("available");
	}

}
