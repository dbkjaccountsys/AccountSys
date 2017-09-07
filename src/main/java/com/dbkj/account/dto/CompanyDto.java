package com.dbkj.account.dto;

public class CompanyDto {
	
	public CompanyDto(){
		
	}
	
	public CompanyDto(Long userId, String companyName) {
		super();
		this.userId = userId;
		this.companyName = companyName;
	}
	private Long userId;
	private String companyName;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	@Override
	public String toString() {
		return "CompanyDto [userId=" + userId + ", companyName=" + companyName + "]";
	}
	
	
}
