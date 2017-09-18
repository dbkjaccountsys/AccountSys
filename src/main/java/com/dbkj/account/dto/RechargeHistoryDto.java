package com.dbkj.account.dto;

public class RechargeHistoryDto {
	
	private Long id;
	private Long userId;
	private String username;
	private String companyName;
	private String time;
	private double charge;
	private double realcharge;
	private String chargeType;
	private String serialNum;
	private String chargeUser;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public void setUsername(String userName) {
		this.username = userName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getCharge() {
		return charge;
	}
	public void setCharge(double charge) {
		this.charge = charge;
	}
	public double getRealcharge() {
		return realcharge;
	}
	public void setRealcharge(double realcharge) {
		this.realcharge = realcharge;
	}
	public String getChargeType() {
		return chargeType;
	}
	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getChargeUser() {
		return chargeUser;
	}
	public void setChargeUser(String chargeUser) {
		this.chargeUser = chargeUser;
	}
	@Override
	public String toString() {
		return "RechargeHistoryDto [id=" + id + ", userId=" + userId + ", userName=" + username + ", companyName="
				+ companyName + ", time=" + time + ", charge=" + charge + ", realcharge=" + realcharge + ", chargeType="
				+ chargeType + ", serialNum=" + serialNum + ", chargeUser=" + chargeUser + "]";
	}
	
	

}
