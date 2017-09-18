package com.dbkj.account.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserInfo<M extends BaseUserInfo<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setUserid(java.lang.Long userid) {
		set("userid", userid);
	}

	public java.lang.Long getUserid() {
		return getLong("userid");
	}

	public void setCompanyname(java.lang.String companyname) {
		set("companyname", companyname);
	}

	public java.lang.String getCompanyname() {
		return getStr("companyname");
	}

	public void setContact(java.lang.String contact) {
		set("contact", contact);
	}

	public java.lang.String getContact() {
		return getStr("contact");
	}

	public void setContactphone(java.lang.String contactphone) {
		set("contactphone", contactphone);
	}

	public java.lang.String getContactphone() {
		return getStr("contactphone");
	}

	public void setPublicaccount(java.lang.String publicaccount) {
		set("publicaccount", publicaccount);
	}

	public java.lang.String getPublicaccount() {
		return getStr("publicaccount");
	}

	public void setLicence(java.lang.String licence) {
		set("licence", licence);
	}

	public java.lang.String getLicence() {
		return getStr("licence");
	}

	public void setIdcard(java.lang.String idcard) {
		set("idcard", idcard);
	}

	public java.lang.String getIdcard() {
		return getStr("idcard");
	}

	public void setSafety(java.lang.String safety) {
		set("safety", safety);
	}

	public java.lang.String getSafety() {
		return getStr("safety");
	}

	public void setIspass(java.lang.Integer ispass) {
		set("ispass", ispass);
	}

	public java.lang.Integer getIspass() {
		return getInt("ispass");
	}

	public void setChecktime(java.util.Date checktime) {
		set("checktime", checktime);
	}

	public java.util.Date getChecktime() {
		return get("checktime");
	}

	public void setCheckuser(java.lang.Long checkuser) {
		set("checkuser", checkuser);
	}

	public java.lang.Long getCheckuser() {
		return getLong("checkuser");
	}

	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}

	public java.lang.String getRemark() {
		return getStr("remark");
	}

	public void setAppId(java.lang.String appId) {
		set("app_id", appId);
	}

	public java.lang.String getAppId() {
		return getStr("app_id");
	}

	public void setAppSecret(java.lang.String appSecret) {
		set("app_secret", appSecret);
	}

	public java.lang.String getAppSecret() {
		return getStr("app_secret");
	}

	public void setAvailable(java.lang.Boolean available) {
		set("available", available);
	}

	public java.lang.Boolean getAvailable() {
		return get("available");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setModifyTime(java.util.Date modifyTime) {
		set("modify_time", modifyTime);
	}

	public java.util.Date getModifyTime() {
		return get("modify_time");
	}

	public void setAccountname(java.lang.String accountname) {
		set("accountname", accountname);
	}

	public java.lang.String getAccountname() {
		return getStr("accountname");
	}

	public void setBank(java.lang.String bank) {
		set("bank", bank);
	}

	public java.lang.String getBank() {
		return getStr("bank");
	}

	public void setTaxaccount(java.lang.String taxaccount) {
		set("taxaccount", taxaccount);
	}

	public java.lang.String getTaxaccount() {
		return getStr("taxaccount");
	}

}
