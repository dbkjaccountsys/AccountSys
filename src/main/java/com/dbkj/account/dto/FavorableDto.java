package com.dbkj.account.dto;

import java.io.Serializable;

public class FavorableDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 499857078153204495L;
	
	private Long id;
	private String name;
	private String beginTime;
	private String endTime;
	private String status;
	private Float rate;
	private Double mincharge;
	private Double maxcharge;
	private String opera;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Float getRate() {
		return rate;
	}
	public void setRate(Float rate) {
		this.rate = rate;
	}
	public Double getMincharge() {
		return mincharge;
	}
	public void setMincharge(Double mincharge) {
		this.mincharge = mincharge;
	}
	public Double getMaxcharge() {
		return maxcharge;
	}
	public void setMaxcharge(Double maxcharge) {
		this.maxcharge = maxcharge;
	}
	public String getOpera() {
		return opera;
	}
	public void setOpera(String opera) {
		this.opera = opera;
	}
	@Override
	public String toString() {
		return "FavorableDto [id=" + id + ", name=" + name + ", beginTime=" + beginTime + ", endTime=" + endTime
				+ ", status=" + status + ", rate=" + rate + ", mincharge=" + mincharge + ", maxcharge=" + maxcharge
				+ ", opera=" + opera + "]";
	}
	
	
	
}
