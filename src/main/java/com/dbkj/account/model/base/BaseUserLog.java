package com.dbkj.account.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserLog<M extends BaseUserLog<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setUsertype(java.lang.Integer usertype) {
		set("usertype", usertype);
	}

	public java.lang.Integer getUsertype() {
		return getInt("usertype");
	}

	public void setUserid(java.lang.Long userid) {
		set("userid", userid);
	}

	public java.lang.Long getUserid() {
		return getLong("userid");
	}

	public void setTime(java.util.Date time) {
		set("time", time);
	}

	public java.util.Date getTime() {
		return get("time");
	}

	public void setIp(java.lang.String ip) {
		set("ip", ip);
	}

	public java.lang.String getIp() {
		return getStr("ip");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return getStr("content");
	}

	public void setOperaType(java.lang.Integer operaType) {
		set("opera_type", operaType);
	}

	public java.lang.Integer getOperaType() {
		return getInt("opera_type");
	}

	public void setOperaResult(java.lang.Integer operaResult) {
		set("opera_result", operaResult);
	}

	public java.lang.Integer getOperaResult() {
		return getInt("opera_result");
	}

	public void setExceptionMsg(java.lang.String exceptionMsg) {
		set("exception_msg", exceptionMsg);
	}

	public java.lang.String getExceptionMsg() {
		return getStr("exception_msg");
	}

}
