package com.dbkj.account.vo;

import java.io.File;

import com.jfinal.upload.UploadFile;

public class FrontUserFormVo {

	private Long id;
	private String phone;
	private String password;
	private String confirmPassword;
	private String name;
	private String email;
	private String companyName;
	private String contact;
	private String contactPhone;
	private String license;
	private String idcard;
	private String safety;
	private String accountName;
	private String publicAccount;
	private String bank;
	private String taxAccount;
	public String getPublicAccount() {
		return publicAccount;
	}
	public void setPublicAccount(String publicAccount) {
		this.publicAccount = publicAccount;
	}
	private UploadFile licenseFile;
	private UploadFile idcardFile;
	private UploadFile safetyFile;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	public String getSafety() {
		return safety;
	}
	public void setSafety(String safety) {
		this.safety = safety;
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
	public UploadFile getLicenseFile() {
		return licenseFile;
	}
	public void setLicenseFile(UploadFile licenseFile) {
		this.licenseFile = licenseFile;
	}
	public UploadFile getIdcardFile() {
		return idcardFile;
	}
	public void setIdcardFile(UploadFile idcardFile) {
		this.idcardFile = idcardFile;
	}
	public UploadFile getSafetyFile() {
		return safetyFile;
	}
	public void setSafetyFile(UploadFile safetyFile) {
		this.safetyFile = safetyFile;
	}
	@Override
	public String toString() {
		return "FrontUserFormVo [id=" + id + ", phone=" + phone + ", password=" + password + ", confirmPassword="
				+ confirmPassword + ", name=" + name + ", email=" + email + ", companyName=" + companyName
				+ ", contact=" + contact + ", contactPhone=" + contactPhone + ", license=" + license + ", idcard="
				+ idcard + ", safety=" + safety + ", accountName=" + accountName + ", publicAccount=" + publicAccount
				+ ", bank=" + bank + ", taxAccount=" + taxAccount + ", licenseFile=" + licenseFile + ", idcardFile="
				+ idcardFile + ", safetyFile=" + safetyFile + "]";
	}
	
	
}
