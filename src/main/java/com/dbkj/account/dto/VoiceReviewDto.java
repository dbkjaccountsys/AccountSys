package com.dbkj.account.dto;

public class VoiceReviewDto {
	
	private Long id;
	private Long vid;
	public Long getVid() {
		return vid;
	}
	public void setVid(Long vid) {
		this.vid = vid;
	}
	private String username;
	private String updateTime;
	private String fileName;
	private String filePath;
	private String voiceName;
	private String content;
	private String status;
	private String checkTime;
	private String checkUser;
	private String opera;
	private String reason;
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
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getVoiceName() {
		return voiceName;
	}
	public void setVoiceName(String voiceName) {
		this.voiceName = voiceName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
	public String getCheckUser() {
		return checkUser;
	}
	public void setCheckUser(String checkUser) {
		this.checkUser = checkUser;
	}
	public String getOpera() {
		return opera;
	}
	public void setOpera(String opera) {
		this.opera = opera;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	@Override
	public String toString() {
		return "VoiceReviewDto [id=" + id + ", vid=" + vid + ", username=" + username + ", updateTime=" + updateTime
				+ ", fileName=" + fileName + ", filePath=" + filePath + ", voiceName=" + voiceName + ", content="
				+ content + ", status=" + status + ", checkTime=" + checkTime + ", checkUser=" + checkUser + ", opera="
				+ opera + ", reason=" + reason + "]";
	}
	
	
	
	
	
	
}
