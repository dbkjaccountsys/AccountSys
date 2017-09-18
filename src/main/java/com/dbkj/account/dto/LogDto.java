package com.dbkj.account.dto;

public class LogDto {

	private Long id;
	private String userType;
	private Long userId;
	private String username;
	private String time;
	private String ip;
	private String content;
	private String operaType;
	private String operaResult;
	private String exceptionMsg;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOperaType() {
		return operaType;
	}
	public void setOperaType(String operaType) {
		this.operaType = operaType;
	}
	public String getOperaResult() {
		return operaResult;
	}
	public void setOperaResult(String operaResult) {
		this.operaResult = operaResult;
	}
	public String getExceptionMsg() {
		return exceptionMsg;
	}
	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	@Override
	public String toString() {
		return "LogDto [id=" + id + ", userType=" + userType + ", userId=" + userId + ", username=" + username
				+ ", time=" + time + ", ip=" + ip + ", content=" + content + ", operaType=" + operaType
				+ ", operaResult=" + operaResult + ", exceptionMsg=" + exceptionMsg + "]";
	}
	
	
}
