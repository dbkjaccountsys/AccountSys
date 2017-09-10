package com.dbkj.account.dto;

import java.io.Serializable;

public class FrontUserDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4353909419769911529L;
	
	private Long id;
	private String username;
	private String name;
	private String phone;
	private String email;
	private Double charge;
	private String companyName;
	private String contact;
	private String contactPhone;
	private String accountName;
	private String publicAccount;
	private String bank;
	private String taxAccount;
	private String ispass;
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
	public Double getCharge() {
		return charge;
	}
	public void setCharge(Double charge) {
		this.charge = charge;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
	public String getPublicAccount() {
		return publicAccount;
	}
	public void setPublicAccount(String publicAccount) {
		this.publicAccount = publicAccount;
	}
	public String getIspass() {
		return ispass;
	}
	public void setIspass(String ispass) {
		this.ispass = ispass;
	}
	public String getOpera() {
		return opera;
	}
	public void setOpera(String opera) {
		this.opera = opera;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getTaxAccount() {
		return taxAccount;
	}
	public void setTaxAccount(String taxAccount) {
		this.taxAccount = taxAccount;
	}
	@Override
	public String toString() {
		return "FrontUserDto [id=" + id + ", username=" + username + ", name=" + name + ", phone=" + phone + ", email="
				+ email + ", charge=" + charge + ", companyName=" + companyName + ", contact=" + contact
				+ ", contactPhone=" + contactPhone + ", accountName=" + accountName + ", publicAccount=" + publicAccount
				+ ", bank=" + bank + ", taxAccount=" + taxAccount + ", ispass=" + ispass + ", opera=" + opera + "]";
	}
	
	
}
