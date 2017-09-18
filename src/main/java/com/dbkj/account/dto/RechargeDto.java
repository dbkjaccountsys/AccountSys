package com.dbkj.account.dto;

import java.io.Serializable;

public class RechargeDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4839571533577642730L;

	private Long id;
	private String username;
	private String companyName;
	private double charge;
	private String opera;
	private String chargeAmount;
	private String actualAmount; 
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
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public double getCharge() {
		return charge;
	}
	public void setCharge(double charge) {
		this.charge = charge;
	}
	public String getOpera() {
		return opera;
	}
	public void setOpera(String opera) {
		this.opera = opera;
	}
	
	public String getChargeAmount() {
		return chargeAmount;
	}
	public void setChargeAmount(String chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	public String getActualAmount() {
		return actualAmount;
	}
	public void setActualAmount(String actualAmount) {
		this.actualAmount = actualAmount;
	}
	@Override
	public String toString() {
		return "RechargeDto [id=" + id + ", username=" + username + ", companyName=" + companyName + ", charge="
				+ charge + ", opera=" + opera + ", chargeAmount=" + chargeAmount + ", actualAmount=" + actualAmount
				+ "]";
	}
	
	
	
}
